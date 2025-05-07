package pieker.dsl.architecture.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

@Slf4j
@Getter
public class Delay implements ConditionTemplate {

    private final String name = Delay.class.getSimpleName();
    private final float seconds;

    public Delay(float seconds) {
        this.seconds = seconds;
    }

    public Delay(int seconds) {
        this.seconds = seconds;
    }

    public void addContextVariable(VelocityContext ctx){
        ctx.put("delay", (long) this.seconds*1000);
    }

    public void performCondition(){
        long longDelay = (long) this.seconds*1000;
        if (longDelay > 0){
            try {
                Thread.sleep(longDelay); // Pause execution for the specified time
            } catch (InterruptedException e) {
                log.error("exception on delay: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public Object getValue() {
        return this.seconds;
    }
}
