package pieker.dsl.architecture.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

@Slf4j
@Getter
public class After implements ConditionTemplate {

    private final String name = After.class.getSimpleName();
    private final long millis;

    public After(float seconds) {
        this.millis = (long) (seconds*1000);
    }

    public After(int seconds) {
        this.millis = seconds * 1000L;
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("after", this.millis);
    }

    public void performCondition() {
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
