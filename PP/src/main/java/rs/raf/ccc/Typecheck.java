package rs.raf.ccc;

import rs.raf.ccc.ast.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Typecheck {

    private Language l;
    private FunctionDeclaration currentFunction;
    private List<FunctionDeclaration> functions = new ArrayList<>();
    private List<Identifier> enviroments = new ArrayList();

    public Typecheck(Language language) {
        this.l = language;

    }

    private void pushDeclaration(Identifier name)
    {
        enviroments.add(name);
    }

    public void typeCheck(StatementList block) {
        block.getStmts().forEach(this::typeCheck);
    }

    private void typeCheck(Statement statement) {
        switch (statement)
        {
            case PrintStatement stmt -> {
                stmt.getArgs().forEach(this::typeCheck);
            }
            case FunctionDeclaration functionDeclaration -> {
                FunctionDeclaration oldFunction = currentFunction;
                functions.add(functionDeclaration);
                try{
                    currentFunction = functionDeclaration;
                    for(var identifier : functionDeclaration.getParams().keySet())
                    {
                        identifier.setResultType(functionDeclaration.getParams().get(identifier));
                        pushDeclaration(identifier);
                    }
                    if(functionDeclaration.getStatementList() != null)
                    {
                        typeCheck(functionDeclaration.getStatementList());
                        typeCheck(functionDeclaration.getReturnStatement());
                    }
                }
                finally{
                    if(oldFunction != null)
                        currentFunction = oldFunction;
                }
            }
            case StatementList statementList -> {
                typeCheck(statementList);
            }
            case Declaration varDeclaration -> {
                //varDeclaration.setType(varDeclaration.getType());
                var name = varDeclaration.getName();
                var type = varDeclaration.getType();
                name.setResultType(type);
                //pushDeclaration(name);

            }
            case IfStatement ifStatement -> {
                // Provera tipa uslova
                var conditionType = typeCheck(ifStatement.getCondition());
                if (!conditionType.getResultType().equals(l.getBooleanType())) {
                    l.error(ifStatement.getLocation(),
                            "Condition in if statement must be of type 'logical'.");
                }

                // Provera "then" dela
                for (var thenStmt : ifStatement.getThenStatements()) {
                    typeCheck(thenStmt);
                }

                // Provera "else" dela ako postoji
                if (ifStatement.getElseStatements() != null) {
                    for (var elseStmt : ifStatement.getElseStatements()) {
                        typeCheck(elseStmt);
                    }
                }
            }
            case WhileLoopStatement whileStatement -> {
                var condition = typeCheck(whileStatement.getCondition());
                if(!condition.getResultType().equals(l.getBooleanType()))
                    l.error(whileStatement.getLocation(), "Condition in while loop must be of type 'logical'.");

                for (var whileStmt : whileStatement.getBody()) {
                    typeCheck(whileStmt);
                }

            }

            case ReturnStatement returnStatement -> {
                if (currentFunction == null) {
                    l.error(returnStatement.getLocation(),
                            "Return call made outside of function!");
                    break;
                }
                var returnType = typeCheck(returnStatement.getValue());
                if(!returnType.getResultType().equals(currentFunction.getType()))
                    l.error(returnStatement.getLocation(), "Return type mismatch.");
            }
            case AssignmentStatement assignmentStatement -> {
                var lhsE = assignmentStatement.getLhsE();
                var lhsV = assignmentStatement.getLhsV();
                var rhs = typeCheck(assignmentStatement.getRhs());
                if(lhsE == null)
                {
                    typeCheck(lhsV);
                    if(!lhsV.getType().equals(rhs.getResultType()))
                        l.error(assignmentStatement.getLocation(), "Assignment expression mismatch.");
                }
                else
                {
                    lhsE = typeCheck(lhsE);
                    if(!lhsE.getResultType().equals(rhs.getResultType()))
                        l.error(assignmentStatement.getLocation(), "Assignment expression mismatch.");
                }
            }
            case InputStatement scanStatement -> {
            }

            default -> throw new IllegalStateException("Unexpected value: " + statement);
        }
    }

    private Expression typeCheck(Expression expr) {
        switch (expr)
        {
            case ErrorExpression errorExpression ->{
                errorExpression.setResultType(l.getNumberType());
                return errorExpression;
            }
            case NumberLiteral expression -> {
                expression.setResultType(l.getNumberType());
                return expression;
            }
            case LogicalValue expression -> {
                expression.setResultType(l.getBooleanType());
                return expression;
            }
            case Identifier expression -> {
                for (var identifier : enviroments.reversed()) {
                    if (identifier.getIdentifier().equals(expression.getIdentifier())) {
                        return identifier;
                    }
                }
            }
            case FunctionCall functionCall -> {
                FunctionDeclaration functionDeclaration = null;
                for(var func : functions)
                {
                    if(func.getName().getIdentifier().equals(currentFunction.getName().getIdentifier()))
                    {
                        functionDeclaration = func;
                        break;
                    }
                }
                if(functionDeclaration == null)
                    l.error(functionCall.getLocation(),"Function call not found.");

                assert functionDeclaration != null;
                var expectedParams = functionDeclaration.getParams();
                var expectedParamTypes = new ArrayList<>(expectedParams.values());

                var arguments = functionCall.getParams();

                if(arguments.size() != expectedParams.size())
                    l.error(functionCall.getLocation(),"Number of arguments mismatch.");

                for(int i = 0; i < arguments.size(); i++)
                {
                    var argument = typeCheck(arguments.get(i));
                    var expectedType = expectedParamTypes.get(i);

                    if(!argument.getResultType().equals(expectedType))
                        l.error(argument.getLocation(),"Argument types mismatch.");
                }

                functionCall.setResultType(functionDeclaration.getType());

                return functionCall;
            }
            case ArrayAccess arrayAccess -> {
                for (var identifier : enviroments.reversed()) {
                    if (identifier.getIdentifier().equals(arrayAccess.getName().getIdentifier())) {
                        arrayAccess.setResultType(identifier.getResultType());
                        if(arrayAccess.getExpressions().size() != identifier.getDimension().size())
                            l.error(arrayAccess.getLocation(),"Number of dimensions mismatch.");
                        for(Expression expression : arrayAccess.getExpressions())
                        {
                            if(expression.getResultType().equals(l.getBooleanType()))
                            {
                                l.error(arrayAccess.getLocation(), "Cannot access an array with a boolean!");
                            }
                        }
                        /*
                         * Ovde bismo proveravali slučaj u kom je neki indeks van dimenzija niza, međutim, trenutno to
                         * možemo uraditi samo ako bi izraz koji prosleđujemo kao indeks bio klase Number, jer za veće izraze
                         * još nemamo podešavanje njihovih vrednosti, to ide u međukodu u kasnijoj fazi projekta
                         * */
                    }
                }

                return arrayAccess;
            }
            case GroupingExpression groupingExpression -> {
                groupingExpression.setResultType(groupingExpression.getExpressionList().get(0).getResultType());
                if(groupingExpression.getExpressionList().size() > 1)
                {
                    if(!groupingExpression.getExpressionList().get(1).getResultType().equals(groupingExpression.getResultType()))
                        groupingExpression.setResultType(new ArrayType(new NumberType())); //Ne poklapaju nam se svi tipovi u grupi, pa stavljamo tip koji se ne koristi nigde kao neku null vrednost ali bez greške
                }
                return groupingExpression;
            }
            case Expression expression -> {

            }
        }
        switch (expr.getOperation())
        {
            case ADD, DIV, MUL, SUB-> {
                Expression expression = typeCheck(expr.getLhs());
                expr.setLhs(expression);
                expr.setRhs(typeCheck(expr.getRhs()));

                expr.setLhs(tryAndConvert(l.getNumberType(), expr.getLhs()));
                expr.setRhs(tryAndConvert(l.getNumberType(), expr.getRhs()));

                expr.setResultType(l.getNumberType());
                return expr;
            }
            case LEQ, LES, GRE, GRQ -> {
                Expression expression = typeCheck(expr.getLhs());
                expr.setLhs(expression);
                expr.setRhs(typeCheck(expr.getRhs()));

                expr.setLhs(tryAndConvert(l.getNumberType(), expr.getLhs()));
                expr.setRhs(tryAndConvert(l.getNumberType(), expr.getRhs()));

                expr.setResultType(l.getBooleanType());
                return expr;
            }
            case OR, AND, NOT -> {
                expr.setLhs(typeCheck(expr.getLhs()));
                expr.setRhs(typeCheck(expr.getRhs()));

                expr.setLhs(tryAndConvert(l.getBooleanType(), expr.getLhs()));
                expr.setRhs(tryAndConvert(l.getBooleanType(), expr.getRhs()));

                expr.setResultType(l.getBooleanType());
                return expr;
            }
            case EQL, NEQ -> {
                expr.setLhs(typeCheck(expr.getLhs()));
                expr.setRhs(typeCheck(expr.getRhs()));

                if(!(expr.getLhs().getResultType().equals(expr.getRhs().getResultType())))
                {
                    l.error(expr.getLocation(), "Incompatible types in the expression!");
                }

                expr.setResultType(l.getBooleanType());
                return expr;
            }
            case VALUE -> {
                throw new IllegalStateException();
            }
        }

        throw new IllegalStateException();
    }

    private Expression tryAndConvert(Type to, Expression expr) {
        var resultType = expr.getResultType();
        assert expr.getResultType() != null;

        if(!resultType.equals(to))
        {
            l.error(expr.getLocation(), "Cannot use a value of type %s where type %s is needed", expr.getResultType().userReadableName(), to.userReadableName());
        }
        return expr;
    }


}
