package rs.raf.ccc.ast;


import ccc.parser.LanguageParser;
import ccc.parser.LanguageVisitor;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CSTtoASTConverter extends AbstractParseTreeVisitor<Tree> implements LanguageVisitor<Tree> {


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
                .toList();
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
        if (substatement instanceof Expression e) {
            /* It's an expression statement.  */
            substatement = new ExpressionStatement(e.getLocation(), e);
        }
        return (Statement) substatement;

    }

    @Override
    public Tree visitDeclaration(LanguageParser.DeclarationContext ctx) {

        if (ctx.arrayDeclaration() != null) {
            // Proverava da li je arrayDeclaration prisutan i poseti ga
            return visitArrayDeclaration(ctx.arrayDeclaration());
        }

        // Izvlači tip promenljive
        rs.raf.ccc.ast.Type type = (rs.raf.ccc.ast.Type) visit(ctx.type());

        // Izvlači ime promenljive
        String name = ctx.ID().getText();

        // Proverava da li postoji dodeljena vrednost
        Expression value = ctx.expression() != null ? (Expression) visit(ctx.expression()) : null;

        // Kreira instancu klase Declaration
        return new Declaration(getLocation(ctx), type, name, value);

    }

    @Override
    public Tree visitArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx) {
        // Prvo uzimamo osnovne informacije
        String type = ctx.type().getText();  // Tip niza
        String name = ctx.ID().getText();    // Ime niza
        int size = Integer.parseInt(ctx.INT_LIT().getText());  // Veličina niza

        // Lista za elemente niza
        List<Expression> elements = new ArrayList<>();

        // Ako postoji dodela vrednosti (ASSIGN { ... })
        if (ctx.LBRACE() != null && ctx.RBRACE() != null) {
            // Obrada izraza unutar { ... }
            for (LanguageParser.ExpressionContext exprCtx : ctx.expression()) {
                // Obradjujemo svaki izraz
                Expression expr = (Expression) visit(exprCtx);  // Pozivamo visit na svakom izrazu
                elements.add(expr);  // Dodajemo element niza
            }
        }

        // Kreiramo i vraćamo novu ArrayDeclaration instancu
        return new ArrayDeclaration(getLocation(ctx), type, name, size, elements);
    }





    @Override
    public Tree visitIfStatement(LanguageParser.IfStatementContext ctx) {
        // Prvo, posetimo uslov koji je između LPAREN i RPAREN
        Expression condition = (Expression) visit(ctx.expression());

        // Telo 'then' bloka (svi statement-ovi unutar prve grane)
        List<Statement> thenStatements = ctx.statement().stream()
                .filter(statement -> ctx.ELSE() == null || ctx.statement().indexOf(statement) < ctx.statement().size() - 1)
                .map(this::visit)
                .map(x -> (Statement) x)
                .collect(Collectors.toList());

        // Telo 'else' bloka (ako postoji ELSE)
        List<Statement> elseStatements = null;
        if (ctx.ELSE() != null) {
            // Ako postoji ELSE, obradimo sve statement-ove u else delu
            elseStatements = ctx.statement().stream()
                    .filter(statement -> ctx.statement().indexOf(statement) == ctx.statement().size() - 1)
                    .map(this::visit)
                    .map(x -> (Statement) x)
                    .collect(Collectors.toList());
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
        // Parsiramo inicijalizaciju, ako postoji
        Statement initialization = null;
        if (ctx.declaration() != null) {
            initialization = (Statement) visit(ctx.declaration());
        } else if (ctx.expression(0) != null) {
            initialization = (Statement) visit(ctx.expression(0));
        }

        // Parsiramo uslov (condition), ako postoji
        Expression condition = null;
        if (ctx.expression(1) != null) {
            condition = (Expression) visit(ctx.expression(1));
        }

        // Parsiramo inkrement (increment), ako postoji
        Statement increment = null;
        if (ctx.expression(2) != null) {
            increment = (Statement) visit(ctx.expression(2));
        }

        // Parsiramo telo petlje (body), koje sadrži više izjava
        List<Statement> body = ctx.statement().stream()
                .map(this::visit)
                .map(stmt -> (Statement) stmt)
                .collect(Collectors.toList());

        // Kreiramo AST za ForLoopStatement
        return new ForLoopStatement(getLocation(ctx), initialization, condition, increment, body);
    }

    @Override
    public Tree visitWhileLoop(LanguageParser.WhileLoopContext ctx) {
        // Uslov while petlje
        Expression condition = (Expression) visit(ctx.expression());  // Uslov petlje

        // Telo petlje (mogu biti više izjava unutar bloka)
        List<Statement> body = ctx.statement().stream()
                .map(this::visit)
                .map(x -> (Statement) x)
                .collect(Collectors.toList());

        // Kreiraj WhileLoopStatement objekat
        return new WhileLoopStatement(getLocation(ctx), condition, body);
    }

    @Override
    public Tree visitFunctionDeclaration(LanguageParser.FunctionDeclarationContext ctx) {
        // Posetite tip povratne vrednosti
        rs.raf.ccc.ast.Type returnType = (rs.raf.ccc.ast.Type) visit(ctx.type());

        // Ime funkcije
        String functionName = ctx.ID().getText();

        // Posetite parametre funkcije
        FunctionParameters parameters = (FunctionParameters) visit(ctx.parameterList());

        // Posetite telo funkcije (izjave)
        List<Statement> body = new ArrayList<>();
        for (LanguageParser.StatementContext stmtCtx : ctx.statement()) {
            body.add((Statement) visit(stmtCtx));
        }

        // Posetite return izjavu i dodajte je u telo funkcije
        if (ctx.expression() != null) {  // Verifikujte da postoji expression nakon return
            Expression returnExpr = (Expression) visit(ctx.expression());

            // Dodajte return u telo funkcije
            body.add(new ExpressionStatement(getLocation(ctx),returnExpr));
        }

        // Kreirajte objekat funkcije i vratite ga
        return new FunctionDeclaration(getLocation(ctx), returnType, functionName, parameters.getParameters(), body);
    }

    @Override
    public Tree visitParameterList(LanguageParser.ParameterListContext ctx) {
        List<Parameter> parameters = new ArrayList<>();

        // Posetite svaki parametar u listi
        for (LanguageParser.ParameterContext paramCtx : ctx.parameter()) {
            parameters.add((Parameter) visit(paramCtx)); // Posetite parametar i dodajte ga u listu
        }


        // Vratite objekat sa listom parametara
        return new FunctionParameters(getLocation(ctx), parameters);
    }

    @Override
    public Tree visitParameter(LanguageParser.ParameterContext ctx) {
        // Posetite tip parametra
        rs.raf.ccc.ast.Type type = (rs.raf.ccc.ast.Type) visit(ctx.type());

        // Ime parametra
        String paramName = ctx.ID().getText();


        // Vratite objekat parametra
        return new Parameter(getLocation(ctx), type, paramName);
    }

    @Override
    public Tree visitType(LanguageParser.TypeContext ctx) {
        // Dobavljamo tip podatka kao tekst (npr. "int" ili "bool")
        String typeName = ctx.getText();

        // Vraćamo odgovarajući Type sa TypeEnum
        if (typeName.equals("int")) {
            return new rs.raf.ccc.ast.Type(getLocation(ctx), TypeEnum.INT);
        } else if (typeName.equals("bool")) {
            return new rs.raf.ccc.ast.Type(getLocation(ctx), TypeEnum.BOOL);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + typeName);
        }
    }

    @Override
    public Tree visitExpression(LanguageParser.ExpressionContext ctx) {
        // Handling binary expressions
        if (ctx.getChildCount() == 3 && (ctx.PLUS() != null || ctx.MINUS() != null || ctx.AND() != null ||
                ctx.OR() != null || ctx.EQ() != null || ctx.NEQ() != null ||
                ctx.LT() != null || ctx.GT() != null)) {
            // Get left and right expressions
            Expression left = (Expression) visit(ctx.expression(0));  // Left expression
            Expression right = (Expression) visit(ctx.expression(1)); // Right expression

            // Verify that left and right expressions are not null
            if (left == null || right == null) {
                throw new NullPointerException("Left or right expression is null in binary expression. " +
                        "Left: " + left + ", Right: " + right + ", Operator: " + ctx.getChild(1).getText());
            }

            // Determine the operator
            String operator = ctx.getChild(1).getText();

            // Return a BinaryExpression
            return new Expression.BinaryExpression(getLocation(ctx), left, operator, right);
        }

        // Handling unary expressions (e.g., NOT, INCREMENT, DECREMENT)
        if (ctx.getChildCount() == 2 && (ctx.NOT() != null || ctx.INCREMENT() != null || ctx.DECREMENT() != null)) {
            // Get the expression
            Expression expr = (Expression) visit(ctx.expression(0));

            if (expr == null) {
                throw new NullPointerException("Expression is null in unary expression.");
            }

            // Determine the operator
            String operator = ctx.getChild(0).getText();

            // Return a UnaryExpression
            return new Expression.UnaryExpression(getLocation(ctx), operator, expr);
        }

        // Handling literal expressions (INT_LIT, BOOL_LIT)
        if (ctx.INT_LIT() != null) {
            // Parse the integer literal
            int value = Integer.parseInt(ctx.INT_LIT().getText());
            return new Expression.LiteralExpression(getLocation(ctx), value);
        }
        if (ctx.BOOL_LIT() != null) {
            // Parse the boolean literal
            boolean value = Boolean.parseBoolean(ctx.BOOL_LIT().getText());
            return new Expression.LiteralExpression(getLocation(ctx), value);
        }
        if (ctx.arrayAccess() != null) {
            return visit(ctx.arrayAccess());
        }

        // Handling array access expressions (e.g., ID[expression])
        if (ctx.ID() != null && ctx.getText().contains("[")) {
            // Get the array name
            String arrayName = ctx.ID().getText();
            // Get the index expression
            Expression index = (Expression) visit(ctx.expression(0));

            // Return an ArrayAccessExpression
            return new Expression.ArrayAccessExpression(getLocation(ctx), arrayName, index);
        }

        // Handling variable reference expressions (ID)
        if (ctx.ID() != null && ctx.LPAREN() == null && ctx.RPAREN() == null) {
            // Get the variable name
            String name = ctx.ID().getText();

            // Return a VariableReferenceExpression
            return new Expression.VariableReferenceExpression(getLocation(ctx), name);
        }

        // Handling grouping expressions (e.g., (expression))
        if (ctx.LPAREN() != null && ctx.RPAREN() != null) {
            // Get the grouped expression
            Expression groupedExpr = (Expression) visit(ctx.expression(0));

            // Return a GroupingExpression
            return new Expression.GroupingExpression(getLocation(ctx), groupedExpr);
        }

        // If none of the above cases match, return null or throw an exception
        return null;
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
    public Tree visitArrayAccess(LanguageParser.ArrayAccessContext ctx) {

        String arrayName = ctx.ID().getText(); // Ime niza
        Expression indexExpression = (Expression) visit(ctx.expression()); // Poseti indeks izraza

        // Kreiranje AST čvora za pristup nizu
        return new Expression.ArrayAccessExpression(getLocation(ctx), arrayName, indexExpression);

    }

    @Override
    public Tree visitFunctionCall(LanguageParser.FunctionCallContext ctx) {
        // Uzmi ime funkcije (ID)
        String functionName = ctx.ID().getText();

        // Poseti argumente funkcije
        List<Expression> arguments = new ArrayList<>();
        for (LanguageParser.ExpressionContext exprCtx : ctx.expression()) {
            arguments.add((Expression) visit(exprCtx));  // Svaki argument
        }


        // Kreiraj objekat FunctionCall i vrati ga
        return new FunctionCall(getLocation(ctx), functionName, arguments);
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
            for (LanguageParser.ArgumentListContext argCtx : ctx.argumentList()) {
                // Za svaki ArgumentListContext, prolazimo kroz sve izraze
                for (LanguageParser.ExpressionContext exprCtx : argCtx.expression()) {
                    // Posetiti svaki izraz i dodati ga u listu argumenata
                    Expression expr = (Expression) visit(exprCtx);
                    args.add(expr);
                }
            }
        }

        // Vratiti novi PrintStatement sa argumentima
        return new PrintStatement(getLocation(ctx), args);
    }



    @Override
    public Tree visitNumberConstant(LanguageParser.NumberConstantContext ctx) {
        /* Each labeled alternative gets its own visitor, making this quite
           convenient.  */
        return new Expression.LiteralExpression(getLocation(ctx), Integer.parseInt(ctx.getText()));
    }

    @Override
    public Tree visitVariableReference(LanguageParser.VariableReferenceContext ctx) {
        return new Expression.VariableReferenceExpression(getLocation(ctx), ctx.ID().getText());
    }

    @Override
    public Tree visitGroupingOperator(LanguageParser.GroupingOperatorContext ctx) {

        Expression groupedExpr = (Expression) visit(ctx.expression(0));

        // Return a GroupingExpression
        return new Expression.GroupingExpression(getLocation(ctx), groupedExpr);
    }


    /* Helpers.  */
    /** Returns the range that this subtree is in.  */
    private static Location getLocation(ParserRuleContext context) {
        return getLocation(context.getStart())
                .span(getLocation(context.getStop ()));
    }

    /** Returns the location this terminal is in.  */
    private static Location getLocation(TerminalNode term) {
        return getLocation(term.getSymbol());
    }

    /** Returns the location this token is in.  */
    private static Location getLocation(Token token) {
        /* The token starts at the position ANTLR provides us.  */
        var start = new Position(token.getLine(), token.getCharPositionInLine());

        /* But it does not provide a convenient way to get where it ends, so we
           have to calculate it based on length.  */
        assert !token.getText ().contains ("\n")
                : "CSTtoASTConverter assumes single-line tokens";
        var length = token.getText ().length ();
        assert length > 0;

        /* And then put it together.  */
        var end = new Position (start.line (), start.column () + length - 1);
        return new Location (start, end);
    }

}
