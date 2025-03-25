package pieker.common;


/**
 * Interface for a component that is generated by PIEKER for a given test scenario.
 */
public interface ScenarioComponent {

    String getSource();

    String getTarget();

    String getName();
}
