package pieker.dsl.code.template.condition;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

import java.util.function.Consumer;

@Slf4j
@Getter
public class Times implements ConditionTemplate {

    private final String name = Times.class.getSimpleName();
    private final int amount;

    public Times(int amount) {
        this.amount = amount;
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("times", amount);
    }

    public void performCondition(Consumer<String[]> traffic, String[] args){
        for (int i = 0; i < this.amount; i++) {
            traffic.accept(args);
        }
    }

    @Override
    public Object getValue() {
        return this.amount;
    }
}
