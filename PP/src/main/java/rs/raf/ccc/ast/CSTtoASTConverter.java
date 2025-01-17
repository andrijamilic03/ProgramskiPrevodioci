package rs.raf.ccc.ast;


import ccc.parser.LanguageLexer;
import ccc.parser.LanguageParser;
import ccc.parser.LanguageVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import rs.raf.ccc.Language;

import java.util.*;
import java.util.stream.Collectors;

public class CSTtoASTConverter extends AbstractParseTreeVisitor<Tree> implements LanguageVisitor<Tree> {

    private Language l;

    public CSTtoASTConverter(Language language) {
        this.l = language;
        /* Open the global scope.  */
        openBlock();
    }


    @Override
    public Tree visitStart(LanguageParser.StartContext ctx) {
        var stmts = ctx.statement()
                /* Take all the parsed statements, ... */
                .stream()
                /* ... visit them using this visitor, ... */
                .map(this::visit)
                /* ... then cast them to statements (because 'start: statement*',
                   so they can't be anything else), ...  */
                .map(x -> (Statement) x)
                /* ... and put them into a list.  */
                .collect(Collectors.toCollection(ArrayList::new));

        return new StatementList(getLocation(ctx), stmts);

    }


    private List<Set<Identifier>> environments = new ArrayList();
    private List<Map<String, FunctionDeclaration>> enviromentsF = new ArrayList();

    /** Open a new scope. */
    private void openBlock() {

        environments.add(new HashSet<>());
        enviromentsF.add(new HashMap<>());

    }

    /** Removes the last scope. */
    private void closeBlock() {
        environments.removeLast();
        enviromentsF.removeLast();
    }

    /** Saves a declaration into the current environment, diagnosing
     redeclaration. */
    private void pushDeclaration(Identifier name) {
        /* Intentionally overwriting the old variable as error recovery.  */
        var oldDecl = environments.getLast().add(name);
        if (!oldDecl) {
            l.error(name.getLocation(), "redeclaring variable '%s'", name);
        }
    }

    private void pushFunction(String name, FunctionDeclaration functionDeclaration)
    {
        var oldDecl = enviromentsF.getLast().put(name, functionDeclaration);
        if (oldDecl != null) {
            l.error(functionDeclaration.getLocation(), "Function already exists in this scope!");
        }
    }

    /** Tries to find a declaration in any scope parent to this one.  */
    private Optional<Identifier> lookup(Location loc, String name) {
        /* Walk through the scope, starting at the top one, ... */
        for (var block : environments.reversed()) {
            for (var identifier : block) {
                if (identifier.getIdentifier().equals(name)) {
                    return Optional.of(identifier);
                }
            }
        }
        /* ... otherwise, we fell through and found nothing.  Diagnose and
           continue.  */
        l.error(loc, "failed to find variable '%s' in current scope", name);
        return Optional.empty();
    }

    private Optional<FunctionDeclaration> lookupFunctionCall(Location loc, String name) {
        for(var block : enviromentsF.reversed()) {
            var fdecl = block.get(name);
            if (fdecl != null) {
                return Optional.of(fdecl);
            }
        }
        l.error(loc, "Failed to find the function '%s' in current scope", name);
        return Optional.empty();
    }

    @Override
    public Tree visitBlock(LanguageParser.BlockContext ctx) {
        /* Open a new environment.  */
        openBlock();

        var stmts = ctx.statement()
                /* Take all the parsed statements, ... */
                .stream()
                /* ... visit them using this visitor, ... */
                .map(this::visit)
                /* ... then cast them to statements (because 'start: statement*',
                   so they can't be anything else), ...  */
                .map(x -> (Statement) x)
                /* ... and put them into a list.  */
                .collect(Collectors.toCollection(ArrayList::new));

        /* Close the one opened above.  */
        closeBlock();
        return new StatementList(getLocation(ctx), stmts);
    }


