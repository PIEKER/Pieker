package pieker.dsl.architecture.template.traffic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import pieker.common.Template;
import pieker.common.connection.Http;
import pieker.common.connection.ResponseTuple;
import pieker.dsl.architecture.Engine;
import pieker.dsl.architecture.preprocessor.FileManager;

@Getter
@Slf4j
public class Sql implements Template, TrafficType {

    private final String name = Sql.class.getSimpleName();

    private final String database;
    private final String databaseServer;
    private final String parameter;

    public Sql(String databaseServer, String database, String parameter) {
        this.databaseServer = databaseServer;
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
    public ResponseTuple sendTraffic(String[] args) {
        this.translateParameters();

        if (args.length < 1){
            String message = "unable to send SQL query du to missing parameters: 1 required";
            log.error(message);
            return new ResponseTuple(message, 500);
        }
        String endpointUrl = "/" + this.database + "/query";
        String body = "{\"query\":\"" + this.query + "\"}";
        return Http.send(this.databaseServer, args[0] + endpointUrl, "POST", 3000, 30000, "", body, "json");
    }

    @Override
    public void addContextVariable(VelocityContext ctx) {
        ctx.put("trafficType", "sql");
        ctx.put("database", this.database);
        ctx.put("query", query);
    }

    @Override
    public void translateParameters() {
        this.query = getParameter();
    }

    @Override
    public String getTarget() {
        return this.databaseServer;
    }

    /**
     * Resolves parameter from a stored String value, including possible file referencing.
     * @return query string
     */
    public String getParameter(){
        if (this.parameter.startsWith(FileManager.PREFIX)) {
            return Engine.getFileManager().getDataFromFileHash(this.parameter);
        }
        return this.parameter;
    }
}
