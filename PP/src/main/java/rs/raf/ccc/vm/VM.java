package rs.raf.ccc.vm;

import rs.raf.ccc.Language;
import java.util.ArrayList;


public class VM {
    private Language l;

    public VM(Language context) {
        this.l = context;
    }

    private ArrayList<Value> globals = new ArrayList<>();

    public void run(Blob blob) {
        int ip = 0;
        var callstack = new ArrayList<BlobInvocation>();

        while (globals.size() < l.getGlobalCount())
            globals.add(null);

        /* Prepare the outer invocation.  */
        callstack.add(new BlobInvocation(blob));
    }
}
