package pieker.dsl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pieker.common.*;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.template.condition.*;
import pieker.dsl.model.Feature;
import pieker.dsl.parser.FeatureParser;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


class DslTest {

    static Path resourceDir;
    static Feature feature = null;

    static {
        try {
            resourceDir = Paths.get(Objects.requireNonNull(DslTest.class.getClassLoader().getResource("input")).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void parseFeature() {
        String inputFile = "all-valid.feature";
        feature = new Feature(inputFile, resourceDir.toString());
        assertDoesNotThrow(() -> FeatureParser.parse(feature, true));
        assertDoesNotThrow(() -> Engine.run(feature));
    }

    @Test
    void testValidScenario(){

        List<ScenarioTestPlan> tpList = feature.getScenarioTestPlanList();
        assertFalse(tpList.isEmpty());

        int counter = 0;
        for (ScenarioTestPlan tp : tpList) {
            assertTrue(tp.getName().contains("BeforeEach"));
            if (counter < 4){
                verifyProxySetup(tp);
            } else {
                verifyTrafficSetup(tp);
            }
            ++counter;
        }
    }

    private void verifyTrafficSetup(ScenarioTestPlan tp) {
        assertTrue(tp.getName().contains("All_Traffics_"));

        Collection<ScenarioTrafficComponent> trafficComponents = tp.getTrafficComponents();
        assertFalse(trafficComponents.isEmpty());

        for (String stepId : tp.getStepIds()) {
            boolean passiveRequestExists = false, passiveSqlExists = false;

            for (ScenarioTrafficComponent trafficComponent : trafficComponents) {
                String name = trafficComponent.getName();
                passiveRequestExists = !passiveRequestExists ? name.equals("PIEKER_TRAFFIC_service_c") : passiveRequestExists;
                passiveSqlExists = !passiveSqlExists ? name.equals("PIEKER_TRAFFIC_db") : passiveRequestExists;

                for (TrafficTemplate trafficTemplate : trafficComponent.getStepToTrafficMap().get(stepId)) {
                    String identifier = trafficTemplate.getIdentifier();
                    if (passiveRequestExists && !passiveSqlExists){
                        assertEquals("passive-incr-counter", identifier);
                    } else if (passiveSqlExists) {
                        assertEquals("passive-db", identifier);
                    } else {
                        throw new AssertionError("Component with unexpected identifier encountered.");
                    }

                    if (stepId.contains("No_Condition")){
                        assertTrue(trafficTemplate.getConditionList().isEmpty());
                    } else {
                        assertEquals(5, trafficTemplate.getConditionList().size());
                        for (ConditionTemplate conditionTemplate : trafficTemplate.getConditionList()) {
                            switch (conditionTemplate){
                                case Times times -> assertEquals(20, times.getAmount());
                                case Dropout dropout -> assertEquals(0.05f, dropout.getPercentage());
                                case Delay delay -> assertEquals(500L, delay.getMillis());
                                case After after -> assertEquals(5000L, after.getMillis());
                                case Timeout timeout -> assertEquals(25000L, timeout.getMillis());
                                default -> throw new AssertionError("Unexpected condition encountered.");
                            }
                        }
                    }
                    if (stepId.contains("No_Log")){
                        assertFalse(trafficTemplate.isEnableLogs());
                    } else {
                        assertTrue(trafficTemplate.isEnableLogs());
                    }

                }
            }
        }
    }

    private void verifyProxySetup(ScenarioTestPlan tp) {
        assertTrue(tp.getName().contains("All_Proxy_Container"));

        assertTrue(tp.getTrafficComponents().isEmpty());

        Collection<ScenarioProxyComponent> proxyComponents = tp.getProxyComponents();
        assertFalse(proxyComponents.isEmpty());
        for (TestStep testStep : tp.getTestSteps()) {
            assertTrue(testStep.getSequence().isEmpty());
        }

        for (String stepId : tp.getStepIds()) {
            boolean serviceAExists = false, serviceBExists = false, serviceCExists = false;
            for (ScenarioProxyComponent proxyComponent : proxyComponents) {
                String name = proxyComponent.getName();
                serviceAExists = !serviceAExists ? name.equals("PIEKER_PROXY_service_a") : serviceAExists;
                serviceBExists = !serviceBExists ? name.equals("PIEKER_PROXY_service_b") : serviceBExists;
                serviceCExists = !serviceCExists ? name.equals("PIEKER_PROXY_service_c") : serviceCExists;

                if (proxyComponent.getSource() != null && !proxyComponent.getSource().isEmpty()){
                    assertEquals("PIEKER_PROXY_sA_sC", proxyComponent.getName());
                }
                if (stepId.contains("No_Condition")){
                    assertTrue(proxyComponent.getStepToConditionMap().get(stepId).isEmpty());
                } else {
                    assertEquals(4, proxyComponent.getStepToConditionMap().get(stepId).size());
                    for (ConditionTemplate conditionTemplate : proxyComponent.getStepToConditionMap().get(stepId)) {
                        switch (conditionTemplate){
                            case After after -> assertEquals(5000L, after.getMillis());
                            case Delay delay -> assertEquals(10000L, delay.getMillis());
                            case Timeout timeout -> assertEquals(15000L, timeout.getMillis());
                            case Dropout dropout -> assertEquals(0.5f, dropout.getPercentage());
                            default -> throw new AssertionError("Unexpected condition encountered.");
                        }
                    }
                }

                if (stepId.contains("No_Log")){
                    assertFalse(proxyComponent.getStepToLog().get(stepId));
                } else {
                    assertTrue(proxyComponent.getStepToLog().get(stepId));
                }

            }
            assertTrue(serviceAExists && serviceBExists && serviceCExists);
        }
    }
}
