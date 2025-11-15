package moe.plushie.armourers_workshop.core.skin.attachment;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;
import java.util.Map;

public class SkinAttachmentManager {

    private final Map<SkinAttachmentType, SkinAttachmentPose> fallbacks = new HashMap<>();
    private final Map<SkinAttachmentType, Int2ObjectMap<SkinAttachmentPose>> variants = new HashMap<>();

    public void put(SkinAttachmentType attachmentType, int index, SkinAttachmentPose pose) {
        if (index < 0) {
            fallbacks.put(attachmentType, pose);
        } else {
            variants.computeIfAbsent(attachmentType, it -> new Int2ObjectOpenHashMap<>()).put(index, pose);
        }
    }

    public SkinAttachmentPose get(SkinAttachmentType attachmentType, int index) {
        var map = variants.get(attachmentType);
        if (map != null) {
            var value = map.get(index);
            if (value != null) {
                return value;
            }
        }
        return fallbacks.get(attachmentType);
    }

    public Int2ObjectMap<SkinAttachmentPose> get(SkinAttachmentType attachmentType) {
        return variants.get(attachmentType);
    }

    public void clear() {
        fallbacks.clear();
        variants.clear();
    }
}
