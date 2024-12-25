package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class IfStatement extends Statement {
    private Expression condition;  // Usmerivač uslova (expression)
    private List<Statement> thenStatements; // Telo if bloka
    private List<Statement> elseStatements; // Telo else bloka (opciono)

    // Konstruktor
    public IfStatement(Location location, Expression condition, List<Statement> thenStatements, List<Statement> elseStatements) {
        super(location);
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elseStatements = elseStatements;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("if", () -> {
            condition.prettyPrint(pp);  // Ispis uslova
            pp.node("then", () -> {
                thenStatements.forEach(stmt -> stmt.prettyPrint(pp));  // Ispis then bloka
            });
            if (elseStatements != null) {
                pp.node("else", () -> {
                    elseStatements.forEach(stmt -> stmt.prettyPrint(pp));  // Ispis else bloka
                });
            }
        });
    }
}
