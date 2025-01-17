package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class Atom extends Expression{
    private Expression primary;
    private FunctionCall functionCall;
    private ArrayAccess arrayAccess;

    public Atom(Location location, Expression primary, FunctionCall functionCall, ArrayAccess arrayAccess) {
        super(location);
        this.primary = primary;
        this.functionCall = functionCall;
        this.arrayAccess = arrayAccess;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp)
    {
        if(primary != null && functionCall == null && arrayAccess == null)
            primary.prettyPrint(pp);
        else if(primary == null && functionCall != null && arrayAccess == null)
            functionCall.prettyPrint(pp);
        else if(primary == null && functionCall == null && arrayAccess != null)
            arrayAccess.prettyPrint(pp);
    }
}