    @Override
    public Tree visitStatement(LanguageParser.StatementContext ctx) {
        /* A statement just contains a child.  Visit it instead.  It has to be
           a statement, so we check for that by casting.

           Note that we assume here that statement is defined as an OR of many
           different rules, with its first child being whatever the statement
           actually is, and the rest, if any, are unimportant.  */
        var substatement = visit(ctx.getChild(0));
        return (Statement) substatement;

    }

    @Override
    public Tree visitDeclaration(LanguageParser.DeclarationContext ctx) {
        var typeText = ctx.type.getText();
        Type type;
        if(typeText.equalsIgnoreCase("int"))
            type = new NumberType();
        else
            type = new BooleanType();

        var name = new Identifier(getLocation(ctx.ID()), ctx.ID().getText(), type);
        name.setType(type);

        List<NumberLiteral> dimensions = new ArrayList<>();
        if (ctx.arraySize != null) {
            for (var sizeCtx : ctx.arraySize) {
                if (sizeCtx != null) {
                    dimensions.add(new NumberLiteral(getLocation(ctx), Double.parseDouble(sizeCtx.getText())));
                    name.getDimension().add(new NumberLiteral(getLocation(ctx), Double.parseDouble(sizeCtx.getText())));
                }
            }
        }

        pushDeclaration(name);
        if(dimensions.isEmpty())
            return new Declaration(getLocation(ctx), type, name, null);
        else {
            return new Declaration(getLocation(ctx), type, name, dimensions);
        }
    }

    @Override
    public Tree visitAssignment(LanguageParser.AssignmentContext ctx) {
        var right = visit(ctx.expression()); // 5
        var left = visit(ctx.leftHandSide()); // identifier
        if(left instanceof Identifier || left instanceof ArrayAccess) {
            return new AssignmentStatement(getLocation(ctx), (Expression) left, null, (Expression) right);
        } else{
            return new AssignmentStatement(getLocation(ctx), null, (Declaration) left, (Expression) right);
        }
    }

    @Override
    public Tree visitLeftHandSide(LanguageParser.LeftHandSideContext ctx) {
        if (ctx.ID() != null) {
            var location = getLocation(ctx);
            var id = lookup(location, ctx.getText());
            if (id.isEmpty()) new ErrorExpression(location);
            return id.get();
        } else if (ctx.arrayAccess() != null) {
            return visit(ctx.arrayAccess());
        } else if (ctx.declaration() != null) {
            return visit(ctx.declaration());
        }
        throw new IllegalStateException("Unexpected left-hand side in assignment.");
    }



    @Override
    public Tree visitIfStatement(LanguageParser.IfStatementContext ctx) {
        // Prvo, posetimo uslov koji je između LPAREN i RPAREN
        Expression condition = (Expression) visit(ctx.expression());

        openBlock();
        // Telo 'then' bloka (svi statement-ovi unutar prve grane)
        List<Statement> thenStatements = ctx.statement().stream()
                .filter(statement -> ctx.ELSE() == null || ctx.statement().indexOf(statement) < ctx.statement().size() - 1)
                .map(this::visit)
                .map(x -> (Statement) x)
                .collect(Collectors.toList());
        closeBlock();

        // Telo 'else' bloka (ako postoji ELSE)
        List<Statement> elseStatements = null;
        if (ctx.ELSE() != null) {
            openBlock();

            // Ako postoji ELSE, obradimo sve statement-ove u else delu
            elseStatements = ctx.statement().stream()
                    .filter(statement -> ctx.statement().indexOf(statement) == ctx.statement().size() - 1)
                    .map(this::visit)
                    .map(x -> (Statement) x)
                    .collect(Collectors.toList());
            closeBlock();
        }

        // Kreiramo AST za IfStatement sa odgovarajućim granama
        return new IfStatement(getLocation(ctx), condition, thenStatements, elseStatements);
    }

    @Override
    public Tree visitLoopStatement(LanguageParser.LoopStatementContext ctx) {
        if (ctx.forLoop() != null) {
            return visitForLoop(ctx.forLoop());  // Obrada za 'for' petlju
        } else if (ctx.whileLoop() != null) {
            return visitWhileLoop(ctx.whileLoop());  // Obrada za 'while' petlju
        }
        return null;  // Ako nije prepoznata ni jedna petlja
    }

