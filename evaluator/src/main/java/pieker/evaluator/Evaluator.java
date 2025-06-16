package pieker.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Assertion;
import pieker.architectures.common.model.JdbcLink;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.architectures.model.Link;
import pieker.common.ScenarioTestPlan;
import pieker.common.connection.Http;
import pieker.common.dto.RunDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

@Setter
@Slf4j
@NoArgsConstructor
public class Evaluator {

    private static final String OUTPUT_DIR = System.getProperty("genDir", ".gen/");
    private Map<String, JSONObject> componentMap = new HashMap<>();
    private Map<String, Map<String, File>> fileMap = new HashMap<>();

    public Evaluator(ArchitectureModel<Component> architectureModel, ScenarioTestPlan testPlan){
        testPlan.getAssertableComponents().forEach(name -> {
            try {
                Component c = architectureModel.getComponent(name).orElseThrow();
                Collection<Link<Component>> linkCollection = architectureModel.getLinksForTarget(c);
                assert !linkCollection.isEmpty();
                Link<Component> link = linkCollection.stream().toList().getFirst();

                switch (link.getType()){
                    case JDBC -> {
                        JdbcLink<Component> jdbcLink = (JdbcLink<Component>) link;
                        log.debug("detected a jdbc link: {}", jdbcLink.getJdbcUrl());
                        JSONObject o = new JSONObject();
                        o.put("targetUrlEnv", this.getJdbcBaseUrl(jdbcLink.getJdbcUrl()));
                        o.put("usernameEnv", jdbcLink.getUsername());
                        o.put("passwordEnv", jdbcLink.getPassword());
                        this.addComponent(name, o);
                    }
                    default -> log.warn("unidentified link detected: {}", link.getType());
                }
            } catch (NoSuchElementException | AssertionError e){
                log.error(e.getMessage());
            }
        });
    }

    /**
     * Adds a JSONObject storing connection attributes mapped to a component identifier.
     *
     * @param identifier of component
     * @param component JSONObject
     */
    public void addComponent(String identifier, JSONObject component){
        this.componentMap.put(identifier, component);
    }

    public void preprocessFiles(String path, String fileSuffix){
        this.fileMap.put(fileSuffix, this.collectFiles(new File(path), fileSuffix));
    }

    /**
     * Runs a concurrent evaluation of a provided assertion list. Depending on the systems-available thread count,
     * the evaluator addresses timing challenges of assertAfter configuration.
     *
     * @param assertionList list of assertion objects
     * @param timeout maximum evaluation time.
     */
    public void run(List<Assertion> assertionList, long timeout){
        int maxThreads = Runtime.getRuntime().availableProcessors();
        log.debug("Thread limit: {}", maxThreads);

        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);

        CountDownLatch latch = new CountDownLatch(assertionList.size());

        for (Assertion assertion : assertionList) {
            threadPool.submit(() -> {
                try {
                    log.debug("[START] {}", assertion.getIdentifier());
                    this.evaluateStep(assertion);
                    log.info("[FINISHED] {}", assertion.getIdentifier());
                } catch (Exception e) {
                    log.error("Error in task {}: {}", assertion.getIdentifier(), e.getMessage(), e);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Wait for all tasks to finish
        new Thread(() -> {
            try {
                latch.await(); // Blocks until all tasks finish
                log.info("All tasks completed. Shutting down.");
                threadPool.shutdown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while waiting for task completion: {}", e.getMessage());
            }
        }).start();

        try {
            threadPool.awaitTermination(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("Evaluation interrupted {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void publishResult(ScenarioTestPlan scenario){
        RunDto result = scenario.createTestRunDto();
        ObjectMapper om = new ObjectMapper();
        try {
            String path = OUTPUT_DIR + scenario.getName();
            Files.createDirectories(Path.of(path));
            // Convert an object to a JSON file
            om.writerWithDefaultPrettyPrinter().writeValue(
                    new File(path, "result.json"), result);
            log.info("JSON file created successfully for {}!", scenario.getName());

            boolean publishToServer = Boolean.parseBoolean(System.getProperty("publishToServer", "false"));
            if (publishToServer){
                String resultJson = om.writeValueAsString(result);
                String header = "{ \"Content-Type\": \"application/json\", \"Accept\": \"application/json\" }";
                String url = System.getProperty("publishUrl", "http://localhost:8080/runs/create");
                log.info("publishing results to server: {}", url);
                String response = Http.send("EVALUATOR", url, "POST", 3000, 30000, header, resultJson);
                log.info("published with response {}", response);
            }
        } catch (IOException e) {
            log.error("unable to create JSON file for {}. Error: {}", scenario.getName(), e.getMessage());
        }

    }

    private Map<String, File> collectFiles(File directory, String fileSuffix){
        Map<String, File> logFiles = new HashMap<>();
        if (directory == null || !directory.exists()) {
            log.info("Directory does not exist.");
            return logFiles;
        }

        File[] files = directory.listFiles();
        if (files == null) return logFiles;

        for (File file : files) {
            if (file.isDirectory()) {
                logFiles.putAll(collectFiles(file, fileSuffix));
            } else if (file.getName().toLowerCase().endsWith(fileSuffix)) {
                logFiles.put(file.getName(), file);
            }
        }

        return logFiles;
    }

    private void evaluateStep(Assertion ass){

        if (ass.requiresConnectionParam()) {
            ass.setConnectionParam(this.componentMap.get(ass.getIdentifier()));
        }

        ass.setFileMap(this.fileMap);
        ass.evaluate();
    }

    /**
     * Extracts the base URL from a JDBC URL (remove DB name).
     *
     * @param jdbcUrl The raw JDBC URL
     * @return The base URL (protocol + host + port)
     */
    private String getJdbcBaseUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return "";
        }
        int indexPrefix = jdbcUrl.indexOf("://");
        if (indexPrefix == -1) {
            return jdbcUrl; // No protocol found, return as is
        }
        String prefix = jdbcUrl.substring(0, indexPrefix + 3);
        String suffix = jdbcUrl.substring(indexPrefix + 3);
        int indexHostEnd = suffix.indexOf('/');
        return prefix + suffix.substring(0, indexHostEnd);
    }

}
