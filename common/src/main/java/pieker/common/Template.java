package pieker.common;

import org.apache.velocity.VelocityContext;

public interface Template {

    void addContextVariable(VelocityContext ctx);
}
