package rs.raf.ccc.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import rs.raf.ccc.ast.Declaration;
import rs.raf.ccc.vm.Blob;
import rs.raf.ccc.vm.UpvalueMapEntry;


import java.util.IdentityHashMap;

@RequiredArgsConstructor
@Getter
public class InTranslationBlob {
    private final Blob code;
    private final IdentityHashMap<Declaration, Integer> localSlots;

    public record UpvalSlotInfo(int slotNr, UpvalueMapEntry entry) {}
    private final IdentityHashMap<Declaration, UpvalSlotInfo> upvalSlots;
    private final InTranslationBlob previousBlob;
    private int localDepth = 0;
    private int maxLocalDepth = 0;

    public void setLocalDepth(int localDepth) {
        if ((this.localDepth = localDepth) > maxLocalDepth)
            maxLocalDepth = localDepth;
    }
}
