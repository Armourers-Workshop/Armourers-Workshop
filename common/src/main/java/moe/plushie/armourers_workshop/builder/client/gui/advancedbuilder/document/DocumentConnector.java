package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.builder.data.properties.DataProperty;
import moe.plushie.armourers_workshop.builder.data.properties.VectorProperty;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocument;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.skin.document.SkinDocumentSettings;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DocumentConnector {

    private final DocumentEditor editor;
    private final ArrayList<Consumer<SkinDocumentNode>> nodeListeners = new ArrayList<>();
    private final ArrayList<Consumer<SkinDocumentSettings>> settingsListeners = new ArrayList<>();
    private final ArrayList<Consumer<SkinProperties>> propertiesListeners = new ArrayList<>();

    public final DataProperty<String> name = registerNode(DataProperty::new, SkinDocumentNode::getName, SkinDocumentNode::setName);
    public final DataProperty<SkinDescriptor> part = registerNode(DataProperty::new, SkinDocumentNode::getSkin, SkinDocumentNode::setSkin);
    public final DataProperty<Boolean> lock = registerNode(DataProperty::new, SkinDocumentNode::isLocked, Objects::hashCode);

    public final VectorProperty location = registerNode(VectorProperty::new, SkinDocumentNode::getLocation, SkinDocumentNode::setLocation);
    public final VectorProperty rotation = registerNode(VectorProperty::new, SkinDocumentNode::getRotation, SkinDocumentNode::setRotation);
    public final VectorProperty scale = registerNode(VectorProperty::new, SkinDocumentNode::getScale, SkinDocumentNode::setScale);
    public final VectorProperty pivot = registerNode(VectorProperty::new, SkinDocumentNode::getPivot, SkinDocumentNode::setPivot);

    public final DataProperty<Boolean> enabled = registerNode(DataProperty::new, SkinDocumentNode::isEnabled, SkinDocumentNode::setEnabled);
    public final DataProperty<Boolean> mirror = registerNode(DataProperty::new, SkinDocumentNode::isMirror, SkinDocumentNode::setMirror);

    public final DataProperty<String> itemName = registerProperties(DataProperty::new, SkinProperty.ALL_CUSTOM_NAME);
    public final DataProperty<String> itemFlavour = registerProperties(DataProperty::new, SkinProperty.ALL_FLAVOUR_TEXT);

    public final DataProperty<Boolean> showOrigin = registerSettings(DataProperty::new, SkinDocumentSettings::showsOrigin, SkinDocumentSettings::setShowsOrigin);
    public final DataProperty<Boolean> showHelperModel = registerSettings(DataProperty::new, SkinDocumentSettings::showsHelperModel, SkinDocumentSettings::setShowsHelperModel);

    private SkinDocument document;
    private SkinDocumentNode node;

    public DocumentConnector(SkinDocument document, DocumentEditor editor) {
        this.editor = editor;
        this.document = document;
    }

    public void update(SkinDocumentSettings settings) {
        this.settingsListeners.forEach(it -> it.accept(settings));
    }

    public void update(SkinProperties properties) {
        this.propertiesListeners.forEach(it -> it.accept(properties));
    }

    public void update(SkinDocumentNode node) {
        this.node = node;
        this.nodeListeners.forEach(it -> it.accept(node));
    }

    public void addListener(Consumer<SkinDocumentNode> consumer) {
        nodeListeners.add(consumer);
    }

    public void removeListener(Consumer<SkinDocumentNode> consumer) {
        nodeListeners.remove(consumer);
    }

    public SkinDocumentNode getNode() {
        return node;
    }

    public DocumentEditor getEditor() {
        return editor;
    }

    public <T, P extends DataProperty<T>> P registerProperties(Supplier<P> factory, ISkinProperty<T> key) {
        P property = factory.get();
        property.addEditingObserver((flag) -> {
            if (flag) {
                editor.beginEditing();
            } else {
                editor.endEditing();
            }
        });
        property.addObserver((newValue) -> {
            T oldValue = document.get(key);
            if (!Objects.equal(oldValue, newValue)) {
                document.put(key, newValue);
            }
        });
        propertiesListeners.add((properties) -> {
            T oldValue = property.get();
            T newValue = document.get(key);
            if (!Objects.equal(oldValue, newValue)) {
                property.set(newValue);
            }
        });
        return property;
    }

    public <T, P extends DataProperty<T>> P registerSettings(Supplier<P> factory, Function<SkinDocumentSettings, T> getter, BiConsumer<SkinDocumentSettings, T> setter) {
        P property = factory.get();
        property.addEditingObserver((flag) -> {
            if (flag) {
                editor.beginEditing();
            } else {
                editor.endEditing();
            }
        });
        property.addObserver((newValue) -> {
            T oldValue = getter.apply(document.getSettings());
            if (!Objects.equal(oldValue, newValue)) {
                setter.accept(document.getSettings(), newValue);
            }
        });
        settingsListeners.add((node) -> {
            T oldValue = property.get();
            T newValue = getter.apply(document.getSettings());
            if (!Objects.equal(oldValue, newValue)) {
                property.set(newValue);
            }
        });
        return property;
    }

    public <T, P extends DataProperty<T>> P registerNode(Supplier<P> factory, Function<SkinDocumentNode, T> getter, BiConsumer<SkinDocumentNode, T> setter) {
        P property = factory.get();
        property.addEditingObserver((flag) -> {
            if (flag) {
                editor.beginEditing();
            } else {
                editor.endEditing();
            }
        });
        property.addObserver((newValue) -> {
            T oldValue = getter.apply(node);
            if (!Objects.equal(oldValue, newValue)) {
                setter.accept(node, newValue);
            }
        });
        nodeListeners.add((node) -> {
            T oldValue = property.get();
            T newValue = getter.apply(node);
            if (!Objects.equal(oldValue, newValue)) {
                property.set(newValue);
            }
        });
        return property;
    }
}
