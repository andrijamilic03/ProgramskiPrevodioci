package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** A variable declaration, including support for array declarations. */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Declaration extends Statement {
    private String name;
    private Type type;       // Custom enum or class Type (int, bool)
    private Expression value;      // For single variable declarations
    private Integer arraySize; // Optional size for array declarations
    private List<Expression> arrayValues; // Optional initializer list for array

    // Constructor for single variable declaration
    public Declaration(Location location, Type type, String name, Expression value) {
        super(location);
        this.type = type;
        this.name = name;
        this.value = value;
        this.arraySize = null;
        this.arrayValues = null;
    }

    // Constructor for array declaration
    public Declaration(Location location, Type type, String name, Integer arraySize, List<Expression> arrayValues) {
        super(location);
        this.type = type;
        this.name = name;
        this.arraySize = arraySize;
        this.arrayValues = arrayValues;
        this.value = null;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        if (arraySize != null) {
            pp.node("array declaration of %s[%d]".formatted(name, arraySize),
                    () -> {
                        if (arrayValues != null) {
                            arrayValues.forEach(val -> val.prettyPrint(pp));
                        }
                    });
        } else {
            pp.node("declaration of %s".formatted(name),
                    () -> {
                        if (value != null) {
                            value.prettyPrint(pp);
                        }
                    });
        }
    }
}
