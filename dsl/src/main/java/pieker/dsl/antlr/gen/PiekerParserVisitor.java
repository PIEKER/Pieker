// Generated from H:/Uni/Master/Semester-5/Pieker/dsl/src/main/java/pieker/dsl/antlr/grammar/PiekerParser.g4 by ANTLR 4.13.2
package pieker.dsl.antlr.gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PiekerParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PiekerParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PiekerParser#feature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeature(PiekerParser.FeatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#featureHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeatureHeader(PiekerParser.FeatureHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#background}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackground(PiekerParser.BackgroundContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#globalScope}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobalScope(PiekerParser.GlobalScopeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#scenario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenario(PiekerParser.ScenarioContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#scenarioScope}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenarioScope(PiekerParser.ScenarioScopeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#given}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGiven(PiekerParser.GivenContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#givenCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGivenCondition(PiekerParser.GivenConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#givenKey}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGivenKey(PiekerParser.GivenKeyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#when}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhen(PiekerParser.WhenContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#whenCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenCondition(PiekerParser.WhenConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#whenKey}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenKey(PiekerParser.WhenKeyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#then}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThen(PiekerParser.ThenContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#logAll}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogAll(PiekerParser.LogAllContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#assert}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssert(PiekerParser.AssertContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#assertBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertBody(PiekerParser.AssertBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(PiekerParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#assertBool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertBool(PiekerParser.AssertBoolContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#boolHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolHeader(PiekerParser.BoolHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#assertEquals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertEquals(PiekerParser.AssertEqualsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#equalsHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualsHeader(PiekerParser.EqualsHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#assertNull}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertNull(PiekerParser.AssertNullContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#nullHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullHeader(PiekerParser.NullHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(PiekerParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#step}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStep(PiekerParser.StepContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#beforeEach}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeforeEach(PiekerParser.BeforeEachContext ctx);
	/**
	 * Visit a parse tree produced by {@link PiekerParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(PiekerParser.LineContext ctx);
}