    @Override
    public Tree visitForLoop(LanguageParser.ForLoopContext ctx) {

        return null;
    }

    @Override
    public Tree visitWhileLoop(LanguageParser.WhileLoopContext ctx) {
        // Uslov while petlje
        Expression condition = (Expression) visit(ctx.expression());  // Uslov petlje

        openBlock();

        // Telo petlje (mogu biti više izjava unutar bloka)
        List<Statement> body = ctx.statement().stream()
                .map(this::visit)
                .map(x -> (Statement) x)
                .collect(Collectors.toList());

        closeBlock();

        // Kreiraj WhileLoopStatement objekat
        return new WhileLoopStatement(getLocation(ctx), condition, body);
    }

    @Override
    public Tree visitFunctionDeclaration(LanguageParser.FunctionDeclarationContext ctx) {
        var typeText = ctx.type.getText();
        Type type;
        if(typeText.equalsIgnoreCase("int"))
            type = new NumberType();
        else
            type = new BooleanType();
        var name = new Identifier(getLocation(ctx), ctx.name.getText(), type);
        Map<Identifier, Type> params = new HashMap<>();
        if (ctx.firstName != null) {
            if(ctx.firstType.getText().equalsIgnoreCase("int"))
                params.put(new Identifier(getLocation(ctx), ctx.firstName.getText(), new NumberType()), new NumberType());
            else
                params.put(new Identifier(getLocation(ctx), ctx.firstName.getText(), new NumberType()), new BooleanType());
        }
        if(ctx.restName != null)
        {
            int i = 0;
            for(var param: ctx.restName)
            {
                Type paramType;
                if(ctx.restType.get(i).getText().equalsIgnoreCase("int"))
                    paramType = new NumberType();
                else
                    paramType = new BooleanType();
                var paramName = new Identifier(getLocation(ctx), param.getText(), paramType);
                paramName.setResultType(paramType);
                params.put(paramName, paramType);
                i++;
            }
        }

        openBlock();
        for(var entry : params.entrySet())
        {
            pushDeclaration(entry.getKey());
        }


        var statements = ctx.body.stream()
                .map(this::visit)
                .map(x -> (Statement) x)
                .toList();
        var statementList = new StatementList(getLocation(ctx), statements);

        var returnStatement = visit(ctx.returnStatement());

        closeBlock();
        var f = new FunctionDeclaration(getLocation(ctx), type, name, params, statementList, (ReturnStatement) returnStatement);

        pushFunction(name.getIdentifier(), f);
        return  f;
    }


    @Override
    public Tree visitExpression(LanguageParser.ExpressionContext ctx) {
        return visit(ctx.logicalExpression());
    }

    @Override
    public Tree visitLogicalExpression(LanguageParser.LogicalExpressionContext ctx) {
        var value = visit(ctx.initial);

        assert ctx.op.size()==ctx.rest.size();
        for (int i = 0; i < ctx.op.size(); i++) {
            var op = ctx.op.get(i);
            var rhs = (Expression) visit(ctx.rest.get(i));

            var expressionOP = switch (op.getType())
            {
                case LanguageLexer.AND -> Expression.Operation.AND;
                case LanguageLexer.OR -> Expression.Operation.OR;
                default -> throw new IllegalArgumentException("unhandled expression operation " + op);
            };
            var loc = value.getLocation().span(rhs.getLocation());
            value = new Expression(loc, expressionOP, (Expression) value, rhs);
        }
        return value;
    }

