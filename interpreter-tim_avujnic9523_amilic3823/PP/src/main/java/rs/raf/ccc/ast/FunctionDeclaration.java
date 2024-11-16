package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class FunctionDeclaration extends Statement {
    private Type returnType; // Tip povratne vrednosti
    private String functionName; // Naziv funkcije
    private List<Parameter> parameters; // Lista parametara
    private List<Statement> body; // Telo funkcije

    // Konstruktor
    public FunctionDeclaration(Location location, Type returnType, String functionName, List<Parameter> parameters, List<Statement> body) {
        super(location);
        this.returnType = returnType;
        this.functionName = functionName;
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("function", () -> {
            pp.node("returnType", () -> returnType.prettyPrint(pp));
            pp.node("name", () -> pp.terminal(functionName));
            pp.node("parameters", () -> {
                for (Parameter parameter : parameters) {
                    parameter.prettyPrint(pp);
                }
            });
            pp.node("body", () -> {
                for (Statement stmt : body) {
                    stmt.prettyPrint(pp);
                }

            });
        });
    }
}
