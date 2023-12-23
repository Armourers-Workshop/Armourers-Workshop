package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.SkinUUID;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class SkinDocumentNode {

    private Vector3f location = Vector3f.ZERO;
    private Vector3f rotation = Vector3f.ZERO;
    private Vector3f scale = Vector3f.ONE;

    private ISkinPartType type = SkinPartTypes.ADVANCED;
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
        this(SkinUUID.randomUUIDString(), name);
    }

    public SkinDocumentNode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SkinDocumentNode(CompoundTag tag) {
        this.id = tag.getString(Keys.UID);
        this.name = tag.getOptionalString(Keys.NAME, null);
        this.type = tag.getOptionalType(Keys.TYPE, SkinPartTypes.ADVANCED, SkinPartTypes::byName);
        this.skin = tag.getOptionalSkinDescriptor(Keys.SKIN);
        this.location = tag.getOptionalVector3f(Keys.LOCATION, Vector3f.ZERO);
        this.rotation = tag.getOptionalVector3f(Keys.ROTATION, Vector3f.ZERO);
        this.scale = tag.getOptionalVector3f(Keys.SCALE, Vector3f.ONE);
        if (tag.contains(Keys.CHILDREN)) {
            ListTag listTag = tag.getList(Keys.CHILDREN, Constants.TagFlags.COMPOUND);
            int count = listTag.size();
            for (int i = 0; i < count; ++i) {
                SkinDocumentNode node = new SkinDocumentNode(listTag.getCompound(i));
                node.parent = this;
                this.children.add(node);
            }
        }
        this.isEnabled = tag.getOptionalBoolean(Keys.ENABLED, true);
        this.isMirror = tag.getOptionalBoolean(Keys.MIRROR, false);
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
        int index = children.indexOf(node);
        if (index < 0 || index == toIndex) {
            return;
        }
        children.remove(index);
        children.add(MathUtils.clamp(toIndex, 0, children.size()), node);
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
            CompoundTag tag = new CompoundTag();
            tag.putOptionalString(Keys.NAME, value, null);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        if (cachedTypeName != null) {
            return cachedTypeName;
        }
        Component lhs = TranslateUtils.title("documentType.armourers_workshop.node.root");
        Component rhs = TranslateUtils.title("documentType.armourers_workshop.node." + id);
        if (type != SkinPartTypes.ADVANCED) {
            rhs = TranslateUtils.Name.of(type);
        }
        cachedTypeName = TranslateUtils.title("documentType.armourers_workshop.node", lhs, rhs).getString();
        return cachedTypeName;
    }

    public void setType(ISkinPartType type) {
        this.type = type;
        this.cachedTypeName = null;
    }

    public ISkinPartType getType() {
        return type;
    }

    public void setSkin(SkinDescriptor value) {
        skin = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putOptionalSkinDescriptor(Keys.SKIN, value, null);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public SkinDescriptor getSkin() {
        return skin;
    }

    public void setLocation(Vector3f value) {
        location = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putOptionalVector3f(Keys.LOCATION, value, null);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setRotation(Vector3f value) {
        rotation = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putOptionalVector3f(Keys.ROTATION, value, null);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setScale(Vector3f value) {
        scale = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putOptionalVector3f(Keys.SCALE, value, null);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public Vector3f getScale() {
        return scale;
    }

    public String getId() {
        return id;
    }

    public void setEnabled(boolean value) {
        isEnabled = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(Keys.ENABLED, value);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setMirror(boolean value) {
        isMirror = value;
        if (listener != null) {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(Keys.MIRROR, value);
            listener.documentDidUpdateNode(this, tag);
        }
    }

    public boolean isMirror() {
        return isMirror;
    }

    public boolean isLocked() {
        return name == null;
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

    protected SkinDocumentListener getListener() {
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

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(Keys.UID, id);
        tag.putOptionalString(Keys.NAME, name, null);
        tag.putOptionalType(Keys.TYPE, type, SkinPartTypes.ADVANCED);
        tag.putOptionalSkinDescriptor(Keys.SKIN, skin);
        tag.putOptionalVector3f(Keys.LOCATION, location, Vector3f.ZERO);
        tag.putOptionalVector3f(Keys.ROTATION, rotation, Vector3f.ZERO);
        tag.putOptionalVector3f(Keys.SCALE, scale, Vector3f.ONE);
        if (children.size() != 0) {
            ListTag listTag = new ListTag();
            children.forEach(it -> listTag.add(it.serializeNBT()));
            tag.put(Keys.CHILDREN, listTag);
        }
        tag.putOptionalBoolean(Keys.ENABLED, isEnabled, true);
        tag.putOptionalBoolean(Keys.MIRROR, isMirror, false);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        SkinDescriptor newSkin = tag.getOptionalSkinDescriptor(Keys.SKIN, null);
        if (newSkin != null) {
            skin = newSkin;
        }
        Vector3f newLocation = tag.getOptionalVector3f(Keys.LOCATION, null);
        if (newLocation != null) {
            location = newLocation;
        }
        Vector3f newRotation = tag.getOptionalVector3f(Keys.ROTATION, null);
        if (newRotation != null) {
            rotation = newRotation;
        }
        Vector3f newScale = tag.getOptionalVector3f(Keys.SCALE, null);
        if (newScale != null) {
            scale = newScale;
        }
        String newName = tag.getOptionalString(Keys.NAME, null);
        if (newName != null) {
            name = newName;
        }
        if (tag.contains(Keys.ENABLED)) {
            isEnabled = tag.getBoolean(Keys.ENABLED);
        }
        if (tag.contains(Keys.MIRROR)) {
            isMirror = tag.getBoolean(Keys.MIRROR);
        }
        listener.documentDidUpdateNode(this, tag);
    }

    public static class Keys {
        public static final String UID = "UID";
        public static final String NAME = "Name";
        public static final String TYPE = "Type";
        public static final String SKIN = "Skin";
        public static final String LOCATION = "Location";
        public static final String ROTATION = "Rotation";
        public static final String SCALE = "Scale";
        public static final String CHILDREN = "Children";
        public static final String ENABLED = "Enabled";
        public static final String MIRROR = "Mirror";
    }
}
