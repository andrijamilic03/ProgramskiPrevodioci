// Generated from ccc\parser\Language.g4 by ANTLR 4.13.0
package ccc.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LanguageParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(LanguageParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LanguageParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(LanguageParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(LanguageParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#loopStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStatement(LanguageParser.LoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#forLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForLoop(LanguageParser.ForLoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#whileLoop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileLoop(LanguageParser.WhileLoopContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(LanguageParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#parameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterList(LanguageParser.ParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(LanguageParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(LanguageParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(LanguageParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#arrayAccess}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAccess(LanguageParser.ArrayAccessContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(LanguageParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#inputStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputStatement(LanguageParser.InputStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#printStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintStatement(LanguageParser.PrintStatementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberConstant}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberConstant(LanguageParser.NumberConstantContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VariableReference}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableReference(LanguageParser.VariableReferenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GroupingOperator}
	 * labeled alternative in {@link LanguageParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupingOperator(LanguageParser.GroupingOperatorContext ctx);
}