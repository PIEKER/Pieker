package pieker.supervisor.compose;

import lombok.extern.slf4j.Slf4j;
import pieker.architectures.common.model.HttpLink;
import pieker.architectures.compose.model.ComposeArchitectureModel;
import pieker.architectures.compose.model.ComposeComponent;
import pieker.architectures.compose.model.ComposeService;
import pieker.architectures.model.Link;
import pieker.common.*;
import pieker.supervisor.AbstractSupervisor;
import pieker.supervisor.Supervisor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.NoSuchElementException;

/**
 * PIEKER Supervisor for Docker Compose architectures.
 */
@Slf4j
public class ComposeSupervisor extends AbstractSupervisor<ComposeArchitectureModel> implements Supervisor<ComposeArchitectureModel> {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String DEFAULT_ENDPOINT = "Default";

    private static final String EXECUTION_NAME = System.getProperty("scenarioName");
    private static final String PROJECT_ROOT = System.getProperty("projectRoot");
    private static final String GEN_DIR = PROJECT_ROOT + System.getProperty("genDir");
    private static final String EXECUTION_DIR = GEN_DIR + EXECUTION_NAME + File.separator;
    private static final String COMPOSE_FILE = EXECUTION_DIR + "docker-compose.yml";
    private static final String COMPOSE_START_CMD = "docker-compose -f %s up -d".formatted(COMPOSE_FILE);
    private static final String COMPOSE_STOP_CMD = "docker-compose -f %s stop".formatted(COMPOSE_FILE);
    private static final String COMPOSE_DESTROY_CMD = "docker-compose -f %s down".formatted(COMPOSE_FILE);

    public ComposeSupervisor(ScenarioTestPlan testPlan, ComposeArchitectureModel model) {
        setTestPlan(testPlan);
        setModel(model);
    }

