package rs.raf.ccc.ast;

import java.util.List;

public class ArrayDeclaration extends Statement {
    private final String type;
    private final String name;
    private final int size;
    private final List<Expression> elements;

    public ArrayDeclaration(Location location, String type, String name, int size, List<Expression> elements) {
        super(location);
        this.type = type;
        this.name = name;
        this.size = size;
        this.elements = elements;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("array", () -> {
            pp.node("name", () -> pp.node(name, () -> {})); // Ispisuje ime niza
            pp.node("type", () -> pp.node(type, () -> {})); // Ispisuje tip niza
            pp.node("size", () -> pp.node(String.valueOf(size), () -> {})); // Ispisuje veličinu niza
            if (!elements.isEmpty()) {
                pp.node("elements", () -> {
                    elements.forEach(element -> element.prettyPrint(pp)); // Ispisuje elemente niza
                });
            }
        });
    }
}
