package pieker.supervisor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.architectures.model.ArchitectureModel;
import pieker.architectures.model.Component;
import pieker.common.ScenarioTestPlan;

@Slf4j
@Getter
@Setter
public abstract class AbstractSupervisor<M extends ArchitectureModel<? extends Component>> implements Supervisor<M> {

    private ScenarioTestPlan testPlan;
    private Status status = Status.NONE;
    private M model;

}
