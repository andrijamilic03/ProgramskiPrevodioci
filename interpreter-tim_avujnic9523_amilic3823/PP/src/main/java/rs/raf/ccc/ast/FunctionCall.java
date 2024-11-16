package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FunctionCall extends Statement {
    private String functionName;  // Ime funkcije
    private List<Expression> arguments;  // Argumenti koji se prosleđuju funkciji

    // Konstruktor
    public FunctionCall(Location location, String functionName, List<Expression> arguments) {
        super(location);
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("functionCall", () -> {
            pp.node("functionName", () -> pp.terminal(functionName));  // Ispisivanje imena funkcije
            pp.node("arguments", () -> {
                for (Expression argument : arguments) {
                    argument.prettyPrint(pp);  // Ispisivanje svakog argumenta
                }
            });
        });
    }
}
