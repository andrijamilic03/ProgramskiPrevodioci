package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class WhileLoopStatement extends Statement {
    private Expression condition;      // Uslov petlje
    private List<Statement> body;      // Telo petlje

    // Konstruktor
    public WhileLoopStatement(Location location, Expression condition, List<Statement> body) {
        super(location);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("while", () -> {
            pp.node("condition", () -> {
                condition.prettyPrint(pp);  // Ispis uslova
            });
            pp.node("body", () -> {
                body.forEach(stmt -> stmt.prettyPrint(pp));  // Ispis tela petlje
            });
        });
    }
}
