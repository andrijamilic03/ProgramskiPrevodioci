package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Parameter extends Tree {
    private Type type; // Tip parametra
    private String name; // Ime parametra

    public Parameter(Location location, Type type, String name) {
        super(location);
        this.type = type;
        this.name = name;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("parameter", () -> {
            pp.node("type", () -> type.prettyPrint(pp));
            pp.node("name", () -> pp.terminal(name));
        });
    }
}
