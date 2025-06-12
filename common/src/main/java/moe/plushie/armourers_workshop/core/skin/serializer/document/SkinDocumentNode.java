package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenUUID;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class SkinDocumentNode implements IDataSerializable.Immutable {

    public static final IDataCodec<SkinDocumentNode> CODEC = IDataCodec.COMPOUND_TAG.serializer(SkinDocumentNode::new);

    private OpenVector3f location = OpenVector3f.ZERO;
    private OpenVector3f rotation = OpenVector3f.ZERO;
    private OpenVector3f scale = OpenVector3f.ONE;
    private OpenVector3f pivot = OpenVector3f.ZERO;
    private OpenTransform3f transform = null;

    private SkinPartType type;
    private SkinDescriptor skin = SkinDescriptor.EMPTY;

    private String name;
    private SkinDocumentNode parent;
    private SkinDocumentListener listener;

    private boolean isEnabled = true;
    private boolean isMirror = false;

    private final String id;
    private final ArrayList<SkinDocumentNode> children = new ArrayList<>();

    private String cachedTypeName;

    public SkinDocumentNode(String name) {
        this(OpenUUID.randomUUIDString(), name);
    }

    public SkinDocumentNode(String id, String name) {
        this(id, name, SkinPartTypes.ADVANCED);
    }

    public SkinDocumentNode(String id, String name, SkinPartType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public SkinDocumentNode(IDataSerializer serializer) {
        this.id = serializer.read(CodingKeys.UID);
        this.name = serializer.read(CodingKeys.NAME);
        this.type = serializer.read(CodingKeys.TYPE);
        this.skin = serializer.read(CodingKeys.SKIN);
        this.location = serializer.read(CodingKeys.LOCATION);
        this.rotation = serializer.read(CodingKeys.ROTATION);
        this.scale = serializer.read(CodingKeys.SCALE);
        this.pivot = serializer.read(CodingKeys.PIVOT);
        this.children.addAll(serializer.read(CodingKeys.CHILDREN));
        this.isEnabled = serializer.read(CodingKeys.ENABLED);
        this.isMirror = serializer.read(CodingKeys.MIRROR);
        // restore node hierarchy depend.
        this.children.forEach(it -> it.parent = this);
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.UID, id);
        serializer.write(CodingKeys.NAME, name);
        serializer.write(CodingKeys.TYPE, type);
        serializer.write(CodingKeys.SKIN, skin);
        serializer.write(CodingKeys.LOCATION, location);
        serializer.write(CodingKeys.ROTATION, rotation);
        serializer.write(CodingKeys.SCALE, scale);
        serializer.write(CodingKeys.PIVOT, pivot);
        serializer.write(CodingKeys.CHILDREN, children);
        serializer.write(CodingKeys.ENABLED, isEnabled);
        serializer.write(CodingKeys.MIRROR, isMirror);
    }

    public void deserialize(IDataSerializer serializer) {
        var newSkin = serializer.read(CodingKeys.INC_SKIN);
        if (newSkin != null) {
            skin = newSkin;
        }
        var newLocation = serializer.read(CodingKeys.INC_LOCATION);
        if (newLocation != null) {
            location = newLocation;
            transform = null;
        }
        var newRotation = serializer.read(CodingKeys.INC_ROTATION);
        if (newRotation != null) {
            rotation = newRotation;
            transform = null;
        }
        var newScale = serializer.read(CodingKeys.INC_SCALE);
        if (newScale != null) {
            scale = newScale;
            transform = null;
        }
        var newPivot = serializer.read(CodingKeys.INC_PIVOT);
        if (newPivot != null) {
            pivot = newPivot;
            transform = null;
        }
        var newName = serializer.read(CodingKeys.INC_NAME);
        if (newName != null) {
            name = newName;
        }
        var newEnabled = serializer.read(CodingKeys.INC_ENABLED);
        if (newEnabled != null) {
            isEnabled = newEnabled;
        }
        var newMirror = serializer.read(CodingKeys.INC_MIRROR);
        if (newMirror != null) {
            isMirror = newMirror;
        }
    }

    public void applyChanges(CompoundTag tag) {
        deserialize(new TagSerializer(tag));
        listener.documentDidUpdateNode(this, tag);
    }

    public void add(SkinDocumentNode node) {
        if (node.parent != null) {
            node.removeFromParent();
        }
        children.add(node);
        node.parent = this;
        node.setListener(listener);
        if (listener != null) {
            listener.documentDidInsertNode(this, node, -1);
        }
    }

    public void insertAtIndex(SkinDocumentNode node, int index) {
        if (node.parent != null) {
            node.removeFromParent();
        }
        children.add(index, node);
        node.parent = this;
        node.setListener(listener);
        if (listener != null) {
            listener.documentDidInsertNode(this, node, index);
        }
    }

    public void moveTo(SkinDocumentNode node, int toIndex) {
        var index = children.indexOf(node);
        if (index < 0 || index == toIndex) {
            return;
        }
        children.remove(index);
        children.add(OpenMath.clamp(toIndex, 0, children.size()), node);
        if (listener != null) {
            listener.documentDidMoveNode(this, node, toIndex);
        }
    }

    public void removeFromParent() {
        if (parent == null) {
            return;
        }
        if (listener != null) {
            listener.documentDidRemoveNode(this);
        }
        parent.children.remove(this);
        parent = null;
        setListener(null);
    }

    public void setName(String value) {
        name = value;
        cachedTypeName = null;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_NAME, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public String name() {
        if (name != null) {
            return name;
        }
        if (cachedTypeName != null) {
            return cachedTypeName;
        }
        var lhs = TranslateUtils.title("documentType.armourers_workshop.node.root");
        var rhs = TranslateUtils.title("documentType.armourers_workshop.node." + id);
        if (type != SkinPartTypes.ADVANCED) {
            rhs = TranslateUtils.Name.of("documentType.armourers_workshop.node", type);
        }
        cachedTypeName = TranslateUtils.title("documentType.armourers_workshop.node", lhs, rhs).getString();
        return cachedTypeName;
    }

    public void setType(SkinPartType type) {
        this.type = type;
        this.cachedTypeName = null;
    }

    public SkinPartType type() {
        return type;
    }

    public void setSkin(SkinDescriptor value) {
        skin = value;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_SKIN, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public SkinDescriptor skin() {
        return skin;
    }

    public void setLocation(OpenVector3f value) {
        location = value;
        transform = null;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_LOCATION, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public OpenVector3f location() {
        return location;
    }

    public void setRotation(OpenVector3f value) {
        rotation = value;
        transform = null;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_ROTATION, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public OpenVector3f rotation() {
        return rotation;
    }

    public void setScale(OpenVector3f value) {
        scale = value;
        transform = null;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_SCALE, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public OpenVector3f scale() {
        return scale;
    }

    public void setPivot(OpenVector3f value) {
        pivot = value;
        transform = null;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_PIVOT, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public OpenVector3f pivot() {
        return pivot;
    }


    public OpenTransform3f transform() {
        if (transform != null) {
            return transform;
        }
        var translate = this.location;
        var pivot = this.pivot;
        if (!translate.equals(OpenVector3f.ZERO)) {
            translate = new OpenVector3f(-translate.x(), -translate.y(), translate.z());
        }
        if (!pivot.equals(OpenVector3f.ZERO)) {
            pivot = new OpenVector3f(-pivot.x(), -pivot.y(), pivot.z());
        }
        var scale = this.scale;
        if (isMirror) {
            scale = scale.scaling(-1, 1, 1);
        }
        transform = OpenTransform3f.create(translate, rotation, scale, pivot, OpenVector3f.ZERO);
        return transform;
    }

    public String id() {
        return id;
    }

    public void setEnabled(boolean value) {
        isEnabled = value;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_ENABLED, value);
            listener.documentDidUpdateNode(this, builder.tag());

        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setMirror(boolean value) {
        isMirror = value;
        transform = null;
        if (listener != null) {
            var builder = new TagSerializer();
            builder.write(CodingKeys.INC_MIRROR, value);
            listener.documentDidUpdateNode(this, builder.tag());
        }
    }

    public boolean isMirror() {
        return isMirror;
    }

    public boolean isLocked() {
        return name == null;
    }

    public boolean isStatic() {
        return type == SkinPartTypes.ADVANCED_STATIC;
    }

    public boolean isFloat() {
        return type == SkinPartTypes.ADVANCED_FLOAT;
    }

    public boolean isLocator() {
        return type == SkinPartTypes.ADVANCED_LOCATOR;
    }


    public boolean isBasic() {
        return type != SkinPartTypes.ADVANCED && type != SkinPartTypes.ADVANCED_STATIC && type != SkinPartTypes.ADVANCED_FLOAT && type != SkinPartTypes.ADVANCED_LOCATOR;
    }

    public SkinDocumentNode parent() {
        return parent;
    }

    public ArrayList<SkinDocumentNode> children() {
        return children;
    }

    protected void setListener(SkinDocumentListener listener) {
        this.listener = listener;
        this.children.forEach(it -> it.setListener(listener));
    }

    protected SkinDocumentListener listener() {
        return listener;
    }

//    protected boolean equalsStruct(SkinDocumentNode node) {
//        int childSize = children.size();
//        if (!this.id.equals(node.id) || childSize != node.children.size()) {
//            return false;
//        }
//        for (int i = 0; i < childSize; ++i) {
//            SkinDocumentNode leftChild = children.get(i);
//            SkinDocumentNode rightChild = node.children.get(i);
//            if (!leftChild.equalsStruct(rightChild)) {
//                return false;
//            }
//        }
//        return true;
//    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> UID = IDataSerializerKey.create("UID", IDataCodec.STRING, "");
        public static final IDataSerializerKey<String> NAME = IDataSerializerKey.create("Name", IDataCodec.STRING, null);
        public static final IDataSerializerKey<SkinPartType> TYPE = IDataSerializerKey.create("Type", SkinPartTypes.CODEC, SkinPartTypes.ADVANCED);
        public static final IDataSerializerKey<SkinDescriptor> SKIN = IDataSerializerKey.create("Skin", SkinDescriptor.CODEC, SkinDescriptor.EMPTY);
        public static final IDataSerializerKey<OpenVector3f> LOCATION = IDataSerializerKey.create("Location", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<OpenVector3f> ROTATION = IDataSerializerKey.create("Rotation", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<OpenVector3f> SCALE = IDataSerializerKey.create("Scale", OpenVector3f.CODEC, OpenVector3f.ONE);
        public static final IDataSerializerKey<OpenVector3f> PIVOT = IDataSerializerKey.create("Pivot", OpenVector3f.CODEC, OpenVector3f.ZERO);
        public static final IDataSerializerKey<List<SkinDocumentNode>> CHILDREN = IDataSerializerKey.create("Children", SkinDocumentNode.CODEC.listOf(), Collections.emptyList());
        public static final IDataSerializerKey<Boolean> ENABLED = IDataSerializerKey.create("Enabled", IDataCodec.BOOL, true);
        public static final IDataSerializerKey<Boolean> MIRROR = IDataSerializerKey.create("Mirror", IDataCodec.BOOL, false);

        public static final IDataSerializerKey<String> INC_NAME = IDataSerializerKey.create("Name", IDataCodec.STRING, null);
        public static final IDataSerializerKey<SkinDescriptor> INC_SKIN = IDataSerializerKey.create("Skin", SkinDescriptor.CODEC, null);
        public static final IDataSerializerKey<OpenVector3f> INC_LOCATION = IDataSerializerKey.create("Location", OpenVector3f.CODEC, null);
        public static final IDataSerializerKey<OpenVector3f> INC_ROTATION = IDataSerializerKey.create("Rotation", OpenVector3f.CODEC, null);
        public static final IDataSerializerKey<OpenVector3f> INC_SCALE = IDataSerializerKey.create("Scale", OpenVector3f.CODEC, null);
        public static final IDataSerializerKey<OpenVector3f> INC_PIVOT = IDataSerializerKey.create("Pivot", OpenVector3f.CODEC, null);
        public static final IDataSerializerKey<Boolean> INC_ENABLED = IDataSerializerKey.create("Enabled", IDataCodec.BOOL, null);
        public static final IDataSerializerKey<Boolean> INC_MIRROR = IDataSerializerKey.create("Mirror", IDataCodec.BOOL, null);
    }
}
