package moe.plushie.armourers_workshop.core.skin.serializer.document;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class SkinDocument {

    private SkinDocumentType type;
    private SkinDocumentNode nodes;
    private List<SkinDocumentAnimation> animations;

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
        serializer.write(CodingKeys.TYPE, type);
        serializer.write(CodingKeys.NODES, nodes);
        serializer.write(CodingKeys.ANIMATIONS, animations);
        serializer.write(CodingKeys.SETTINGS, settings);
        serializer.write(CodingKeys.PROPERTIES, properties);
    }

    public void deserialize(IDataSerializer serializer) {
        type = serializer.read(CodingKeys.TYPE);
        settings = serializer.read(CodingKeys.SETTINGS);
        properties = serializer.read(CodingKeys.PROPERTIES);
        nodes = serializer.read(CodingKeys.NODES);
        if (nodes == null) {
            nodes = _generateDefaultNode(type);
        }
        animations = serializer.read(CodingKeys.ANIMATIONS);
        settings.setListener(listener);
        nodes.setListener(listener);
        listener.documentDidReload();
    }

    public void updateSettings(CompoundTag tag) {
        settings.applyChanges(tag);
        listener.documentDidChangeSettings(tag);
    }

    public void updateProperties(CompoundTag value) {
        properties.putAll(new SkinProperties(value));
        listener.documentDidChangeProperties(value);
    }

    public <T> void put(ISkinProperty<T> property, T value) {
        properties.put(property, value);
        var changes = new SkinProperties.Increment();
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

    public void setAnimations(List<SkinDocumentAnimation> animations) {
        this.animations = animations;
    }

    public List<SkinDocumentAnimation> animations() {
        return animations;
    }

    public void setItemTransforms(OpenItemTransforms itemTransforms) {
        settings.setItemTransforms(itemTransforms);
    }

    public OpenItemTransforms itemTransforms() {
        return settings.itemTransforms();
    }

    public SkinDocumentListener handler() {
        return listener;
    }

    public SkinDocumentNode nodeById(String id) {
        return _findNodeById(nodes, id);
    }

    public SkinDocumentNode root() {
        return nodes;
    }

    public SkinDocumentSettings settings() {
        return settings;
    }

    public SkinProperties properties() {
        return properties;
    }

    public SkinDocumentType type() {
        return this.type;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "type", type);
    }

    private SkinDocumentNode _findNodeById(SkinDocumentNode parent, String id) {
        if (id.equals(parent.id())) {
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
        for (var partType : category.skinPartTypes()) {
            var node = new SkinDocumentNode(partType.registryName().path(), null);
            node.setType(partType);
            root.add(node);
        }
        root.add(new SkinDocumentNode("float", null, SkinPartTypes.ADVANCED_FLOAT));
        root.add(new SkinDocumentNode("static", null, SkinPartTypes.ADVANCED_STATIC));
        return root;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<SkinDocumentType> TYPE = IDataSerializerKey.create("Type", SkinDocumentTypes.CODEC, SkinDocumentTypes.GENERAL_ARMOR_HEAD);
        public static final IDataSerializerKey<SkinDocumentNode> NODES = IDataSerializerKey.create("Nodes", SkinDocumentNode.CODEC, null);
        public static final IDataSerializerKey<List<SkinDocumentAnimation>> ANIMATIONS = IDataSerializerKey.create("Animations", SkinDocumentAnimation.CODEC.listOf(), null);
        public static final IDataSerializerKey<SkinDocumentSettings> SETTINGS = IDataSerializerKey.create("Settings", SkinDocumentSettings.CODEC, null, SkinDocumentSettings::new);
        public static final IDataSerializerKey<SkinProperties> PROPERTIES = IDataSerializerKey.create("Properties", SkinProperties.CODEC, SkinProperties.EMPTY, SkinProperties.EMPTY::copy);
    }
}
