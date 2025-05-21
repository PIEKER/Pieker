package pieker.dsl.parser;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import pieker.api.assertions.Assert;
import pieker.common.plugin.PluginManager;
import pieker.dsl.PiekerDslException;
import pieker.dsl.antlr.gen.PiekerParser;
import pieker.dsl.antlr.gen.PiekerParserBaseListener;
import pieker.dsl.model.*;
import pieker.dsl.model.assertions.DatabaseAssert;
import pieker.dsl.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static pieker.dsl.util.FeatureUtil.getShiftedLineIndex;

@Slf4j
public class FeatureParser extends PiekerParserBaseListener {

    private static PluginManager pluginManager = new PluginManager();

    private final ParsingReader reader;
    private final Feature feature;

    private Scenario latestScenario;

    private FeatureParser(ParsingReader reader, Feature feature){
        this.reader = reader;
        this.feature = feature;
        ParseTree tree = this.reader.getParseTree();
        log.debug("start walking on AST of '{}'", this.reader.getFileName());
        this.reader.walker.walk(this, tree);
        log.info("finished walking AST of '{}'", this.reader.getFileName());
    }

    /**
     * Entry method for parsing a Pieker DSL file into runtime objects.
     *
     * @param fromResource  file is stored in resource folder
     * @param feature   runtime object to store the found data in
     * @throws IOException  if file could not be loaded properly
     */
    public static void parse(Feature feature, boolean fromResource) throws IOException {
        parse(feature,fromResource, null);
    }

    public static void parse(Feature feature, boolean fromResource, PluginManager pm) throws IOException{
        if (pm != null) pluginManager = pm;

        ParsingReader r = new ParsingReader(feature.getFileName(), fromResource);
        FeatureParser fp = new FeatureParser(r, feature);
        if(fp.reader.errorListener.isFail()){
            String errorMessage = fp.reader.errorListener.getMessage();
            log.error("not a valid feature file: {} - {}", fp.reader.getFileName(), errorMessage);
            throw new PiekerDslException(errorMessage);
        }
    }

    // LISTENER METHODS

    /**
     * Listener method, processing, when the walker detects the header of the feature file.
     *
     * @param ctx the parse tree beginning on 'featureHeader' node.
     */
    @Override
    public void enterFeatureHeader(PiekerParser.FeatureHeaderContext ctx){
        String featureName = ctx.line() != null ? ctx.line().getText() : "NO_NAME";
        this.feature.setName(featureName);
        log.debug("entered FeatureHeaderContext {}", this.feature.getName());
        this.feature.setDescription(this.getDescriptionIfPresent(ctx.description()));

    }

    /**
     * Listener method, processing, when the walker detects a 'background' node.
     *
     * @param ctx the parse tree beginning on 'background' node
     */
    @Override
    public void enterBackground(PiekerParser.BackgroundContext ctx){
        log.debug("entered BackgroundContext.");
        Background background = new Background(this.feature);
        background.setLine(getShiftedLineIndex(ctx.BACKGROUND()));
        background.setDescription(this.getDescriptionIfPresent(ctx.description()));
        this.createGlobalScope(ctx, background);
        this.feature.setBackground(background);
    }

    /**
     * Listener method, processing, when the walker detects a 'scenario' node.
     *
     * @param ctx the parse tree beginning on 'scenario' node.
     */
    @Override
    public void enterScenario(PiekerParser.ScenarioContext ctx){
        String ident = ctx.line().getText();
        log.debug("entered ScenarioContext {}", ident);

        Scenario scenario = new Scenario(this.feature, ident);
        scenario.setLine(getShiftedLineIndex(ctx.SCENARIO()));
        scenario.setDescription(this.getDescriptionIfPresent(ctx.description()));
        this.createScenarioScope(ctx, scenario);
        this.feature.addScenario(scenario);
        this.latestScenario = scenario;
    }

    /**
     * Listener method, processing, when the walker detects a 'step' node.
     *
     * @param ctx the parse tree beginning on 'step' node.
     */
    @Override
    public void enterBeforeEach(PiekerParser.BeforeEachContext ctx){
        log.debug("entered BeforeEachContext");
        if (this.latestScenario == null) {
            log.error("beforeEach detected without entering a scenario context. " +
                    "Skipping context 'beforeEach' to prevent runtime failure.");
            return;
        }

        Step step = new Step(this.feature, this.latestScenario);
        step.setBeforeEach(true);
        step.setLine(getShiftedLineIndex(ctx.BEFORE_EACH()));
        this.latestScenario.setBeforeEach(step);

        this.createBDDNodes(ctx.given(), ctx.when(), ctx.then(), step);
    }

