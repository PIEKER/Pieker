package pieker.dsl.architecture.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

@Slf4j
@Getter
public class Timeout implements ConditionTemplate {

    private final String name = Timeout.class.getSimpleName();
    private final long millis;

    public Timeout(float seconds) {
        this.millis =  (long) (seconds*1000);
    }
    public Timeout(int seconds) {
        this.millis = seconds * 1000L;
    }

    public void addContextVariable(VelocityContext ctx){
        ctx.put("timeout", millis);
    }

    @Override
    public Object getValue() {
        return this.millis;
    }
}
