package rs.raf.ccc.vm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class BlobInvocation {
    private final List<lombok.Value> operandStack = new ArrayList<>();
    private final Blob blob;
    private final lombok.Value[] upvalues;
    private final Value[] locals;
    private final int prevIp;

    /** Construct a blob invocation for the outermost blob.  It has no locals,
        nor upvalues (duh - there's no up).  */
    public BlobInvocation(Blob blob) {
        this(blob, null, null, -1);
    }
}
