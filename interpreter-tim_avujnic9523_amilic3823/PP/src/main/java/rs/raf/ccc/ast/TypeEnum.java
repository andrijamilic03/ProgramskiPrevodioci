package rs.raf.ccc.ast;

public enum TypeEnum {
    INT("int"),
    BOOL("bool");

    private final String typeName;

    TypeEnum(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}