    @Override
    public void setupTestEnvironment() {
        log.info("Setting up test environment ...");
        setStatus(Status.SETUP);
        try {
            runCommand(COMPOSE_START_CMD);
        } catch (IOException | RuntimeException e) {
            log.error("Failed to start Docker Compose system: {}", e.getMessage());
            setStatus(Status.ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Start command interrupted: {}", e.getMessage());
        }
        sleep(15000); // Sleep for 15 seconds to allow the system to start
    }

    @Override
    public void stopTestEnvironment() {
        log.info("Stopping test environment ...");
        setStatus(Status.STOPPED);
        try {
            runCommand(COMPOSE_STOP_CMD);
        } catch (IOException | RuntimeException e) {
            log.error("Failed to stop Docker Compose system: {}", e.getMessage());
            setStatus(Status.ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Stop command interrupted: {}", e.getMessage());
        }
    }

    @Override
    public void destroyTestEnvironment() {
        log.info("Shutting down test environment ...");
        setStatus(Status.SHUTDOWN);
        try {
            runCommand(COMPOSE_DESTROY_CMD);
        } catch (IOException | RuntimeException e) {
            log.error("Failed to shut down Docker Compose system: {}", e.getMessage());
            setStatus(Status.ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Shutdown command interrupted: {}", e.getMessage());
        }
    }

    /**
     * Runs a command in the system shell (Linux: bash, Windows: cmd.exe).
     *
     * @param command the command to run
     * @throws InterruptedException if the process is interrupted
     * @throws IOException          if an I/O error occurs
     * @throws RuntimeException     if the command fails
     */
    private void runCommand(String command) throws InterruptedException, IOException, RuntimeException {
        ProcessBuilder processBuilder = new ProcessBuilder();

        final String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            command = command.replace('/', '\\');
            log.debug("Executing command on Windows: {}", command);
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            command = command.replace('\\', '/');
            log.debug("Executing command on Linux: {}", command);
            processBuilder.command("bash", "-c", command);
        }

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Failed to run command. Process exited with code: " + exitCode);
        }
    }

    @Override
    public void executeTests() {
        for (String testStepId : getTestPlan().getStepIds()) {
            final long testDurationMs = getTestPlan().getStepToDurationMap().get(testStepId);
            executeTestStep(testStepId, testDurationMs);
        }

        if (getStatus() != Status.ERROR) {
            setStatus(Status.FINISHED);
        }
    }

    @Override
    public void executeTestStep(String testStepId, long duration) {

        if (getStatus() == Status.ERROR) {
            return;
        }
        setStatus(Status.RUNNING);

        final TestStep testStep = getTestPlan().getTestSteps().stream()
                .filter(step -> step.getId().equals(testStepId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Test step not found: " + testStepId));

        log.info("Changing PIEKER component behavior...");
        setComponentBehaviorForTestStep(testStepId);
        log.info("Executing test step: {}", testStepId);

        // Execute Before-Each
        TestStep beforeEachStep = getTestPlan().getBeforeEachStep();
        if (beforeEachStep != null) {
            log.info("Executing before each steps...");
            beforeEachStep.getSequence();
        }

        sleep(500); // Sleep for 0.5 seconds to allow for component behavior changes

        // Execute actual test step
        log.info("Starting test sequence...");
        for (TrafficTemplate trafficTemplate : testStep.getSequence()) {
            ComposeComponent component;
            Link<ComposeComponent> link;
            try {
                component = getModel().getComponent(trafficTemplate.getTarget()).orElseThrow();
                link = getModel().getLinksForTarget(component).getFirst();
            } catch (NoSuchElementException e) {
                log.warn("No link found for target component: {}", trafficTemplate.getTarget());
                continue;
            }
            handleComposeTraffic(trafficTemplate, component, link);
        }

        sleep(duration); // Sleep for the duration of the test step
    }

    private void handleComposeTraffic(TrafficTemplate trafficTemplate, ComposeComponent component, Link<ComposeComponent> link) {
        switch (link) {
            case HttpLink<ComposeComponent> _ -> {
                ComposeService service = (ComposeService) component;
                // FIXME: Implement to handle port properly (multiple port mappings, etc.)
                trafficTemplate.startTraffic(new String[]{
                        "http://%s:%s".formatted(
                                System.getProperty("systemHost", "localhost"),
                                service.getPorts().entrySet().iterator().next().getKey()
                        )
                });
            }
            default -> log.warn("Unsupported traffic type: {}", link.getType());
        }
    }

    private void setComponentBehaviorForTestStep(String testStepId) {
        boolean success = true;
        // Change proxy behavior if required
        for (ScenarioProxyComponent scenarioProxyComponent : getTestPlan().getProxyComponents()) {
            if (scenarioProxyComponent.getStepToConditionMap().containsKey(testStepId)) {
                success &= sendRequestToComponent(scenarioProxyComponent.getName(), testStepId);
            } else {
                success &= sendRequestToComponent(scenarioProxyComponent.getName(), DEFAULT_ENDPOINT);
            }
        }
        // Change traffic behavior if required
        for (ScenarioTrafficComponent scenarioTrafficComponent : getTestPlan().getTrafficComponents()) {
            if (scenarioTrafficComponent.getStepToTrafficMap().containsKey(testStepId)) {
                success &= sendRequestToComponent(scenarioTrafficComponent.getName(), testStepId);
            } else {
                success &= sendRequestToComponent(scenarioTrafficComponent.getName(), DEFAULT_ENDPOINT);
            }
        }

        if (!success) {
            log.error("Failed to set component behavior for test step: {}", testStepId);
            setStatus(Status.ERROR);
        }
    }

    private boolean sendRequestToComponent(String componentName, String endpoint) {
        final String host = System.getProperty("systemHost", "127.0.0.1");
        final String supervisorProxyPort = System.getProperty("supervisorPort", "42690");
        return sendGetRequest("http://%s:%s/http/%s/%s".formatted(host, supervisorProxyPort, componentName, endpoint));
    }

    /**
     * Sends a GET request to the specified URL and returns true if the response status code is 200.
     *
     * @param url the URL to send the request to
     * @return true if the response status code is 200, false otherwise
     */
    private boolean sendGetRequest(String url) {
        log.debug("Sending GET request to: {}", url);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

        HttpResponse<String> response;
        try {
            response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Error while sending request: {}", e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Request interrupted: {}", e.getMessage());
            return false;
        }

        if (response.statusCode() != 200) {
            log.error("Received Error Response ({}) from {}", response.statusCode(), url);
            return false;
        }

        return true;
    }

    /**
     * Sleeps for the specified duration.
     *
     * @param duration the duration to sleep in milliseconds
     */
    private void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error while waiting for test execution to finish: {}", e.getMessage());
        }
    }

}
