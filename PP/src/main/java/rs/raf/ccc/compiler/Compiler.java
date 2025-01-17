package rs.raf.ccc.compiler;

import rs.raf.ccc.Language;
import rs.raf.ccc.ast.*;
import rs.raf.ccc.vm.Blob;
import rs.raf.ccc.vm.Function;
import rs.raf.ccc.vm.Instruction;
import rs.raf.ccc.vm.UpvalueMapEntry;

import java.util.IdentityHashMap;
import static rs.raf.ccc.vm.Instruction.Code.*;


public class Compiler {
    /* This class should not emit errors.  */
    private final Language l;

    public Compiler(Language language) {
        this.l = language;
    }

    /** A "sphagetti-stack" of blobs of code.  Each blob of code, except for
        the outermost one, is related to a function, and hence has a table of
        local values, as well as a table of "upvalues", which are variables
        copied from the outer scope.  */
    private InTranslationBlob blob = null;

    private int emit(Instruction.Code opcode) {
        return emit(new Instruction(opcode));
    }

    private int emit(Instruction.Code opcode, int arg1) {
        return emit(new Instruction(opcode, arg1));
    }

    private int emit(Instruction insn) {
        return blob.getCode().addInsn(insn);
    }

    /** Compiles a single global scope statement list and produces a blob of
        code for it that the VM can interpret immediately.  Populates the
        function table if a function is declared within {@code input}.  */
    public Blob compileInput(StatementList input) {
        assert !(l.hadError() || l.hadRuntimeError());
        /* This function should only be called for the global scope.  */
        assert blob == null;
        var outerBlob = new InTranslationBlob(new Blob(),
                                              null,
                                              null,
                                              blob);
        /* Push.  */
        blob = outerBlob;

        compileBlock(input);
        /* Used as a signal to our VM that we're done with the blob.  */
        emit(FINISH_OUTER);

        /* We must've come back down to the bottom of the stack.  */
        assert blob == outerBlob;
        blob = null;

        /* There can't possibly be any locals here.  */
        assert outerBlob.getMaxLocalDepth() == 0;
        return outerBlob.getCode();
    }

    private void compileBlock(StatementList input) {
        var currentBlob = blob;
        var oldLocalDepth = currentBlob.getLocalDepth();

        for (var statement : input.getStmts())
            compileStatement(statement);

        blob.setLocalDepth(oldLocalDepth);
        assert currentBlob == blob;
    }

    private Instruction declareVariable(Declaration declaration) {
        if (blob.getPreviousBlob() == null) {
            /* New global variable.  */
            return new Instruction(SET_GLOBAL, l.declareGlobal(declaration));
        } else {
            var newVarId = blob.getLocalDepth();
            blob.setLocalDepth(newVarId + 1);
            var oldId = blob.getLocalSlots().put(declaration, newVarId);
            assert oldId == null : "how did you redeclare it??";
            return new Instruction(SET_LOCAL, newVarId);
        }
    }

    private int compileFunction(FunctionDeclaration fn) {
        var function = new Function();
        function.setFuncDecl(fn);
        var functionBlob = new InTranslationBlob(new Blob(),
                                                 new IdentityHashMap<>(),
                                                 new IdentityHashMap<>(),
                                                 blob);
        blob = functionBlob;
        var newFnId = l.addFunction(function);

        /* Declare variables.  */
        //fn.getParams().getArguments().forEach(this::declareVariable);

        /* Compile body.  */
        compileBlock(fn.getStatementList());

        /* Populate the function data.  */
        function.setCode(functionBlob.getCode());
        var upvals = new UpvalueMapEntry[functionBlob.getUpvalSlots().size()];
        function.setUpvalueMap(upvals);
        function.setLocalCount(functionBlob.getMaxLocalDepth());
        functionBlob.getUpvalSlots()
            .values()
            .forEach(s -> { upvals[s.slotNr()] = s.entry(); });

        /* Pop.  */
        blob = blob.getPreviousBlob();
        return newFnId;
    }

