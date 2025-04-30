package pieker.dsl.code.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

@Slf4j
@Getter
public class Timeout implements ConditionTemplate {

    private final String name = Timeout.class.getSimpleName();
    private final float seconds;

    public Timeout(float seconds) {
        this.seconds = seconds;
    }
    public Timeout(int seconds) {
        this.seconds = seconds;
    }

    public void addContextVariable(VelocityContext ctx){
        ctx.put("timeout", (long) seconds*1000);
    }

    @Override
    public Object getValue() {
        return this.seconds;
    }
}
