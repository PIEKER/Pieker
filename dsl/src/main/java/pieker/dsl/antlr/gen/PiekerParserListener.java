// Generated from H:/Uni/Master/Semester-5/Pieker/dsl/src/main/java/pieker/dsl/antlr/grammar/PiekerParser.g4 by ANTLR 4.13.2
package pieker.dsl.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PiekerParser}.
 */
public interface PiekerParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PiekerParser#feature}.
	 * @param ctx the parse tree
	 */
	void enterFeature(PiekerParser.FeatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#feature}.
	 * @param ctx the parse tree
	 */
	void exitFeature(PiekerParser.FeatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#featureHeader}.
	 * @param ctx the parse tree
	 */
	void enterFeatureHeader(PiekerParser.FeatureHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#featureHeader}.
	 * @param ctx the parse tree
	 */
	void exitFeatureHeader(PiekerParser.FeatureHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#background}.
	 * @param ctx the parse tree
	 */
	void enterBackground(PiekerParser.BackgroundContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#background}.
	 * @param ctx the parse tree
	 */
	void exitBackground(PiekerParser.BackgroundContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#globalScope}.
	 * @param ctx the parse tree
	 */
	void enterGlobalScope(PiekerParser.GlobalScopeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#globalScope}.
	 * @param ctx the parse tree
	 */
	void exitGlobalScope(PiekerParser.GlobalScopeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#scenario}.
	 * @param ctx the parse tree
	 */
	void enterScenario(PiekerParser.ScenarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#scenario}.
	 * @param ctx the parse tree
	 */
	void exitScenario(PiekerParser.ScenarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#scenarioScope}.
	 * @param ctx the parse tree
	 */
	void enterScenarioScope(PiekerParser.ScenarioScopeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#scenarioScope}.
	 * @param ctx the parse tree
	 */
	void exitScenarioScope(PiekerParser.ScenarioScopeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#given}.
	 * @param ctx the parse tree
	 */
	void enterGiven(PiekerParser.GivenContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#given}.
	 * @param ctx the parse tree
	 */
	void exitGiven(PiekerParser.GivenContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#givenCondition}.
	 * @param ctx the parse tree
	 */
	void enterGivenCondition(PiekerParser.GivenConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#givenCondition}.
	 * @param ctx the parse tree
	 */
	void exitGivenCondition(PiekerParser.GivenConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#givenKey}.
	 * @param ctx the parse tree
	 */
	void enterGivenKey(PiekerParser.GivenKeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#givenKey}.
	 * @param ctx the parse tree
	 */
	void exitGivenKey(PiekerParser.GivenKeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#when}.
	 * @param ctx the parse tree
	 */
	void enterWhen(PiekerParser.WhenContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#when}.
	 * @param ctx the parse tree
	 */
	void exitWhen(PiekerParser.WhenContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#whenCondition}.
	 * @param ctx the parse tree
	 */
	void enterWhenCondition(PiekerParser.WhenConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#whenCondition}.
	 * @param ctx the parse tree
	 */
	void exitWhenCondition(PiekerParser.WhenConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#whenKey}.
	 * @param ctx the parse tree
	 */
	void enterWhenKey(PiekerParser.WhenKeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#whenKey}.
	 * @param ctx the parse tree
	 */
	void exitWhenKey(PiekerParser.WhenKeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#then}.
	 * @param ctx the parse tree
	 */
	void enterThen(PiekerParser.ThenContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#then}.
	 * @param ctx the parse tree
	 */
	void exitThen(PiekerParser.ThenContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#logAll}.
	 * @param ctx the parse tree
	 */
	void enterLogAll(PiekerParser.LogAllContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#logAll}.
	 * @param ctx the parse tree
	 */
	void exitLogAll(PiekerParser.LogAllContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#assert}.
	 * @param ctx the parse tree
	 */
	void enterAssert(PiekerParser.AssertContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#assert}.
	 * @param ctx the parse tree
	 */
	void exitAssert(PiekerParser.AssertContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#assertBody}.
	 * @param ctx the parse tree
	 */
	void enterAssertBody(PiekerParser.AssertBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#assertBody}.
	 * @param ctx the parse tree
	 */
	void exitAssertBody(PiekerParser.AssertBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(PiekerParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(PiekerParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#assertBool}.
	 * @param ctx the parse tree
	 */
	void enterAssertBool(PiekerParser.AssertBoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#assertBool}.
	 * @param ctx the parse tree
	 */
	void exitAssertBool(PiekerParser.AssertBoolContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#boolHeader}.
	 * @param ctx the parse tree
	 */
	void enterBoolHeader(PiekerParser.BoolHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#boolHeader}.
	 * @param ctx the parse tree
	 */
	void exitBoolHeader(PiekerParser.BoolHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#assertEquals}.
	 * @param ctx the parse tree
	 */
	void enterAssertEquals(PiekerParser.AssertEqualsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#assertEquals}.
	 * @param ctx the parse tree
	 */
	void exitAssertEquals(PiekerParser.AssertEqualsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#equalsHeader}.
	 * @param ctx the parse tree
	 */
	void enterEqualsHeader(PiekerParser.EqualsHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#equalsHeader}.
	 * @param ctx the parse tree
	 */
	void exitEqualsHeader(PiekerParser.EqualsHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#assertNull}.
	 * @param ctx the parse tree
	 */
	void enterAssertNull(PiekerParser.AssertNullContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#assertNull}.
	 * @param ctx the parse tree
	 */
	void exitAssertNull(PiekerParser.AssertNullContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#nullHeader}.
	 * @param ctx the parse tree
	 */
	void enterNullHeader(PiekerParser.NullHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#nullHeader}.
	 * @param ctx the parse tree
	 */
	void exitNullHeader(PiekerParser.NullHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#description}.
	 * @param ctx the parse tree
	 */
	void enterDescription(PiekerParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#description}.
	 * @param ctx the parse tree
	 */
	void exitDescription(PiekerParser.DescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#step}.
	 * @param ctx the parse tree
	 */
	void enterStep(PiekerParser.StepContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#step}.
	 * @param ctx the parse tree
	 */
	void exitStep(PiekerParser.StepContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#beforeEach}.
	 * @param ctx the parse tree
	 */
	void enterBeforeEach(PiekerParser.BeforeEachContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#beforeEach}.
	 * @param ctx the parse tree
	 */
	void exitBeforeEach(PiekerParser.BeforeEachContext ctx);
	/**
	 * Enter a parse tree produced by {@link PiekerParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(PiekerParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link PiekerParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(PiekerParser.LineContext ctx);
}