    /**
     * Listener method, processing, when the walker detects a 'step' node.
     *
     * @param ctx the parse tree beginning on 'step' node.
     */
    @Override
    public void enterStep(PiekerParser.StepContext ctx){
        String ident = ctx.line().getText();

        log.debug("entered StepContext {}", ident);
        if (this.latestScenario == null) {
            log.error("step detected without entering a scenario context. " +
                    "Skipping context '{}' to prevent runtime failure.", ident);
            return;
        }

        Step step = new Step(this.feature, this.latestScenario, ident);
        step.setLine(getShiftedLineIndex(ctx.STEP()));
        step.setDescription(this.getDescriptionIfPresent(ctx.description()));
        this.latestScenario.addStep(step);

        this.createBDDNodes(ctx.given(),ctx.when(), ctx.then(), step);
    }

    // sub-node processing

    /**
     * Processes variable declarations on provided on a 'scenario' node.
     *
     * @param ctx 'scenario' node
     * @param scenario entity model
     */
    private void createScenarioScope(PiekerParser.ScenarioContext ctx, Scenario scenario) {
        PiekerParser.ScenarioScopeContext ctxScenarioScope = ctx.scenarioScope();
        if (ctxScenarioScope == null){
            return;
        }
        ctxScenarioScope.line()
                .forEach(lineContext ->
                        scenario.addEntry(ctxScenarioScope.DEF().getFirst().getText(), lineContext.getText()));
    }

    /**
     * A method to process sub-nodes of the BackgroundContext. Any found data matching the Pieker grammar will be stored
     * in the provided background object.
     *
     * @param ctx parse tree of the 'background' node
     * @param background entity model
     */
    private void createGlobalScope(PiekerParser.BackgroundContext ctx, Background background){
        PiekerParser.GlobalScopeContext ctxGlobalScope = ctx.globalScope();
        if (ctxGlobalScope == null){
            return;
        }

        ctxGlobalScope.line()
                .forEach(lineContext ->
                    background.addEntry(ctxGlobalScope.DEF().getFirst().getText(), lineContext.getText()));
    }

    /**
     * A method to process sub-nodes of a StepContext. Possible sub-nodes are 'given', 'when' and 'then' contexts.
     * Any found data matching the Pieker grammar will be stored in the provided step object.
     *
     * @param ctxGiven parse tree of the 'given' node
     * @param ctxWhen parse tree of the 'when' node
     * @param ctxThen parse tree of the 'then' node
     * @param step entity model
     */
    private void createBDDNodes(PiekerParser.GivenContext ctxGiven,
                                PiekerParser.WhenContext ctxWhen,
                                PiekerParser.ThenContext ctxThen, Step step){
        if (ctxGiven != null) {
            this.createGivenNode(ctxGiven, step);
        }

        if (ctxWhen != null){
            log.debug("entered WhenContext of step '{}'", step.getName());
            When when = new When(step);
            when.setDescription(this.getDescriptionIfPresent(ctxWhen.description()));
            when.setLine(getShiftedLineIndex(ctxWhen.WHEN()));
            for (PiekerParser.WhenConditionContext ctxCondition: ctxWhen.whenCondition()){
                when.addEntry(ctxCondition.whenKey().getText(),ctxCondition.line().getText());
            }
            step.setWhen(when);
        }

        if (ctxThen != null){
            this.createThenNode(ctxThen, step);
        }

    }

    private void createGivenNode(PiekerParser.GivenContext ctxGiven, Step step){
        log.debug("entered GivenContext of step '{}'", step.getName());
        Given given = new Given(step);
        given.setDescription(this.getDescriptionIfPresent(ctxGiven.description()));
        given.setLine(getShiftedLineIndex(ctxGiven.GIVEN()));

        for (PiekerParser.GivenConditionContext ctxCondition: ctxGiven.givenCondition()){
            String givenLine = ctxCondition.line() == null ? "" : ctxCondition.line().getText();
            PiekerParser.GivenKeyContext ctxGivenKey = ctxCondition.givenKey();

            this.addEntryForGivenNode(ctxGivenKey, given, givenLine, ctxCondition);
        }
        step.setGiven(given);
    }

