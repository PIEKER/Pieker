package pieker.dsl.code.component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;
import pieker.common.TrafficTemplate;
import pieker.dsl.code.template.architecture.TrafficType;
import pieker.dsl.code.template.condition.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class Traffic implements StepComponent, TrafficTemplate {

    private final String identifier;
    private final TrafficType trafficType;
    private List<Template> conditionList = new ArrayList<>();
    private boolean enableLogs;

    @JsonIgnore
    private After after;
    @JsonIgnore
    private Delay delay;
    @JsonIgnore
    private Dropout dropout;
    @JsonIgnore
    private Retry retry;
    @JsonIgnore
    private Timeout timeout;
    @JsonIgnore
    private Times times;

    public Traffic(String identifier, TrafficType trafficType) {
        this.identifier = identifier;
        this.trafficType = trafficType;
    }

    public Traffic(String identifier, TrafficType trafficType, List<Template> conditionList) {
        this.identifier = identifier;
        this.trafficType = trafficType;
        this.conditionList = conditionList;
    }

    @Override
    public void addCondition(Template condition) {
        this.conditionList.add(condition);
    }

    @Override
    public StepComponent copy() {
        return new Traffic(this.identifier, this.trafficType, new ArrayList<>(this.conditionList));
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        this.trafficType.translateParameters();
        this.trafficType.addContextVariable(ctx);
        ctx.put("enableLogging", this.enableLogs);
        this.conditionList.forEach(t -> t.addContextVariable(ctx));
    }

    @Override
    public void startTraffic(String[] args){
        this.transferConditionList();

        this.after.performCondition();
        if (this.times != null && this.retry != null){
            log.error("invalid traffic configuration on Traffic. No Amount or Retry Limit set.");
        } else if (this.times != null) {
            this.times.performCondition(this::runTraffic, args);
        } else {
            this.retry.performCondition(this::runTraffic, args);
        }
    }

    @Override
    public void enableLogging(){
        this.enableLogs = true;
    }

    private void transferConditionList(){
        this.conditionList.forEach( condition -> {
            switch (condition){
                case After a -> this.after = a;
                case Delay d -> this.delay = d;
                case Dropout dr -> this.dropout = dr;
                case Retry r -> this.retry = r;
                case Timeout tt -> this.timeout = tt;
                case Times ts -> this.times = ts;
                default -> log.warn("Condition skipped due to unknown class type: {}", condition.getClass());
            }
        });
        if (this.times != null && this.retry != null){
            this.times = new Times(1);
        }
    }

    private void runTraffic(String[] args){
        this.delay.performCondition();
        if (this.dropout.performCondition()){
            return;
        }
        String response = this.trafficType.sendTraffic(args);
        log.info("[RESULT] {}", response);
    }
}
