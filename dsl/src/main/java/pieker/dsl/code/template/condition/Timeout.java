package pieker.dsl.code.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;

@Slf4j
@Getter
public class Timeout implements Template {

    private final String name = Timeout.class.getSimpleName();
    private final float seconds;

    public Timeout(float seconds) {
        this.seconds = seconds;
    }
    public Timeout(int seconds) {
        this.seconds = seconds;
    }

    public void addContextVariable(VelocityContext ctx){
        ctx.put("timeout", seconds*1000);
    }
}
