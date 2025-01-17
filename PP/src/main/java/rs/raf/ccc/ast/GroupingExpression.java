package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
public final class GroupingExpression extends Expression{

    private List<Expression> expressionList;
    public GroupingExpression(Location location, List<Expression> expressionList) {
        super(location);
        this.expressionList = expressionList;
    }

    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("izrazi:",
                () ->
                {
                    for(Expression expression : expressionList)
                    {
                        expression.prettyPrint(pp);
                    }
                });
    }
}
