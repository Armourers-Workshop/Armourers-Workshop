package moe.plushie.armourers_workshop.core.skin.document;

import moe.plushie.armourers_workshop.api.data.IDataSerializer;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.DataSerializerKey;
import moe.plushie.armourers_workshop.utils.DataTypeCodecs;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class SkinDocument {

    private static final DataSerializerKey<SkinDocumentType> TYPE_KEY = DataSerializerKey.create("Type", DataTypeCodecs.SKIN_DOCUMENT_TYPE, SkinDocumentTypes.GENERAL_ARMOR_HEAD);
    private static final DataSerializerKey<SkinDocumentNode> NODES_KEY = DataSerializerKey.create("Nodes", DataTypeCodecs.SKIN_DOCUMENT_NODE, null);
    private static final DataSerializerKey<List<SkinAnimation>> ANIMATIONS_KEY = DataSerializerKey.create("Animations", DataTypeCodecs.SKIN_DOCUMENT_ANIMATION.listOf(), null);
    private static final DataSerializerKey<SkinDocumentSettings> SETTINGS_KEY = DataSerializerKey.create("Settings", DataTypeCodecs.SKIN_DOCUMENT_SETTINGS, null, SkinDocumentSettings::new);
    private static final DataSerializerKey<SkinProperties> PROPERTIES_KEY = DataSerializerKey.create("Properties", DataTypeCodecs.SKIN_PROPERTIES, SkinProperties.EMPTY, SkinProperties::new);

    private SkinDocumentType type;
    private SkinDocumentNode nodes;
    private List<SkinAnimation> animations;

    private SkinProperties properties = new SkinProperties();
    private SkinDocumentSettings settings = new SkinDocumentSettings();

    private final SkinDocumentListeners.Proxy listener = new SkinDocumentListeners.Proxy();

    public SkinDocument() {
        setType(SkinDocumentTypes.GENERAL_ARMOR_HEAD);
    }

    public void reset() {
        this.nodes = _generateDefaultNode(type);
        this.animations = null;
        this.settings = _generateSkinSettings();
        this.settings.setListener(listener);
        this.nodes.setListener(listener);
        this.listener.documentDidChangeType(type);
    }

    public void setType(SkinDocumentType type) {
        this.type = type;
        this.properties = _generateSkinProperties();
        this.reset();
    }

    public void serialize(IDataSerializer serializer) {
        serializer.write(TYPE_KEY, type);
        serializer.write(NODES_KEY, nodes);
        serializer.write(ANIMATIONS_KEY, animations);
        serializer.write(SETTINGS_KEY, settings);
        serializer.write(PROPERTIES_KEY, properties);
    }

    public void deserialize(IDataSerializer serializer) {
        type = serializer.read(TYPE_KEY);
        settings = serializer.read(SETTINGS_KEY);
        properties = serializer.read(PROPERTIES_KEY);
        nodes = serializer.read(NODES_KEY);
        if (nodes == null) {
            nodes = _generateDefaultNode(type);
        }
        animations = serializer.read(ANIMATIONS_KEY);
        settings.setListener(listener);
        nodes.setListener(listener);
        listener.documentDidReload();
    }

    public void updateSettings(CompoundTag tag) {
        settings.deserializeNBT(tag);
        listener.documentDidChangeSettings(tag);
    }

    public void updateProperties(CompoundTag value) {
        properties.putAll(new SkinProperties.Changes(value));
        listener.documentDidChangeProperties(value);
    }

    public <T> void put(ISkinProperty<T> property, T value) {
        properties.put(property, value);
        var changes = new SkinProperties.Changes();
        changes.put(property, value);
        listener.documentDidChangeProperties(changes.serializeNBT());
    }

    public <T> T get(ISkinProperty<T> property) {
        return properties.get(property);
    }

    public void beginEditing() {
        listener.documentWillBeginEditing();
    }

    public void endEditing() {
        listener.documentDidEndEditing();
    }

    public void addListener(SkinDocumentListener listener) {
        this.listener.addListener(listener);
    }

    public void removeListener(SkinDocumentListener listener) {
        this.listener.removeListener(listener);
    }

    public void setAnimations(List<SkinAnimation> animations) {
        this.animations = animations;
    }

    public List<SkinAnimation> getAnimations() {
        return animations;
    }

    public void setItemTransforms(SkinItemTransforms itemTransforms) {
        settings.setItemTransforms(itemTransforms);
    }

    public SkinItemTransforms getItemTransforms() {
        return settings.getItemTransforms();
    }

    public SkinDocumentListener getHandler() {
        return listener;
    }

    public SkinDocumentNode nodeById(String id) {
        return _findNodeById(nodes, id);
    }

    public SkinDocumentNode getRoot() {
        return nodes;
    }

    public SkinDocumentSettings getSettings() {
        return settings;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public SkinDocumentType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "type", type);
    }

    private SkinDocumentNode _findNodeById(SkinDocumentNode parent, String id) {
        if (id.equals(parent.getId())) {
            return parent;
        }
        for (var node : parent.children()) {
            var result = _findNodeById(node, id);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private SkinDocumentSettings _generateSkinSettings() {
        var settings1 = new SkinDocumentSettings();
        settings1.setShowsOrigin(settings.showsOrigin());
        settings1.setShowsHelperModel(settings.showsHelperModel());
        return settings1;
    }

    private SkinProperties _generateSkinProperties() {
        var name = properties.get(SkinProperty.ALL_CUSTOM_NAME);
        var flavour = properties.get(SkinProperty.ALL_FLAVOUR_TEXT);
        var properties = new SkinProperties();
        properties.put(SkinProperty.ALL_CUSTOM_NAME, name);
        properties.put(SkinProperty.ALL_FLAVOUR_TEXT, flavour);
        return properties;
    }

    private SkinDocumentNode _generateDefaultNode(SkinDocumentType category) {
        var root = new SkinDocumentNode("root", null);
        for (var partType : category.getSkinPartTypes()) {
            var node = new SkinDocumentNode(partType.getRegistryName().getPath(), null);
            node.setType(partType);
            root.add(node);
        }
        root.add(new SkinDocumentNode("float", null));
        root.add(new SkinDocumentNode("static", null));
        return root;
    }
}
