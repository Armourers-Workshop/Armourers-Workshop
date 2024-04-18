package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.armature.Armature;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV0;
import moe.plushie.armourers_workshop.core.skin.cube.impl.SkinCubesV2;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import manifold.ext.rt.api.auto;

public class BedrockModelExporter {

    protected SkinSettings settings = new SkinSettings();
    protected SkinProperties properties = new SkinProperties();
    protected SkinItemTransforms itemTransforms;

    protected Node rootNode = new Node("", null, null);
    protected HashMap<String, Node> namedNodes = new HashMap<>();
    protected ArrayList<Pair<String, Node>> allNodes = new ArrayList<>();

    protected boolean keepItemTransforms = false;

    public void add(BedrockModelBone bone, BedrockModelTexture texture) {
        Node node = new Node(bone.getName(), bone, texture);
        allNodes.add(Pair.of(bone.getParent(), node));
        namedNodes.put(bone.getName(), node);
    }

    public void add(String name, ITransformf transform) {
        if (itemTransforms == null) {
            itemTransforms = new SkinItemTransforms();
        }
        // for identity transform, since it's the default value, we don't need to save it.
        if (!transform.isIdentity()) {
            itemTransforms.put(name, transform);
        }
    }

    public <T> void add(ISkinProperty<T> property, T value) {
        this.properties.put(property, value);
    }

    public Skin export(ISkinType skinType) {
        // build the bone child-parent relationships tree.
        allNodes.forEach(it -> {
            String parentName = it.getKey();
            if (parentName == null || parentName.isEmpty() || parentName.equalsIgnoreCase("none")) {
                rootNode.add(it.getValue());
                return;
            }
            Node parent = namedNodes.get(parentName);
            if (parent == null) {
                parent = new Node(parentName, null, null);
                rootNode.add(parent);
                namedNodes.put(parentName, parent);
            }
            parent.add(it.getValue());
        });

        // start export all skin parts.
        Mapper mapper = Mapper.of(skinType);
        ArrayList<SkinPart> rootParts = new ArrayList<>();
        for (Node child : rootNode.children) {
            exportSkinPart(child, null, rootParts, mapper);
        }

//        ArrayList<ISkinPartType> whitelist = new ArrayList<>();
//        whitelist.add(SkinPartTypes.BIPPED_HEAD);
//        whitelist.add(SkinPartTypes.BIPPED_CHEST);
//        whitelist.add(SkinPartTypes.BIPPED_RIGHT_ARM);
//        whitelist.add(SkinPartTypes.BIPPED_RIGHT_ARM2);
//        rootParts.removeIf(it -> !whitelist.contains(it.getType()));
//        rootParts.removeIf(it -> it.getType() == SkinPartTypes.ADVANCED);

        auto builder = createSkin(skinType);

        if (skinType == SkinTypes.ADVANCED || skinType == SkinTypes.OUTFIT || skinType == SkinTypes.ITEM_BOW || skinType == SkinTypes.ITEM_FISHING || skinType instanceof ISkinArmorType) {
            builder.parts(rootParts);
        } else {
            if (skinType.getParts().size() == 1) {
                SkinPart.Builder builder1 = new SkinPart.Builder(skinType.getParts().get(0));
                builder1.cubes(new SkinCubesV0(0));
                //builder1.transform(SkinBasicTransform.createScaleTransform(1, 1, 1));
                SkinPart rootPart = builder1.build();
                rootParts.forEach(rootPart::addPart);
                builder.parts(Collections.singleton(rootPart));
            } else {
                builder.parts(rootParts);
            }
        }

        builder.settings(settings);
        builder.properties(properties);
        if (isKeepItemTransforms()) {
            settings.setItemTransforms(itemTransforms);
        }
        builder.version(SkinSerializer.Versions.V20);
        return builder.build();
    }

    protected Skin.Builder createSkin(ISkinType skinType) {
        return new Skin.Builder(skinType);
    }

    protected void exportSkinPart(Node node, SkinPart parentPart, Collection<SkinPart> rootParts, Mapper mapper) {
        auto origin = Vector3f.ZERO;
        auto entry = mapper.get(node.name);
        if (entry.isRootPart()) {
            // new root node
            parentPart = null;
            origin = node.bone.getPivot();
        }
        // new child node
        auto part = exportSkinPart(node, origin, entry);
        if (parentPart != null) {
            parentPart.addPart(part);
        } else {
            rootParts.add(part);
        }
        for (Node child : node.children) {
            exportSkinPart(child, part, rootParts, mapper);
        }
    }

