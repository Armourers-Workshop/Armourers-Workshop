package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class DocumentPartMapper {

    private static final Set<SkinType> SINGLE_TYPES = Collections.immutableSet(it -> {
        it.add(SkinTypes.ITEM_SWORD);
        it.add(SkinTypes.ITEM_SHIELD);
        //it.add(SkinTypes.ITEM_BOW);
        it.add(SkinTypes.ITEM_TRIDENT);

        it.add(SkinTypes.ITEM_PICKAXE);
        it.add(SkinTypes.ITEM_AXE);
        it.add(SkinTypes.ITEM_SHOVEL);
        it.add(SkinTypes.ITEM_HOE);

        it.add(SkinTypes.ITEM_FISHING);
        it.add(SkinTypes.ITEM_BACKPACK);

        it.add(SkinTypes.ITEM);
        it.add(SkinTypes.BLOCK);
    });

    private static final Map<String, SkinPartType> BOW_PARTS = Collections.immutableMap(it -> {
        it.put("Arrow", SkinPartTypes.ITEM_ARROW);
        it.put("Frame0", SkinPartTypes.ITEM_BOW0);
        it.put("Frame1", SkinPartTypes.ITEM_BOW1);
        it.put("Frame2", SkinPartTypes.ITEM_BOW2);
        it.put("Frame3", SkinPartTypes.ITEM_BOW3);
    });

    private static final Map<String, SkinPartType> FINISHING_PARTS = Collections.immutableMap(it -> {
        it.put("Hook", SkinPartTypes.ITEM_FISHING_HOOK);
        it.put("Frame0", SkinPartTypes.ITEM_FISHING_ROD);
        it.put("Frame1", SkinPartTypes.ITEM_FISHING_ROD1);
    });

    private final SkinType type;
    private final Function<String, Entry> provider;

    private final Map<Node, Node> overrideNodes = new HashMap<>();

    public DocumentPartMapper(SkinType type, Function<String, Entry> provider) {
        this.type = type;
        this.provider = provider;
        this.setup();
    }

    public static DocumentPartMapper of(SkinType type) {
        // read bow item
        if (type == SkinTypes.ITEM_BOW) {
            return of(type, BOW_PARTS);
        }
        // read fishing item
        if (type == SkinTypes.ITEM_FISHING) {
            return of(type, FINISHING_PARTS);
        }
        // read from armature
        return of(type, Armatures.byType(type));
    }

    private static DocumentPartMapper of(SkinType type, Armature armature) {
        return new DocumentPartMapper(type, name -> {
            var joint = armature.jointByName(name);
            if (joint != null) {
                var partType = armature.typeByJoint(joint);
                if (partType != null) {
                    return new Entry(joint, partType);
                }
            }
            return null;
        });
    }

    private static DocumentPartMapper of(SkinType type, Map<String, SkinPartType> map) {
        return new DocumentPartMapper(type, name -> {
            var partType = map.get(name);
            if (partType != null) {
                return new Entry(null, partType);
            }
            return null;
        });
    }

    public Entry get(String name) {
        var entry = provider.apply(name);
        if (entry != null) {
            return entry;
        }
        return Entry.NONE;
    }


    public Entry root() {
        if (SINGLE_TYPES.contains(type)) {
            for (var partType : type.parts()) {
                return new Entry(null, partType);
            }
        }
        return null;
    }

    public Node resolve(String name, SkinPartType partType) {
        var node = new Node(name, partType);
        var overridedNode = overrideNodes.get(node);
        if (overridedNode != null) {
            return overridedNode;
        }
        var overrideEntry = provider.apply(name);
        if (overrideEntry != null) {
            return new Node(name, overrideEntry.type);
        }
        return node;
    }

    public boolean isEmpty() {
        return false;
    }


    private void setup() {

        register(Node.bone("LeftHandLocator"), SkinAttachmentTypes.LEFT_HAND);
        register(Node.bone("RightHandLocator"), SkinAttachmentTypes.RIGHT_HAND);

        register(Node.bone("LeftShoulderLocator"), SkinAttachmentTypes.LEFT_SHOULDER);
        register(Node.bone("RightShoulderLocator"), SkinAttachmentTypes.RIGHT_SHOULDER);

        register(Node.bone("LeftWaistLocator"), SkinAttachmentTypes.LEFT_WAIST);
        register(Node.bone("RightWaistLocator"), SkinAttachmentTypes.RIGHT_WAIST);

        register(Node.bone("BackpackLocator"), SkinAttachmentTypes.BACKPACK);
        register(Node.bone("ViewLocator"), SkinAttachmentTypes.VIEW);

        register(Node.bone("ElytraLocator"), SkinAttachmentTypes.ELYTRA);
        register(Node.bone("NameLocator"), SkinAttachmentTypes.NAME);

        register(Node.bone("RidingLocator"), SkinAttachmentTypes.RIDING);

        register(Node.locator("hand_l"), SkinAttachmentTypes.LEFT_HAND);
        register(Node.locator("hand_r"), SkinAttachmentTypes.RIGHT_HAND);

        // multiple hands is allowed.
        registerMultiple(9, Node.bone("LeftHandLocator"), SkinAttachmentTypes.LEFT_HAND);
        registerMultiple(9, Node.bone("RightHandLocator"), SkinAttachmentTypes.RIGHT_HAND);

        // multiple riding is allowed.
        registerMultiple(16, Node.bone("RidingLocator"), SkinAttachmentTypes.RIDING);
    }

    // <name>Locator => armourers:<name>
    private void register(Node node, SkinAttachmentType attachmentType) {
        overrideNodes.put(node, Node.locator(attachmentType.registryName().toString()));
    }

    // <name>Locator<index> => armourers:<name>.<index>
    private void registerMultiple(int count, Node node, SkinAttachmentType attachmentType) {
        for (int i = 0; i < count; i++) {
            var s1 = new Node(node.name + (i + 1), node.type);
            var s2 = Node.locator(attachmentType.registryName().toString() + "." + i);
            overrideNodes.put(s1, s2);
        }
    }


    public static class Entry {

        public static final Entry NONE = new Entry(null, SkinPartTypes.ADVANCED);

        private final SkinPartType type;

        public Entry(IJoint joint, SkinPartType type) {
            this.type = type;
        }

        public boolean isRootPart() {
            return type != SkinPartTypes.ADVANCED;
        }

        public OpenVector3f offset() {
            if (type == SkinPartTypes.BIPPED_CHEST || type == SkinPartTypes.BIPPED_TORSO) {
                return new OpenVector3f(0, 6, 0);
            }
            return OpenVector3f.ZERO;
        }

        public SkinPartType type() {
            return type;
        }
    }

    public static class Node {

        private final String name;
        private final SkinPartType type;

        public Node(String name, SkinPartType type) {
            this.name = name;
            this.type = type;
        }

        public static Node bone(String name) {
            return new Node(name, SkinPartTypes.ADVANCED);
        }

        public static Node locator(String name) {
            return new Node(name, SkinPartTypes.ADVANCED_LOCATOR);
        }

        public String name() {
            return name;
        }

        public SkinPartType type() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node node)) return false;
            return Objects.equals(name, node.name) && Objects.equals(type, node.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, type);
        }
    }
}
