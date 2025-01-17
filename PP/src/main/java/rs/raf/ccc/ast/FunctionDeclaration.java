package rs.raf.ccc.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public final class FunctionDeclaration extends Statement {
    private Type type;
    private Identifier name;
    private Map<Identifier, Type> params;
    private StatementList statementList;
    private ReturnStatement returnStatement;

    public FunctionDeclaration(Location location, Type type, Identifier name, Map<Identifier, Type> params, StatementList statementList, ReturnStatement returnStatement) {
        super(location);
        this.type = type;
        this.name = name;
        this.params = params;
        this.statementList = statementList;
        this.returnStatement = returnStatement;
    }


    @Override
    public void prettyPrint(ASTPrettyPrinter pp) {
        pp.node("deklarizacija funkcije tipa %s".formatted(type.userReadableName()),
                () -> {
                    name.prettyPrint(pp);
                    pp.node("lista parametara:", () -> {
                        if (params != null && !params.isEmpty()) {
                            for (Map.Entry<Identifier, Type> entry : params.entrySet()) {
                                pp.node("parametar tipa %s".formatted(entry.getValue().userReadableName()),
                                        () -> {
                                            entry.getKey().prettyPrint(pp);
                                        }
                                );
                            }
                        }
                    });
                    pp.node("tvrdjenja",() -> statementList.prettyPrint(pp));
                    pp.node("vraca:", () -> returnStatement.prettyPrint(pp));
                });
    }
}
