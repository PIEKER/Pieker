package pieker.dsl.code.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;

@Slf4j
@Getter
public class Delay implements Template {

    private final String name = Delay.class.getSimpleName();
    private final float seconds;

    public Delay(float seconds) {
        this.seconds = seconds;
    }

    public Delay(int seconds) {
        this.seconds = seconds;
    }

    public void addContextVariable(VelocityContext ctx){
        long longDelay = (long) this.seconds*1000;
        ctx.put("delay", longDelay);
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
}
