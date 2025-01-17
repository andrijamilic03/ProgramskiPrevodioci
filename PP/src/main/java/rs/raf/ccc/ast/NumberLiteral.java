package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class NumberLiteral extends Expression{
    private double value;
    private NumberType type;

    protected NumberLiteral(Location location, double value) {
        super(location);
        this.value=value;
        this.type = new NumberType();
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp)
    {
        pp.node("broj/velicina niza", () -> pp.terminal(Objects.toString(value)));
    }
}