    private Instruction findLocalInsn(InTranslationBlob blob,
                                      Declaration decl) {
        /* We already checked globals in getVarInsn.  */
        var locals = blob.getLocalSlots();
        var upvals = blob.getUpvalSlots();
        assert locals != null && upvals != null;

        var local = locals.get(decl);
        if (local != null)
            return new Instruction(GET_LOCAL, local);

        /* So, this is a upvalue.  But is it new?  */
        var upval = blob.getUpvalSlots().get(decl);
        if (upval != null)
            /* No, it isn't.  */
            return new Instruction(GET_UPVALUE, upval.slotNr());

        /* It is.  */
        var inSuperscope = findLocalInsn(blob.getPreviousBlob(), decl);
        var upvalSlot = blob.getUpvalSlots().size();

        var upvalME = new UpvalueMapEntry(switch (inSuperscope.getOpcode()) {
            case GET_LOCAL -> UpvalueMapEntry.UpvalueLocation.LOCAL;
            case GET_UPVALUE -> UpvalueMapEntry.UpvalueLocation.UPVALUE;
            default -> throw new IllegalArgumentException();
            }, Math.toIntExact(inSuperscope.getArg1()));

        var oldSlot = blob.getUpvalSlots()
            .put(decl, new InTranslationBlob.UpvalSlotInfo(upvalSlot, upvalME));
        assert oldSlot == null;
        return new Instruction(GET_UPVALUE, upvalSlot);
    }

    private Instruction getVarInsn(Declaration decl) {
        return l.getGlobalSlot(decl)
            .map(s -> new Instruction(GET_GLOBAL, s))
            .orElseGet(() -> findLocalInsn(blob, decl));
    }

    private void compileStatement(Statement stmt) {
        switch (stmt) {

        case PrintStatement print -> {
            /* The VM contains a {@code PRINT} instruction that prints a single
               operand from the operand stack, however our language contains a
               n-ary {@code print} statement.  We must, then, implement the
               {@code print} statement as many {@code PRINT}s.  */
            print.getArgs().forEach
                (expr -> {
                    compileExpr(expr);
                    emit(PRINT);
                });
        }

        case WhileLoopStatement whileStatement -> {
            int loopStart = blob.getCode().code().size();
            compileExpr(whileStatement.getCondition());

            int exitJumpIndex = emit(JUMP_FALSE, -1);
            for (var whileStmt : whileStatement.getBody()) {
                compileBlock((StatementList) whileStmt);
            }

            emit(JUMP, loopStart);
            blob.getCode().code().get(exitJumpIndex)
                    .setArg1(blob.getCode().code().size());
        }

            case IfStatement ifStatement -> {
                compileExpr(ifStatement.getCondition());
                int elseJumpIndex = emit(JUMP_FALSE, -1);
                for (var thenStmt : ifStatement.getThenStatements()) {
                    compileBlock((StatementList) thenStmt);
                }

                int endJumpIndex = -1;
                if (ifStatement.getElseStatements() != null) {
                    endJumpIndex = emit(JUMP, -1);
                }

                blob.getCode().code().get(elseJumpIndex)
                        .setArg1(blob.getCode().code().size());

                if (ifStatement.getElseStatements() != null) {
                    for (var elseStmt : ifStatement.getElseStatements()) {
                        compileBlock((StatementList) elseStmt);
                    }
                    blob.getCode().code().get(endJumpIndex)
                            .setArg1(blob.getCode().code().size());
                }
            }

        case ReturnStatement ret -> {
            if (ret.getValue() != null) {
                compileExpr(ret.getValue());
                emit(RETURN);
            } else
                emit(RETURN_VOID);
        }

        case Declaration decl -> {
            var newVarSetter = declareVariable(decl);
            compileExpr(decl.getName());
            emit(newVarSetter);
        }

        case StatementList block ->
            compileBlock(block);
            default -> throw new IllegalStateException("Unexpected value: " + stmt);
        }
    }

    private void compileExpr(Expression expr) {
        switch (expr) {
        case ErrorExpression ignored -> throw new IllegalStateException();
        case FunctionCall call -> {
            compileExpr(call.getName());
            call.getParams().forEach(this::compileExpr);
            emit(FUNCTION_CALL, call.getParams().size());
        }
        
        case Expression binaryExpr -> {
            /* Must not be a subclass.  */
            assert binaryExpr.getClass() == Expression.class;
            compileExpr(binaryExpr.getLhs());
            compileExpr(binaryExpr.getRhs());
            emit(switch (binaryExpr.getOperation()) {
                case ADD -> ADD;
                case DIV -> DIVIDE;
                case MUL -> MULTIPLY;
                case SUB -> SUBTRACT;
                default -> throw new IllegalArgumentException();
                });
        }
        }
    }
}
