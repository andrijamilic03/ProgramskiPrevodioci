package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public final class AssignmentStatement extends Statement{
    private Expression lhsE;
    private Declaration lhsV;
    private Expression rhs;

    public AssignmentStatement(Location location, Expression lhsE, Declaration lhsV, Expression rhs) {
        super(location);
        this.lhsE = lhsE;
        this.lhsV = lhsV;
        this.rhs = rhs;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("dodela vrednosti:",
                () -> {
                    if(lhsE != null)
                        lhsE.prettyPrint(pp);
                    else
                        lhsV.prettyPrint(pp);

                    rhs.prettyPrint(pp);
                }
                );
    }
}
