package pieker.dsl.architecture.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

import java.util.function.Consumer;

@Slf4j
@Getter
public class Retry implements ConditionTemplate {

    private final String name =Retry.class.getSimpleName();
    private float seconds;

    public Retry(float seconds) {
        this.seconds = seconds;
    }

    public Retry(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("retry", (long) seconds*1000);
    }

    public void performCondition(Consumer<String[]> traffic, String[] args){
        long millis = (long) this.seconds*1000;
        long trafficUntil = System.currentTimeMillis() + millis;
        long timer = System.currentTimeMillis();
        while (timer < trafficUntil){
            traffic.accept(args);
            timer = System.currentTimeMillis();
        }
    }

    @Override
    public Object getValue() {
        return this.seconds;
    }
}
