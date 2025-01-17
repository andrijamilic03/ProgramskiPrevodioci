package rs.raf.ccc.ast;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode

public class BooleanType implements Type {
    @Override
    public String userReadableName() {
        return "bool";
    }
}
