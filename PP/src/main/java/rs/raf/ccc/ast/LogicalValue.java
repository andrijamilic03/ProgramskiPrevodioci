package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class LogicalValue extends Expression{
    private String value;
    private BooleanType booleanType;

    public LogicalValue(Location location, String value) {
        super(location);
        this.value = value;
        this.booleanType = new BooleanType();
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp)
    {
        pp.node("vrednost:", () -> pp.terminal(value));
    }
}