    @Override
    public Tree visitRelationalExpression(LanguageParser.RelationalExpressionContext ctx) {
        var value = visit(ctx.initial);

        assert ctx.op.size()==ctx.rest.size();
        for (int i = 0; i < ctx.op.size(); i++) {
            var op = ctx.op.get(i);
            var rhs = (Expression) visit(ctx.rest.get(i));

            var expressionOP = switch (op.getType())
            {
                case LanguageLexer.LT -> Expression.Operation.LES;
                case LanguageLexer.LTE -> Expression.Operation.LEQ;
                case LanguageLexer.NEQ -> Expression.Operation.NEQ;
                case LanguageLexer.EQ -> Expression.Operation.EQL;
                case LanguageLexer.GT -> Expression.Operation.GRE;
                case LanguageLexer.GTE -> Expression.Operation.GRQ;
                default -> throw new IllegalArgumentException("unhandled expression operation " + op);
            };
            var loc = value.getLocation().span(rhs.getLocation());
            value = new Expression(loc, expressionOP, (Expression) value, rhs);
        }
        return value;
    }

    @Override
    public Tree visitAdditionalExpression(LanguageParser.AdditionalExpressionContext ctx) {
        var value = (Expression) visit(ctx.initial);

        assert ctx.op.size() == ctx.rest.size();
        for (int i = 0; i < ctx.op.size(); i++) {
            var op = ctx.op.get(i);
            var rhs = (Expression) visit(ctx.rest.get(i));

            var exprOp = switch (op.getType()) {
                case LanguageLexer.PLUS -> Expression.Operation.ADD;
                case LanguageLexer.MINUS -> Expression.Operation.SUB;
                default -> throw new IllegalArgumentException("unhandled expr op " + op);
            };

            var loc = value.getLocation().span(rhs.getLocation());
            value = new Expression(loc, exprOp, value, rhs);
        }
        return value;
    }

    @Override
    public Tree visitMultiplicationExpression(LanguageParser.MultiplicationExpressionContext ctx) {
        var value = (Expression) visit(ctx.initial);

        assert ctx.op.size() == ctx.rest.size();
        for (int i = 0; i < ctx.op.size(); i++) {
            var op = ctx.op.get(i);
            var rhs = (Expression) visit(ctx.rest.get(i));

            var exprOp = switch (op.getType()) {
                case LanguageLexer.MUL -> Expression.Operation.MUL;
                case LanguageLexer.DIV -> Expression.Operation.DIV;
                default -> throw new IllegalArgumentException("unhandled expr op " + op);
            };

            var loc = value.getLocation().span(rhs.getLocation());
            value = new Expression(loc, exprOp, value, rhs);
        }
        return value;
    }

    @Override
    public Tree visitAtom(LanguageParser.AtomContext ctx) {
        if(ctx.primary() != null)
            return visit(ctx.primary());
        else if(ctx.arrayAccess() != null)
            return visit(ctx.arrayAccess());
        else if(ctx.functionCall() != null)
            return visit(ctx.functionCall());
        throw new IllegalStateException("Unexpected atom type.");
    }

    @Override
    public Tree visitArgumentList(LanguageParser.ArgumentListContext ctx) {
        List<Expression> expressions = new ArrayList<>();

        // Iteriraj kroz sve izraze u argumentList
        for (LanguageParser.ExpressionContext exprCtx : ctx.getRuleContexts(LanguageParser.ExpressionContext.class)) {
            // Poseti svaki izraz i kastuj ga u Expression
            Expression expr = (Expression) visit(exprCtx);
            expressions.add(expr);
        }

        // Vraćanje izraza kao deo neke druge strukture, zavisno od tvoje implementacije
        return new ArgumentList(getLocation(ctx), expressions);  // ArgumentList je pretpostavljeni tip

    }

    @Override
    public Tree visitReturnStatement(LanguageParser.ReturnStatementContext ctx) {
        var op = (Expression) visit(ctx.expression());
        return new ReturnStatement(getLocation((TerminalNode) ctx.RETURN()), op);
    }

    @Override
    public Tree visitArrayAccess(LanguageParser.ArrayAccessContext ctx) {
        var location = getLocation(ctx);
        var id = lookup(location, ctx.ID().getText());
        if (id.isEmpty()) new ErrorExpression(location);
        var expressions = ctx.expression().stream().map(this::visit).map(x -> (Expression)x).toList();
        return new ArrayAccess(getLocation(ctx), id.get(), expressions);

    }

