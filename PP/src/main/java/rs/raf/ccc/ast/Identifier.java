package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public final class Identifier extends Expression
{
    private String identifier;
    private Type type;
    private List<NumberLiteral> dimension;
    private List<Objects> elements;

    public Identifier(Location location, String identifier, Type type) {
        super(location);
        this.identifier = identifier;
        this.type = type;
        dimension = new ArrayList<>();
        elements = new ArrayList<>();
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp)
    {
        pp.node("naziv:", () -> pp.terminal(identifier));
    }
}
