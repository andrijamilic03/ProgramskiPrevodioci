package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class FunctionCall extends Expression {
    private Type type;
    private Identifier name;
    private List<Expression> params;

    public FunctionCall(Location location, Identifier name, List<Expression> params) {
        super(location);
        this.name = name;
        this.params = params;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp)
    {
        pp.node("poziv funkcije",
                () -> {
                    pp.terminal(name.getIdentifier());
                    pp.node("argumenti:", () -> {params.forEach(x -> x.prettyPrint(pp));
                    });
                }
        );
    }
}
