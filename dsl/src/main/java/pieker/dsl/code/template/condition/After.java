package pieker.dsl.code.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;

@Slf4j
@Getter
public class After implements Template {

    private final String name = After.class.getSimpleName();
    private final float seconds;

    public After(float seconds) {
        this.seconds = seconds;
    }

    public After(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("after", seconds*1000);
    }

    public void performCondition() {
        long longAfter = (long) this.seconds*1000;
        if (longAfter > 0){
            try {
                Thread.sleep(longAfter); // Pause execution for the specified time
            } catch (InterruptedException e) {
                log.error("exception on delay: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