    @Override
    public Tree visitFunctionCall(LanguageParser.FunctionCallContext ctx) {
        var name = ctx.ID();
        List<Expression> arguments = new ArrayList<>();
        for (LanguageParser.ExpressionContext exprCtx : ctx.argumentList().expression()) {
            arguments.add((Expression) visit(exprCtx));  // Svaki argument
        }        var location = getLocation(ctx);
        return lookupFunctionCall(location, name.getText()).map(decl -> (Tree) new FunctionCall(location, decl.getName(), arguments)).orElseGet(() -> new ErrorExpression(location));
    }

    @Override
    public Tree visitInputStatement(LanguageParser.InputStatementContext ctx) {
        var args = ctx.expression()
                /* Take all the parsed arguments, ... */
                .stream()
                /* ... visit them using this visitor, ... */
                .map(this::visit)
                /* ... then cast them to expressions, ...  */
                .map(x -> (Expression) x)
                /* ... and put them into a list.  */
                .toList();
        return new InputStatement(getLocation(ctx), args);
    }

    @Override
    public Tree visitPrintStatement(LanguageParser.PrintStatementContext ctx) {
        List<Expression> args = new ArrayList<>();

        if (ctx.argumentList() != null) {
            // Prolazimo kroz sve argumente u argumentList
            // Za svaki ArgumentListContext, prolazimo kroz sve izraze
            for (LanguageParser.ExpressionContext exprCtx : ctx.argumentList().expression()) {
                // Posetiti svaki izraz i dodati ga u listu argumenata
                Expression expr = (Expression) visit(exprCtx);
                args.add(expr);
            }
        }

        // Vratiti novi PrintStatement sa argumentima
        return new PrintStatement(getLocation(ctx), args);
    }



    @Override
    public Tree visitVariableReference(LanguageParser.VariableReferenceContext ctx) {
        var location = getLocation(ctx);
        var id = lookup(location, ctx.getText());
        if (id.isEmpty()) new ErrorExpression(location);

        return id.get();
    }

    @Override
    public Tree visitNumberLiteral(LanguageParser.NumberLiteralContext ctx) {
        var number = new NumberLiteral(getLocation(ctx), Double.parseDouble(ctx.getText()));
        number.setType(new NumberType());
        number.setResultType(new NumberType());
        return number;
    }

    @Override
    public Tree visitLogicalValue(LanguageParser.LogicalValueContext ctx) {
        var logVal = new LogicalValue(getLocation(ctx), ctx.getText());
        logVal.setBooleanType(new BooleanType());
        logVal.setResultType(new BooleanType());
        return logVal;
    }

    @Override
    public Tree visitGroupingOperator(LanguageParser.GroupingOperatorContext ctx) {
        var expressions = ctx.expression()
                /* Take all the parsed arguments, ... */
                .stream()
                /* ... visit them using this visitor, ... */
                .map(this::visit)
                /* ... then cast them to expressions, ...  */
                .map(x -> (Expression) x)
                /* ... and put them into a list.  */
                .toList();
        return new GroupingExpression(getLocation(ctx), expressions);
    }


    /* Helpers.  */

    /**
     * Returns the range that this subtree is in.
     */
    private static Location getLocation(ParserRuleContext context) {
        return getLocation(context.getStart())
                .span(getLocation(context.getStop()));
    }

    /**
     * Returns the location this terminal is in.
     */
    private static Location getLocation(TerminalNode term) {
        return getLocation(term.getSymbol());
    }

    /**
     * Returns the location this token is in.
     */
    private static Location getLocation(Token token) {
        /* The token starts at the position ANTLR provides us.  */
        var start = new Position(token.getLine(), token.getCharPositionInLine());

        /* But it does not provide a convenient way to get where it ends, so we
           have to calculate it based on length.  */
        assert !token.getText().contains("\n")
                : "CSTtoASTConverter assumes single-line tokens";
        var length = token.getText().length();
        assert length > 0;

        /* And then put it together.  */
        var end = new Position(start.line(), start.column() + length - 1);
        return new Location(start, end);
    }

}
