package pieker.common.connection;

import org.jetbrains.annotations.NotNull;

public record ResponseTuple(String response, int statusCode) {
    @NotNull
    public String toString(){
        return "REQUEST-STATUS{" + this.statusCode + "}-REQUEST-MESSAGE{" + this.response + "}";
    }
}
