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
    private final long millis;

    public Retry(float seconds) {
        this.millis = (long) (seconds*1000);
    }

    public Retry(int seconds) {
        this.millis = seconds * 1000L;
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("retry", millis);
    }

    public void performCondition(Consumer<String[]> traffic, String[] args){
        long trafficUntil = System.currentTimeMillis() + this.millis;
        long timer = System.currentTimeMillis();
        while (timer < trafficUntil){
            traffic.accept(args);
            timer = System.currentTimeMillis();
        }
    }

    @Override
    public Object getValue() {
        return this.millis;
    }
}
