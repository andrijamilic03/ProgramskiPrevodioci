package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/** A variable declaration, including support for array declarations. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class Declaration extends Statement {

    private Type type;
    private Identifier name;
    private List<NumberLiteral> dimensions;

    public Declaration(Location location, Type type, Identifier name, List<NumberLiteral> arraySize) {
        super(location);
        this.type = type;
        this.name = name;
        this.dimensions = arraySize;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter astPrettyPrint) {
        astPrettyPrint.node("deklarizacija promenljive tipa %s".formatted(type.userReadableName()),
                () -> {
                    name.prettyPrint(astPrettyPrint);
                    if(dimensions != null && !dimensions.isEmpty())
                        astPrettyPrint.node("dimenzije niza/matrice", () -> dimensions.forEach(x-> x.prettyPrint(astPrettyPrint)));
                });
    }
}
