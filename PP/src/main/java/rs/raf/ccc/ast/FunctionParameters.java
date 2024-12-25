package rs.raf.ccc.ast;

import java.util.List;

public class FunctionParameters extends Tree {
    private List<Parameter> parameters;

    public FunctionParameters(Location location, List<Parameter> parameters) {
        super(location);
        this.parameters = parameters;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("parameters", () -> {
            for (Parameter param : parameters) {
                param.prettyPrint(pp);  // Ispis parametara
            }
        });
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
