package pieker.dsl.code.template.architecture;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;
import pieker.dsl.code.Engine;
import pieker.dsl.code.preprocessor.FileManager;

import java.sql.*;

@Getter
@Slf4j
public class Sql implements Template, TrafficType {

    private final String name = Sql.class.getSimpleName();

    private final String database;
    private final String parameter;

    public Sql(String database, String parameter) {
        this.database = database;
        this.parameter = parameter.trim();
    }

    // Parameter
    @JsonIgnore
    private String query;

    /**
     * Initiates an SQL query for a database, specified in arguments.
     *
     * @param args Array of: dbUrl, dbUser, dbPassword
     * @return String of response.
     */
    @Override
    public String sendTraffic(String[] args) {
        this.translateParameters();

        if (args.length < 3){
            log.error("unable to send SQL query du to missing parameters: 3 required, only {} found", args.length);
            return "ERROR ON SQL";
        }
        return pieker.common.connection.Sql.send(this.database, args[0], args[1], args[2], this.query);
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("trafficType", "sql");
        ctx.put("query", query);
    }

    @Override
    public void translateParameters() {
        this.query = getParameter();
    }

    @Override
    public String getTarget() {
        return this.database;
    }

    public String getParameter(){
        if (this.parameter.startsWith(FileManager.PREFIX)) {
            return Engine.getFileManager().getDataFromFileHash(this.parameter);
        }
        return this.parameter;
    }
}
