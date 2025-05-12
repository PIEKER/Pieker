package pieker.dsl.architecture.template.condition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.ConditionTemplate;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Getter
public class Dropout implements ConditionTemplate {

    private final String name = Dropout.class.getSimpleName();
    @JsonIgnore
    private static final Random RANDOM = new SecureRandom();
    private final float percentage;

    public Dropout(float percentage) {
        this.percentage = percentage;
    }

    public Dropout(int percentage) {
        this.percentage = percentage;
    }

    public void addContextVariable(VelocityContext ctx){
        ctx.put("dropout", percentage);
    }

    public boolean performCondition() {
        return RANDOM.nextDouble() < (percentage);
    }

    @Override
    public Object getValue() {
        return this.percentage;
    }
}
