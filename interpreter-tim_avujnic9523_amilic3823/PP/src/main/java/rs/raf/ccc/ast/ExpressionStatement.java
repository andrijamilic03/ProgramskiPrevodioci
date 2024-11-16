package rs.raf.ccc.ast;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
/** A statement that evaluates an expression.  */
public class ExpressionStatement extends Statement {
    private Expression expr;

    public ExpressionStatement(Location location, Expression expr) {
        super(location);
        this.expr = expr;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("expr_stmt", () -> expr.prettyPrint(pp));
    }
}
