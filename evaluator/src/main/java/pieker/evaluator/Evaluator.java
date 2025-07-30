package pieker.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.api.Assertion;
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
public class Evaluator {
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String OUTPUT_DIR = System.getProperty("genDir", ".gen/");
    private String gatewayUrl;
    private Map<String, Map<String, File>> fileMap = new HashMap<>();

    public Evaluator() {
        this.gatewayUrl = "http://"
                + System.getProperty("orchestratorHost", "127.0.0.1") + ":"
                + System.getProperty("orchestratorPort", "42690");
    }

    public void preprocessFiles(String path, String fileSuffix) {
        this.fileMap.put(fileSuffix, this.collectFiles(new File(path), fileSuffix));
    }

    /**
     * Runs a concurrent evaluation of a provided assertion list. Depending on the systems-available thread count,
     * the evaluator addresses timing challenges of assertAfter configuration.
     *
     * @param assertionList list of assertion objects
     * @param timeout       maximum evaluation time.
     */
    public void run(List<Assertion> assertionList, long timeout) {
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
                log.debug("All tasks completed. Shutting down.");
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

    public void publishResult(ScenarioTestPlan scenario) {
        RunDto result = scenario.createTestRunDto();
        ObjectMapper om = new ObjectMapper();
        try {
            final String path = PROJECT_ROOT + File.separator + OUTPUT_DIR + scenario.getName();
            Files.createDirectories(Path.of(path));
            // Convert an object to a JSON file
            om.writerWithDefaultPrettyPrinter().writeValue(
                    new File(path, "result.json"), result);
            log.info("Result JSON file created successfully for {}: {}", scenario.getName(), path + "/result.json");

            boolean publishToServer = Boolean.parseBoolean(System.getProperty("publishToServer", "false"));
            if (publishToServer) {
                String resultJson = om.writeValueAsString(result);
                String header = "{ \"Content-Type\": \"application/json\", \"Accept\": \"application/json\" }";
                String url = System.getProperty("publishUrl", "http://localhost:8080/runs/create");
                log.info("publishing results to server: {}", url);
                String response = Http.send("EVALUATOR", url, "POST", 3000, 30000, header, resultJson, "json").response();
                log.info("published with response {}", response);
            }
        } catch (IOException e) {
            log.error("unable to create JSON file for {}. Error: {}", scenario.getName(), e.getMessage());
        }

    }

    private Map<String, File> collectFiles(File directory, String fileSuffix) {
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

    private void evaluateStep(Assertion ass) {

        if (ass.requiresConnectionParam()) {
            ass.setConnectionParam(this.gatewayUrl);
        }

        ass.setFileMap(this.fileMap);
        ass.evaluate();
    }
}