    protected SkinPart exportSkinPart(Node node, Vector3f origin, Mapper.Entry entry) {
        auto bone = node.bone;

        auto pivot = Vector3f.ZERO;
        auto translate = origin.subtracting(entry.getOffset()).scaling(-1);
        auto rotation = Vector3f.ZERO;

        if (bone != null && !entry.isRootPart()) {
            pivot = bone.getPivot();
            rotation = bone.getRotation();
        }

        auto cubes = new SkinCubesV2();
        auto builder = new SkinPart.Builder(entry.getType());

        builder.cubes(cubes);
        builder.transform(SkinTransform.create(Vector3f.ZERO, rotation, Vector3f.ONE, pivot, translate));

        if (bone != null) {
            builder.name(bone.getName());
            for (BedrockModelCube cube : bone.getCubes()) {
                auto box = exportSkinCube(cube, node.texture);
                cubes.addBox(box);
            }
        }

        return builder.build();
    }

    protected SkinCubesV2.Box exportSkinCube(BedrockModelCube cube, BedrockModelTexture texture) {
        auto pivot = cube.getPivot();
        auto translate = Vector3f.ZERO;
        auto rotation = cube.getRotation();

        auto origin = cube.getOrigin();
        auto size = cube.getSize();

        float x = origin.getX();
        float y = origin.getY();
        float z = origin.getZ();

        float w = size.getWidth();
        float h = size.getHeight();
        float d = size.getDepth();

        float inflate = cube.getInflate();

        auto skyBox = texture.read(cube);
        if (inflate != 0) {
            // after inflate, the cube size and texture size has been diff,
            // so we need to split per-face, it means each face will save separately.
            skyBox = skyBox.separated();
        }

        auto rect = new Rectangle3f(x, y, z, w, h, d).inflate(inflate);
        auto transform = SkinTransform.create(Vector3f.ZERO, rotation, Vector3f.ONE, pivot, translate);
        return new SkinCubesV2.Box(rect, transform, skyBox);
    }

    public boolean isKeepItemTransforms() {
        return keepItemTransforms;
    }

    public void setKeepItemTransforms(boolean keepItemTransforms) {
        this.keepItemTransforms = keepItemTransforms;
    }

    public static class Node {

        public final String name;
        public final BedrockModelBone bone;
        public final BedrockModelTexture texture;
        public final ArrayList<Node> children = new ArrayList<>();

        public Node(String name, BedrockModelBone bone, BedrockModelTexture texture) {
            this.bone = bone;
            this.name = name;
            this.texture = texture;
        }

        public void add(Node node) {
            children.add(node);
        }
    }

    public static class Mapper {

        private final Function<String, Entry> provider;

        public Mapper(Function<String, Entry> provider) {
            this.provider = provider;
        }

        public static Mapper of(ISkinType skinType) {
            // read bow item
            if (skinType == SkinTypes.ITEM_BOW) {
                return of(BOW_PARTS);
            }
            // read fishing item
            if (skinType == SkinTypes.ITEM_FISHING) {
                return of(FINISHING_PARTS);
            }
            // read from armature
            return of(Armatures.byType(skinType));
        }

        public static Mapper of(Armature armature) {
            return new Mapper(name -> {
                auto joint = armature.getJoint(name);
                if (joint != null) {
                    auto partType = armature.getPartType(joint);
                    if (partType != null) {
                        return new Entry(joint, partType);
                    }
                }
                return null;
            });
        }

        public static Mapper of(Map<String, ISkinPartType> map) {
            return new Mapper(name -> {
                auto partType = map.get(name);
                if (partType != null) {
                    return new Entry(null, partType);
                }
                return null;
            });
        }

        public Entry get(String name) {
            auto entry = provider.apply(name);
            if (entry != null) {
                return entry;
            }
            return Entry.NONE;
        }

        public static class Entry {

            public static final Entry NONE = new Entry(null, SkinPartTypes.ADVANCED);

            private final ISkinPartType type;

            public Entry(IJoint joint, ISkinPartType type) {
                this.type = type;
            }

            public boolean isRootPart() {
                return type != SkinPartTypes.ADVANCED;
            }

            public Vector3f getOffset() {
                if (type == SkinPartTypes.BIPPED_CHEST || type == SkinPartTypes.BIPPED_TORSO) {
                    return new Vector3f(0, 6, 0);
                }
                return Vector3f.ZERO;
            }

            public ISkinPartType getType() {
                return type;
            }
        }

        private static final ImmutableMap<String, ISkinPartType> BOW_PARTS = new ImmutableMap.Builder<String, ISkinPartType>()
                .put("Arrow", SkinPartTypes.ITEM_ARROW)
                .put("Frame0", SkinPartTypes.ITEM_BOW0)
                .put("Frame1", SkinPartTypes.ITEM_BOW1)
                .put("Frame2", SkinPartTypes.ITEM_BOW2)
                .put("Frame3", SkinPartTypes.ITEM_BOW3)
                .build();

        private static final ImmutableMap<String, ISkinPartType> FINISHING_PARTS = new ImmutableMap.Builder<String, ISkinPartType>()
                .put("Hook", SkinPartTypes.ITEM_FISHING_HOOK)
                .put("Frame0", SkinPartTypes.ITEM_FISHING_ROD)
                .put("Frame1", SkinPartTypes.ITEM_FISHING_ROD1)
                .build();
    }
}
