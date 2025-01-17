package rs.raf.ccc.vm;

import lombok.Data;
import rs.raf.ccc.ast.FunctionDeclaration;

/** A VM function.  A function is effectively a blueprint for a closure: it
    specifies which locals of the surrounding context end up in which part of
    the local table for this function.  That local table then gets cloned with
    the arguments to form the actual function local table.
 */
@Data
public class Function {
    private Blob code;
    private UpvalueMapEntry[] upvalueMap;
    private int localCount = -1;
    private FunctionDeclaration funcDecl;
}
