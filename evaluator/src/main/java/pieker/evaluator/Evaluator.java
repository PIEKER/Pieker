package pieker.evaluator;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.common.Assertions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Slf4j
public class Evaluator {

    public record SQLConnection(String url, String user, String password){}

    private String logDirectory = ".";
    private Map<String, SQLConnection> databaseMap = new HashMap<>();

    public Evaluator(){}

    public Evaluator(String logDirectory, Map<String, SQLConnection> databaseMap){
        this.logDirectory = logDirectory;
        this.databaseMap = databaseMap;
    }

    public void addDatabaseConnection(String identifier, SQLConnection con){
        this.databaseMap.put(identifier, con);
    }

    public void run(Map<String, List<Assertions>> stepToEvaluationMap){
        stepToEvaluationMap.forEach((s, evaluations) ->
                evaluations.forEach(evaluation -> {
                    if (this.databaseMap.containsKey(evaluation.getIdentifier())){
                        //fixme: implement loggable and accessible interfaces for assertions
                        log.debug("{} identified as DatabaseAssert.", evaluation.getIdentifier());
                        SQLConnection con = this.databaseMap.get(evaluation.getIdentifier());
                        evaluation.evaluate(new String[]{s, con.url, con.user, con.password});
                    } else {
                        log.debug("{} identified as TrafficAssert.", evaluation.getIdentifier());
                        evaluation.evaluate(new String[]{s, this.logDirectory});
                    }
                }
            )
        );
    }
}
