package rs.raf.ccc.ast;

public class ArrayType implements Type {
    private Type elementType;

    public ArrayType(Type elementType) {
        this.elementType = elementType;
    }

    @Override
    public String userReadableName() {
        return elementType.userReadableName() + "[]";
    }
}
