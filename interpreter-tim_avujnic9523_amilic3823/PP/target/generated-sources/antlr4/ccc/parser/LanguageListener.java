// Generated from ccc\parser\Language.g4 by ANTLR 4.13.0
package ccc.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LanguageParser}.
 */
public interface LanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LanguageParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(LanguageParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(LanguageParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(LanguageParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(LanguageParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(LanguageParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(LanguageParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(LanguageParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(LanguageParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement(LanguageParser.LoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement(LanguageParser.LoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void enterForLoop(LanguageParser.ForLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#forLoop}.
	 * @param ctx the parse tree
	 */
	void exitForLoop(LanguageParser.ForLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#whileLoop}.
	 * @param ctx the parse tree
	 */
	void enterWhileLoop(LanguageParser.WhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#whileLoop}.
	 * @param ctx the parse tree
	 */
	void exitWhileLoop(LanguageParser.WhileLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(LanguageParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(LanguageParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void enterParameterList(LanguageParser.ParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#parameterList}.
	 * @param ctx the parse tree
	 */
	void exitParameterList(LanguageParser.ParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(LanguageParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(LanguageParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(LanguageParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(LanguageParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(LanguageParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(LanguageParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void enterArrayAccess(LanguageParser.ArrayAccessContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#arrayAccess}.
	 * @param ctx the parse tree
	 */
	void exitArrayAccess(LanguageParser.ArrayAccessContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(LanguageParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(LanguageParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#inputStatement}.
	 * @param ctx the parse tree
	 */
	void enterInputStatement(LanguageParser.InputStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#inputStatement}.
	 * @param ctx the parse tree
	 */
	void exitInputStatement(LanguageParser.InputStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#printStatement}.
	 * @param ctx the parse tree
	 */
	void enterPrintStatement(LanguageParser.PrintStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#printStatement}.
	 * @param ctx the parse tree
	 */
	void exitPrintStatement(LanguageParser.PrintStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberConstant}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNumberConstant(LanguageParser.NumberConstantContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberConstant}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNumberConstant(LanguageParser.NumberConstantContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableReference}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterVariableReference(LanguageParser.VariableReferenceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableReference}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitVariableReference(LanguageParser.VariableReferenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GroupingOperator}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterGroupingOperator(LanguageParser.GroupingOperatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GroupingOperator}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitGroupingOperator(LanguageParser.GroupingOperatorContext ctx);
}