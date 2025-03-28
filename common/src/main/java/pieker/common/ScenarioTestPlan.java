package pieker.common;

import java.util.Collection;

/**
 * Interface for a test plan that is generated by PIEKER for a given test scenario.
 */
public interface ScenarioTestPlan {

    String getName();

    Collection<ScenarioProxyComponent> getProxyComponents();

    Collection<ScenarioTrafficComponent> getTrafficComponents();

    Collection<ScenarioComponent> getComponents();

    Collection<String> getStepIds();

    TestStep getBeforeStep();

    Collection<TestStep> getSupervisorStepListAsTestStep();

}
