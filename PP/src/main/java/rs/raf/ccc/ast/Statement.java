package rs.raf.ccc.ast;


/** Base class for all statements.  */
public sealed abstract class Statement extends Tree permits AssignmentStatement, Declaration, ForLoopStatement, FunctionDeclaration, IfStatement, InputStatement, PrintStatement, ReturnStatement, StatementList, WhileLoopStatement {
    public Statement(Location location) {
        super(location);
    }
}
