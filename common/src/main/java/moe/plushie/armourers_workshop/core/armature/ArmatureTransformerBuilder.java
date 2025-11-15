package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.core.armature.core.AfterTransformModifier;
import moe.plushie.armourers_workshop.core.armature.core.DefaultOverriddenArmaturePlugin;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class ArmatureTransformerBuilder {

    protected OpenResourceLocation parent;
    protected Armature armature;
    protected IODataObject contents;

    protected final OpenResourceLocation name;
    protected final ArrayList<OpenResourceLocation> models = new ArrayList<>();
    protected final ArrayList<IRegistryHolder<?>> entities = new ArrayList<>();
    protected final ArrayList<String> pluginModifiers = new ArrayList<>();
    protected final HashMap<String, Collection<String>> overrideModifiers = new HashMap<>();
    protected final HashMap<Joint, Collection<JointModifier>> jointModifiers = new HashMap<>();
    protected final HashMap<Joint, Collection<JointModifier>> transformModifiers = new HashMap<>();

    public ArmatureTransformerBuilder(OpenResourceLocation name) {
        this.name = name;
    }

    public void load(IODataObject object) {
        object.get("parent").ifPresent(it -> {
            parent = OpenResourceLocation.parse(it.stringValue());
        });
        object.get("target").ifPresent(it -> {
            armature = Armatures.byName(OpenResourceLocation.parse(it.stringValue()));
        });
        contents = object;
        if (armature != null) {
            _parseContent(object);
        }
    }

    public void resolve(Collection<ArmatureTransformerBuilder> hierarchy) {
        _parseContentWithParent(contents, hierarchy);
        hierarchy.forEach(builder -> {
            _mergeTo(builder.jointModifiers, jointModifiers);
            _mergeTo(builder.transformModifiers, transformModifiers);
            _mergeTo(builder.overrideModifiers, overrideModifiers);
            _mergeTo(builder.pluginModifiers, pluginModifiers);
            if (models.isEmpty()) {
                _mergeTo(builder.models, models); // only inherit when empty.
            }
            if (entities.isEmpty()) {
                _mergeTo(builder.entities, entities); // only inherit when empty.
            }
        });
    }

    public ArmatureTransformer build(ArmatureTransformerContext context) {
        var plugins = new ArrayList<ArmaturePlugin>();
        var modifiers = new HashMap<Joint, ArrayList<JointModifier>>();
        plugins.add(new DefaultOverriddenArmaturePlugin(overrideModifiers, context));
        jointModifiers.forEach((joint, modifiers1) -> modifiers.computeIfAbsent(joint, k -> new ArrayList<>()).addAll(modifiers1));
        transformModifiers.forEach((joint, modifiers1) -> modifiers.computeIfAbsent(joint, k -> new ArrayList<>()).addAll(modifiers1));
        pluginModifiers.forEach(it -> plugins.add(buildPlugin(it, context)));
        plugins.removeIf(Objects::isNull);
        var transformer = new ArmatureTransformer(armature, plugins, context);
        var jointContext = new JointContext(transformer, context);
        modifiers.forEach((joint, values) -> transformer.put(joint, buildTransform(joint, values, jointContext)));
        return transformer;
    }

    protected ArmaturePlugin buildPlugin(String name, ArmatureTransformerContext context) {
        var builder = ArmatureSerializers.getPlugin(name);
        if (builder != null) {
            return builder.apply(context);
        }
        return null;
    }

    protected IJointTransform buildTransform(Joint joint, Collection<JointModifier> modifiers, JointContext context) {
        var transform = IJointTransform.NONE;
        for (var modifier : modifiers) {
            transform = modifier.apply(transform, joint, context);
        }
        return transform;
    }

    protected abstract JointModifier buildJointTarget(String name, IODataObject parameters);

    public ArrayList<OpenResourceLocation> models() {
        return models;
    }

    public ArrayList<IRegistryHolder<?>> entities() {
        return entities;
    }

    public OpenResourceLocation parent() {
        return parent;
    }

    public OpenResourceLocation name() {
        return name;
    }

    private void _parseContentWithParent(IODataObject object, Collection<ArmatureTransformerBuilder> hierarchy) {
        // if load has been completed, ignore.
        if (armature != null || object == null) {
            return;
        }
        // Find the last armature.
        for (var builder : hierarchy) {
            if (builder.armature != null) {
                armature = builder.armature;
                break; // this -> this.parent -> this.parent.parent -> ...
            }
        }
        if (armature != null) {
            _parseContent(object);
        }
    }

    private void _parseContent(IODataObject object) {
        // read all joint
        object.get("joint").entrySet().forEach(it -> {
            var joint = armature.jointByName(it.getKey());
            if (joint != null) {
                jointModifiers.put(joint, _parseModelModifiers(it.getValue()));
            }
        });
        object.get("transform").ifPresent(it -> {
            it.get("translate").entrySet().forEach(it2 -> _parseTranslateModifiers(it2.getKey(), it2.getValue()));
            it.get("scale").entrySet().forEach(it2 -> _parseScaleModifiers(it2.getKey(), it2.getValue()));
            it.get("rotate").entrySet().forEach(it2 -> _parseRotateModifiers(it2.getKey(), it2.getValue()));
        });
        object.get("override").entrySet().forEach(it -> overrideModifiers.put(it.getKey(), _parseOverrideModifiers(it.getValue())));
        object.get("plugin").allValues().forEach(it -> pluginModifiers.add(it.stringValue()));
        object.get("model").allValues().forEach(it -> models.add(ArmatureSerializers.readResourceLocation(it)));
        object.get("entity").allValues().forEach(it -> entities.add(ArmatureSerializers.readEntityType(it)));

        // clear all contents.
        contents = null;
    }

    private Collection<JointModifier> _parseModelModifiers(IODataObject object) {
        switch (object.type()) {
            case STRING: {
                return _parseJointTargets(object, null);
            }
            case DICTIONARY: {
                var modifiers = new ArrayList<JointModifier>();
                modifiers.addAll(_parseJointTargets(object, null));
                modifiers.addAll(_parseTransformModifiers(object));
                modifiers.addAll(_parseModifiers(object.get("modifier")));
                return modifiers;
            }
            default: {
                // sorry, but we can't support this format yet.
                return Collections.emptyList();
            }
        }
    }

    private Collection<JointModifier> _parseJointTargets(IODataObject object, IODataObject parameters) {
        switch (object.type()) {
            case DICTIONARY: {
                return _parseJointTargets(object.get("target"), object);
            }
            case STRING: {
                var value = object.stringValue();
                if (!value.isEmpty()) {
                    return Collections.singleton(buildJointTarget(value, parameters));
                }
                return Collections.emptyList();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    private Collection<JointModifier> _parseModifiers(IODataObject object) {
        switch (object.type()) {
            case ARRAY: {
                var modifiers = new ArrayList<JointModifier>();
                object.allValues().forEach(it -> {
                    var modifier = ArmatureSerializers.getModifier(it.stringValue());
                    if (modifier != null) {
                        modifiers.add(modifier.get());
                    }
                });
                return modifiers;
            }
            case DICTIONARY: {
                var modifiers = new ArrayList<JointModifier>();
                object.entrySet().forEach(it -> {
//                    Function<IDataPackObject, JointModifier> builder = PARAMETERIZED_MODIFIERS.get(it.getKey());
//                    if (builder != null) {
//                        modifiers.add(builder.apply(it.getValue()));
//                        return;
//                    }
                    var modifier = ArmatureSerializers.getModifier(it.getKey());
                    if (modifier != null) {
                        modifiers.add(modifier.get());
                    }
                });
                return modifiers;
            }
            default: {
                // sorry, but we can't support this format yet.
                return Collections.emptyList();
            }
        }
    }

    private Collection<JointModifier> _parseTransformModifiers(IODataObject object) {
        var transform = ArmatureSerializers.readTransform(object);
        if (transform.isIdentity()) {
            return Collections.emptyList();
        }
        return Collections.singleton(new AfterTransformModifier(transform));
    }


    private Collection<String> _parseOverrideModifiers(IODataObject object) {
        return switch (object.type()) {
            case STRING -> Collections.singleton(object.stringValue());
            case ARRAY -> object.collect(IODataObject::stringValue);
            default -> Collections.emptyList();
        };
    }

    private void _parseTranslateModifiers(String name, IODataObject object) {
        var value = ArmatureSerializers.readVector(object, OpenVector3f.ZERO);
        if (value.equals(OpenVector3f.ZERO)) {
            return;
        }
        var transform = OpenTransform3f.createTranslateTransform(value);
        _addTransformModifier(name, new AfterTransformModifier(transform));
    }

    private void _parseRotateModifiers(String name, IODataObject object) {
        var value = ArmatureSerializers.readVector(object, OpenVector3f.ZERO);
        if (value.equals(OpenVector3f.ZERO)) {
            return;
        }
        var transform = OpenTransform3f.createRotationTransform(value);
        _addTransformModifier(name, new AfterTransformModifier(transform));
    }

    private void _parseScaleModifiers(String name, IODataObject object) {
        var value = ArmatureSerializers.readVector(object, OpenVector3f.ONE);
        if (value.equals(OpenVector3f.ONE)) {
            return;
        }
        var transform = OpenTransform3f.createScaleTransform(value);
        _addTransformModifier(name, new AfterTransformModifier(transform));
    }

    private void _addTransformModifier(String name, JointModifier modifier) {
        // ..
        Collection<? extends Joint> joints;
        if (name.equals("") || name.equals("*")) {
            joints = armature.allJoints();
        } else {
            var joint = armature.jointByName(name);
            if (joint == null) {
                return;
            }
            joints = Collections.singleton(joint);
        }
        for (var joint : joints) {
            transformModifiers.computeIfAbsent(joint, k -> new ArrayList<>()).add(modifier);
        }
    }

    private <K, V> void _mergeTo(Map<K, V> other, Map<K, V> result) {
        other.forEach((key, value) -> {
            if (!result.containsKey(key)) {
                result.put(key, value);
            }
        });
    }

    private <V> void _mergeTo(Collection<V> other, Collection<V> result) {
        result.addAll(other);
    }
}
