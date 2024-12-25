package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Type extends Tree {
    private TypeEnum typeEnum;

    public Type(Location location, TypeEnum typeEnum) {
        super(location);
        this.typeEnum = typeEnum;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.terminal(typeEnum.getTypeName());
    }
}
