package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class ArrayAccess extends Expression{
    private Identifier name;
    private List<Expression> expressions;

    public ArrayAccess(Location location, Identifier name, List<Expression> expressions) {
        super(location);
        this.name = name;
        this.expressions = expressions;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter astPrettyPrint)
    {
        astPrettyPrint.node("pristup indeksu", () ->
        {
            name.prettyPrint(astPrettyPrint);
            astPrettyPrint.node("pristupni indeksi:", () -> {expressions.forEach(x -> x.prettyPrint(astPrettyPrint));});
        });
    }
}
