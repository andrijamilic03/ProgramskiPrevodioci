package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public sealed class Expression extends Tree permits ArrayAccess, Atom, ErrorExpression, FunctionCall, GroupingExpression, Identifier, LogicalValue, NumberLiteral {
    public enum Operation {
        ADD("+"),
        SUB("-"),

        MUL("*"),
        DIV("/"),

        LES("<"),
        LEQ("<="),
        NEQ("!="),
        EQL("=="),
        GRQ(">="),
        GRE(">"),

        OR("||"),
        AND("&&"),
        NOT("!"),

        VALUE(null),
        ;

        public final String label;

        Operation(String label) {
            this.label = label;
        }
    }
    private Operation operation;
    private Expression lhs;
    private Expression rhs;

    private Type resultType;

    public Expression(Location location, Operation operation, Expression lhs, Expression rhs) {
        super(location);
        if (operation == Operation.VALUE)
            throw new IllegalArgumentException("cannot construct a value like that");
        this.operation = operation;
        this.lhs = Objects.requireNonNull(lhs);
        this.rhs = Objects.requireNonNull(rhs);
    }

    protected Expression(Location location)
    {
        super(location);
        this.operation = Operation.VALUE;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node(operation.label,
                () -> {
                    lhs.prettyPrint(pp);
                    rhs.prettyPrint(pp);

                    if(getResultType() != null)
                    {
                        pp.node("tip:",
                                () -> pp.terminal(getResultType().userReadableName()));
                    }
                });
    }
}
