package pieker.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Assertion;
import pieker.common.ScenarioTestPlan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Setter
@Slf4j
public class Evaluator {

    private static final String OUTPUT_DIR = System.getProperty("genDir", ".gen/");
    private Map<String, JSONObject> componentMap = new HashMap<>();
    private Map<String, Map<String, File>> fileMap = new HashMap<>();

    public Evaluator(){}

    public Evaluator(Map<String, JSONObject> componentMap){
        this.componentMap = componentMap;
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

    public void generateResultJson(ScenarioTestPlan scenario){
        ObjectMapper om = new ObjectMapper();
        try {
            String path = OUTPUT_DIR + scenario.getName();
            Files.createDirectories(Path.of(path));

            // Convert an object to a JSON file
            om.writerWithDefaultPrettyPrinter().writeValue(
                    new File(path, "result.json"), scenario.createTestRunDto());
            log.info("JSON file created successfully for {}!", scenario.getName());
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

}
