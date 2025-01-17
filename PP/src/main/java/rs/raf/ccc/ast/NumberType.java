package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/** A type for all numeric values.  */
@Getter
@Setter
@EqualsAndHashCode
public class NumberType implements Type {
    @Override
    public String userReadableName() {
        return "int";
    }
}
