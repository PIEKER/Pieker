package pieker.dsl.architecture.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

@Slf4j
@Getter
public class Delay implements ConditionTemplate {

    private final String name = Delay.class.getSimpleName();
    private final long millis;

    public Delay(float seconds) {
        this.millis = (long) (seconds*1000);
    }

    public Delay(int seconds) {
        this.millis = seconds * 1000L;
    }

    public void addContextVariable(VelocityContext ctx){
        ctx.put("delay", this.millis);
    }

    public void performCondition(){
        if (this.millis > 0){
            try {
                Thread.sleep(this.millis); // Pause execution for the specified time
            } catch (InterruptedException e) {
                log.error("exception on delay: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public Object getValue() {
        return this.millis;
    }

}
