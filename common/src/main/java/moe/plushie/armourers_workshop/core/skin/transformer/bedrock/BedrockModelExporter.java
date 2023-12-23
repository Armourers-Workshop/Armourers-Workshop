package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
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
import moe.plushie.armourers_workshop.utils.math.Size3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import moe.plushie.armourers_workshop.utils.texture.TextureBox;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BedrockModelExporter {

    protected Vector3f baseOrigin = Vector3f.ZERO;

    protected SkinSettings settings = new SkinSettings();
    protected SkinProperties properties = new SkinProperties();
    protected SkinItemTransforms itemTransforms = new SkinItemTransforms();

    protected Node rootNode = new Node("", null, null);
    protected HashMap<String, Node> namedNodes = new HashMap<>();
    protected ArrayList<Pair<String, Node>> allNodes = new ArrayList<>();
    protected Map<String, ISkinPartType> mapper = PARTS;

    public void add(BedrockModelBone bone, BedrockModelTexture texture) {
        Node node = new Node(bone.getName(), bone, texture);
        allNodes.add(Pair.of(bone.getParent(), node));
        namedNodes.put(bone.getName(), node);
    }

    public void add(String name, ITransformf transform) {
        this.itemTransforms.put(name, transform);
    }

    public <T> void add(ISkinProperty<T> property, T value) {
        this.properties.put(property, value);
    }

    public void move(Vector3f offset) {
        this.baseOrigin = offset;
    }

    public Skin export(ISkinType skinType) {
        if (skinType == SkinTypes.ITEM_BOW) {
            mapper = BOW_PARTS;
        }

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
        ArrayList<SkinPart> rootParts = new ArrayList<>();
        for (Node child : rootNode.children) {
            exportSkinPart(child, null, rootParts);
        }

//        ArrayList<ISkinPartType> whitelist = new ArrayList<>();
//        whitelist.add(SkinPartTypes.BIPPED_HEAD);
//        whitelist.add(SkinPartTypes.BIPPED_CHEST);
//        whitelist.add(SkinPartTypes.BIPPED_RIGHT_ARM);
//        whitelist.add(SkinPartTypes.BIPPED_RIGHT_ARM2);
//        rootParts.removeIf(it -> !whitelist.contains(it.getType()));
//        rootParts.removeIf(it -> it.getType() == SkinPartTypes.ADVANCED);

        Skin.Builder builder = createSkin(skinType);

        if (skinType == SkinTypes.ADVANCED || skinType == SkinTypes.OUTFIT || skinType == SkinTypes.ITEM_BOW || skinType instanceof ISkinArmorType) {
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
        if (skinType instanceof ISkinEquipmentType || skinType == SkinTypes.ITEM) {
            settings.setItemTransforms(itemTransforms);
        }
        builder.version(SkinSerializer.Versions.V20);
        return builder.build();
    }

    protected Skin.Builder createSkin(ISkinType skinType) {
        return new Skin.Builder(skinType);
    }

    protected void exportSkinPart(Node node, SkinPart parentPart, Collection<SkinPart> rootParts) {
        IVector3i iv = Vector3i.ZERO;
        IVector3i of = Vector3i.ZERO;
        ISkinPartType partType = mapper.getOrDefault(node.name, SkinPartTypes.ADVANCED);
        if (partType != null && partType != SkinPartTypes.ADVANCED) {
            // new root node
            parentPart = null;
            iv = partType.getRenderOffset();
            of = PART_OFFSETS.getOrDefault(partType, of);
        }
        // new child node
        SkinPart part = exportSkinPart(node, partType, iv, of);
        if (parentPart != null) {
            parentPart.addPart(part);
        } else {
            rootParts.add(part);
        }
        for (Node child : node.children) {
            exportSkinPart(child, part, rootParts);
        }
    }

    protected SkinPart exportSkinPart(Node node, ISkinPartType partType, IVector3i iv, IVector3i of) {
        BedrockModelBone bone = node.bone;

        Vector3f pivot = Vector3f.ZERO;
        Vector3f translate = Vector3f.ZERO;
        Vector3f rotation = Vector3f.ZERO;

        if (bone != null) {
            pivot = convertToLocal(bone.getPivot());
            rotation = bone.getRotation();
        }

        float tx = of.getX() + iv.getX();
        float ty = of.getY() + iv.getY();
        float tz = of.getZ() + iv.getZ();

        translate = new Vector3f(-tx, -ty, -tz);

        SkinCubesV2 cubes = new SkinCubesV2();
        SkinPart.Builder builder = new SkinPart.Builder(partType);

        builder.cubes(cubes);
        builder.transform(SkinTransform.create(Vector3f.ZERO, rotation, Vector3f.ONE, pivot, translate));

        if (bone != null) {
            builder.name(bone.getName());
            for (BedrockModelCube cube : bone.getCubes()) {
                SkinCubesV2.Box entry = exportSkinCube(cube, node.texture);
                cubes.addBox(entry);
            }
        }

        return builder.build();
    }

    protected SkinCubesV2.Box exportSkinCube(BedrockModelCube cube, BedrockModelTexture texture) {
        Vector3f pivot = convertToLocal(cube.getPivot());
        Vector3f translate = Vector3f.ZERO;
        Vector3f rotation = cube.getRotation();

        Vector3f origin = convertToLocal(cube.getOrigin());
        Size3f size = cube.getSize();

        float x = origin.getX();
        float y = origin.getY();
        float z = origin.getZ();

        float w = size.getWidth();
        float h = size.getHeight();
        float d = size.getDepth();

        float delta = cube.getInflate();

        TextureBox skyBox = texture.read(cube);
        if (delta != 0) {
            // after inflate, the cube size and texture size has been diff,
            // so we need to split per-face, it means each face will save separately.
            skyBox = skyBox.separated();
        }
        Rectangle3f rect = new Rectangle3f(x, y, z, w, h, d).inflate(delta);
        SkinTransform transform = SkinTransform.create(Vector3f.ZERO, rotation, Vector3f.ONE, pivot, translate);
        return new SkinCubesV2.Box(rect, transform, skyBox);
    }

    protected Vector3f convertToLocal(Vector3f pos) {
        // ignore, when base origin no change.
        if (!baseOrigin.equals(Vector3f.ZERO)) {
            return pos.subtracting(baseOrigin);
        }
        return pos;
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


    private static final ImmutableMap<String, ISkinPartType> PARTS = new ImmutableMap.Builder<String, ISkinPartType>()
            // cpm
            .put("head_c", SkinPartTypes.BIPPED_HEAD)
            .put("left_arm_c", SkinPartTypes.BIPPED_LEFT_ARM)
            .put("right_arm_c", SkinPartTypes.BIPPED_RIGHT_ARM)
            .put("body_c", SkinPartTypes.BIPPED_CHEST)
            .put("left_leg_c", SkinPartTypes.BIPPED_LEFT_THIGH)
            .put("right_leg_c", SkinPartTypes.BIPPED_RIGHT_THIGH)

            // ??
//            .put("Head", SkinPartTypes.BIPPED_HEAD)
            .put("Body", SkinPartTypes.BIPPED_CHEST)
//            .put("Skirt", SkinPartTypes.BIPPED_SKIRT)
            .put("LeftArm", SkinPartTypes.BIPPED_LEFT_ARM)
            .put("RightArm", SkinPartTypes.BIPPED_RIGHT_ARM)
            .put("LeftForeArm", SkinPartTypes.BIPPED_LEFT_HAND)
            .put("RightForeArm", SkinPartTypes.BIPPED_RIGHT_HAND)
            .put("LeftLeg", SkinPartTypes.BIPPED_LEFT_THIGH)
            .put("RightLeg", SkinPartTypes.BIPPED_RIGHT_THIGH)
            .put("LeftForeLeg", SkinPartTypes.BIPPED_LEFT_LEG)
            .put("RightForeLeg", SkinPartTypes.BIPPED_RIGHT_LEG)
            .put("LeftFoot", SkinPartTypes.BIPPED_LEFT_FOOT)
            .put("RightFoot", SkinPartTypes.BIPPED_RIGHT_FOOT)

            // aw
            .put("Head", SkinPartTypes.BIPPED_HEAD)
            .put("Chest", SkinPartTypes.BIPPED_CHEST)
            .put("Arm_L", SkinPartTypes.BIPPED_LEFT_ARM)
            .put("Arm_R", SkinPartTypes.BIPPED_RIGHT_ARM)
            .put("Foot_L", SkinPartTypes.BIPPED_LEFT_FOOT)
            .put("Foot_R", SkinPartTypes.BIPPED_RIGHT_FOOT)
            .put("Thigh_L", SkinPartTypes.BIPPED_LEFT_THIGH)
            .put("Thigh_R", SkinPartTypes.BIPPED_RIGHT_THIGH)
            .put("Skirt", SkinPartTypes.BIPPED_SKIRT)
            .put("Wing_L", SkinPartTypes.BIPPED_RIGHT_WING)
            .put("Wing_R", SkinPartTypes.BIPPED_LEFT_WING)
            .put("Phalanx_L", SkinPartTypes.BIPPED_RIGHT_PHALANX)
            .put("Phalanx_R", SkinPartTypes.BIPPED_LEFT_PHALANX)
            .put("Torso", SkinPartTypes.BIPPED_TORSO)
            .put("Hand_L", SkinPartTypes.BIPPED_LEFT_HAND)
            .put("Hand_R", SkinPartTypes.BIPPED_RIGHT_HAND)
            .put("Leg_L", SkinPartTypes.BIPPED_LEFT_LEG)
            .put("Leg_R", SkinPartTypes.BIPPED_RIGHT_LEG)
            .build();

    private static final ImmutableMap<String, ISkinPartType> BOW_PARTS = new ImmutableMap.Builder<String, ISkinPartType>()
            .put("Arrow", SkinPartTypes.ITEM_ARROW)
            .put("Frame0", SkinPartTypes.ITEM_BOW0)
            .put("Frame1", SkinPartTypes.ITEM_BOW1)
            .put("Frame2", SkinPartTypes.ITEM_BOW2)
            .put("Frame3", SkinPartTypes.ITEM_BOW3)
            .build();

    private static final ImmutableMap<ISkinPartType, IVector3i> PART_OFFSETS = new ImmutableMap.Builder<ISkinPartType, IVector3i>()
            .put(SkinPartTypes.BIPPED_TORSO, new Vector3i(0, 6, 0))
            .put(SkinPartTypes.BIPPED_LEFT_HAND, new Vector3i(0, 4, 0))
            .put(SkinPartTypes.BIPPED_RIGHT_HAND, new Vector3i(0, 4, 0))
            .put(SkinPartTypes.BIPPED_LEFT_LEG, new Vector3i(0, 6, 0))
            .put(SkinPartTypes.BIPPED_RIGHT_LEG, new Vector3i(0, 6, 0))
            .build();
}
