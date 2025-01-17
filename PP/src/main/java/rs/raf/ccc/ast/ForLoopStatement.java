package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class ForLoopStatement extends Statement {
    private Statement initialization;
    private Expression condition;
    private Statement increment;
    private List<Statement> body;

    // Konstruktor
    public ForLoopStatement(Location location, Statement initialization, Expression condition, Statement increment, List<Statement> body) {
        super(location);
        this.initialization = initialization;
        this.condition = condition;
        this.increment = increment;
        this.body = body;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("for", () -> {
            pp.node("initialization", () -> {
                if (initialization != null) {
                    initialization.prettyPrint(pp);  // Ispis inicijalizacije
                }
            });
            pp.node("condition", () -> {
                if (condition != null) {
                    condition.prettyPrint(pp);  // Ispis uslova
                }
            });
            pp.node("increment", () -> {
                if (increment != null) {
                    increment.prettyPrint(pp);  // Ispis inkrementa, ako postoji
                }
            });
            pp.node("body", () -> {
                body.forEach(stmt -> stmt.prettyPrint(pp));  // Ispis tela petlje
            });
        });
    }
}