    private void createThenNode(PiekerParser.ThenContext ctxThen, Step step){
        log.debug("entered ThenContext of step '{}'", step.getName());
        Then then = new Then(step);
        then.setLine(getShiftedLineIndex(ctxThen.THEN()));
        then.setDescription(this.getDescriptionIfPresent(ctxThen.description()));

        if (ctxThen.logAll() != null){
            String[] identifierList = Util.convertStringToStringArray(ctxThen.logAll().line().getText());
            Arrays.stream(identifierList).forEach(then::addLogAllIdentifier);
        }

        if (ctxThen.assert_() != null){
            ctxThen.assert_().forEach(ctxAssert -> this.createAssertions(then, ctxAssert));
        }

        step.setThen(then);
    }

    private void createAssertions(Then then, PiekerParser.AssertContext ctxAssert){

        String assertPlugin = "";
        if (ctxAssert.line() != null){
            assertPlugin = ctxAssert.line().getText().trim();
        }

        String arguments = "";
        if (ctxAssert.arguments() != null && ctxAssert.arguments().line() != null){
            arguments = ctxAssert.arguments().line().getText().trim();
        }

        Assert ass = assertPlugin.equals("Database") ? new DatabaseAssert(arguments): pluginManager.createPluginInstance(assertPlugin, arguments);
        ass.setStepId(then.getStep().getId());

        if (ctxAssert.assertBody() == null) {
            throw new PiekerDslException("Assert with no body detected: " + then.getLine());
        }

        this.createAssertFunctionLists(then, ass,
                ctxAssert.assertBody().assertBool(), ctxAssert.assertBody().assertEquals(), ctxAssert.assertBody().assertNull());

        then.addAssert(ass);

    }

    private void createAssertFunctionLists(Then then, Assert ass,
                                           List<PiekerParser.AssertBoolContext> boolContextList,
                                           List<PiekerParser.AssertEqualsContext> equalsContextList,
                                           List<PiekerParser.AssertNullContext> nullContextList){
        boolContextList.forEach(ctxBool -> {
            String[] args = ctxBool.boolHeader().line().getText().trim().split("\\|");
            if (args.length != 2){
                throw new PiekerDslException(
                        "invalid amount of arguments detected on Assert.BoolHeader: " + then.getLine());
            }
            ass.addBoolAssertion(args[0].trim(), args[1].trim(), ctxBool.line().getText().trim());
        });

        equalsContextList.forEach(ctxEquals -> {
                String[] args = ctxEquals.equalsHeader().line().getText().trim().split("\\|");
                if (args.length != 2) {
                    throw new PiekerDslException(
                            "invalid amount of arguments detected on Assert.EqualsHeader: " + then.getLine());
                }
                ass.addEqualsAssertion(args[0].trim(), args[1].trim(), ctxEquals.line().getText().trim());
            }
        );

        nullContextList.forEach(ctxNull ->
                ass.addNullAssertion(
                        ctxNull.nullHeader().line().getText().trim(),
                        ctxNull.line().getText().trim()
                )
        );
    }

    private void addEntryForGivenNode(PiekerParser.GivenKeyContext ctxGivenKey, Given given,
                                      String givenLine, PiekerParser.GivenConditionContext ctxCondition){
        // check for passive on a request or sql condition
        if (ctxGivenKey.PASSIVE() != null ){
            TerminalNode passive = ctxGivenKey.PASSIVE();
            String secondKey = ctxGivenKey.REQUEST() != null ? ctxGivenKey.REQUEST().getText() : "";
            secondKey += ctxGivenKey.SQL() != null ? ctxGivenKey.SQL().getText() : "";
            given.addEntry(passive.getText(), secondKey.trim() + " " + givenLine.trim());
        } else {
            given.addEntry(ctxCondition.givenKey().getText(), givenLine);
        }
    }

    private String getDescriptionIfPresent(PiekerParser.DescriptionContext ctx){
        return ctx != null ? ctx.getText() : "";
    }
}
