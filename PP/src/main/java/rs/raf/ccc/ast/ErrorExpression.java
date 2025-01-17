package rs.raf.ccc.ast;

/** A special node indicating an error occurred.  */
public final class ErrorExpression extends Expression {
    protected ErrorExpression(Location location) {
        super(location);
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
	pp.node("error", () -> {});
    }
}
