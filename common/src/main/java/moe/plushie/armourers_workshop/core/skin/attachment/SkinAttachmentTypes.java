package moe.plushie.armourers_workshop.core.skin.attachment;

import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.init.ModLog;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.LinkedHashMap;

@SuppressWarnings("unused")
public class SkinAttachmentTypes {

    private static final LinkedHashMap<String, SkinAttachmentType> ALL_ATTACHMENT_TYPES = new LinkedHashMap<>();

    public static final SkinAttachmentType UNKNOWN = register("unknown");

    public static final SkinAttachmentType LEFT_HAND = register("leftHand");
    public static final SkinAttachmentType RIGHT_HAND = register("rightHand");

    public static final SkinAttachmentType LEFT_SHOULDER = register("leftShoulder");
    public static final SkinAttachmentType RIGHT_SHOULDER = register("rightShoulder");

    public static final SkinAttachmentType ELYTRA = register("elytra");
    public static final SkinAttachmentType NAME = register("name");

    public static final SkinAttachmentType RIDING = register("riding");

    public static final SkinAttachmentType LEFT_WAIST = register("leftWaist");
    public static final SkinAttachmentType RIGHT_WAIST = register("rightWaist");

    public static final SkinAttachmentType BACKPACK = register("backpack");

    // https://www.curseforge.com/minecraft/mc-mods/first-person-model
    public static final SkinAttachmentType VIEW = register("view");


    private static SkinAttachmentType register(String name) {
        var attachmentType = new SkinAttachmentType();
        attachmentType.setRegistryName(OpenResourceLocation.create("armourers", name));
        if (ALL_ATTACHMENT_TYPES.containsKey(attachmentType.registryName().toString())) {
            ModLog.warn("A mod tried to register a attachment type with an id that is in use.");
            return attachmentType;
        }
        ALL_ATTACHMENT_TYPES.put(attachmentType.registryName().toString(), attachmentType);
        ModLog.debug("Registering Skin Attachment '{}'", attachmentType.registryName());
        return attachmentType;
    }

    public static SkinAttachmentType byName(String name) {
        if (name.equals("hand_l")) {
            return LEFT_HAND;
        }
        if (name.equals("hand_r")) {
            return RIGHT_HAND;
        }
        return ALL_ATTACHMENT_TYPES.getOrDefault(name, UNKNOWN);
    }

    public static Pair<SkinAttachmentType, Integer> parse(String name) {
        // armourers:<name>.<index>
        var index = name.replaceFirst("^.+?(?:\\.(\\d+))?$", "$1");
        if (index.isEmpty()) {
            return Pair.of(byName(name), -1);
        }
        name = name.replaceFirst("\\.\\d+$", "");
        return Pair.of(byName(name), Integer.parseInt(index));
    }

    public static Collection<SkinAttachmentType> values() {
        return ALL_ATTACHMENT_TYPES.values();
    }
}
