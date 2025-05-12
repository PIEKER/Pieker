package pieker.evaluator;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import pieker.api.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Setter
@Slf4j
public class Evaluator {

    private Map<String, JSONObject> componentMap = new HashMap<>();

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

    /**
     * Runs a concurrent evaluation of a provided assertion list. Depending on the systems-available thread count,
     * the evaluator addresses timing challenges of assertAfter configuration.
     *
     * @param assertionList list of assertion objects
     * @param timeout maximum evaluation time.
     */
    public void run(List<Assertions> assertionList, long timeout){
        int maxThreads = Runtime.getRuntime().availableProcessors() * 2; // or any other system-safe number
        log.debug("Thread limit: {}", maxThreads);

        ExecutorService threadPool = Executors.newFixedThreadPool(maxThreads);

        CountDownLatch latch = new CountDownLatch(assertionList.size());

        for (Assertions assertion : assertionList) {
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

    private void evaluateStep(Assertions ass){

        if (ass.requiresConnectionParam()) {
            ass.setupConnectionParam(this.componentMap.get(ass.getIdentifier()));
        }

        ass.evaluate();
    }
}
