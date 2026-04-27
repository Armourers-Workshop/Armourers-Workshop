package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureBox;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ArmatureSerializers {

    private static final HashMap<OpenResourceKey, Class<?>> NAMED_CLASSES = new HashMap<>();
    private static final HashMap<String, Supplier<? extends JointModifier>> NAMED_MODIFIERS = new HashMap<>();
    private static final HashMap<String, Function<ArmatureTransformerContext, ? extends ArmaturePlugin>> NAMED_PLUGINS = new HashMap<>();

    public static OpenVector3f readVector(IODataObject object, OpenVector3f defaultValue) {
        switch (object.type()) {
            case ARRAY: {
                if (object.size() != 3) {
                    break;
                }
                float f1 = object.at(0).floatValue();
                float f2 = object.at(1).floatValue();
                float f3 = object.at(2).floatValue();
                return new OpenVector3f(f1, f2, f3);
            }
            case DICTIONARY: {
                float f1 = object.get("x").floatValue();
                float f2 = object.get("y").floatValue();
                float f3 = object.get("z").floatValue();
                return new OpenVector3f(f1, f2, f3);
            }
            default: {
                break;
            }
        }
        return defaultValue;
    }

    public static OpenTransform3f readTransform(IODataObject object) {
        if (object.isNull()) {
            return OpenTransform3f.IDENTITY;
        }
        var translate = readVector(object.get("translate"), OpenVector3f.ZERO);
        var scale = readVector(object.get("scale"), OpenVector3f.ONE);
        var rotation = readVector(object.get("rotation"), OpenVector3f.ZERO);
        var pivot = readVector(object.get("pivot"), OpenVector3f.ZERO);
        var afterTranslate = readVector(object.get("afterTranslate"), OpenVector3f.ZERO);
        return OpenTransform3f.create(translate, rotation, scale, pivot, afterTranslate);
    }

    public static JointShape readShape(IODataObject object) {
        if (object.isNull()) {
            return null;
        }
        var origin = readVector(object.get("origin"), OpenVector3f.ZERO);
        var size = readVector(object.get("size"), OpenVector3f.ZERO);
        var inflate = object.get("inflate").floatValue();
        var transform = readTransform(object);
        var textureBox = readShapeTextureUVs(object.get("uv"), size);
        var children = new ArrayList<JointShape>();
        object.get("children").allValues().forEach(it -> children.add(readShape(it)));
        return new JointShape(origin, size, inflate, transform, textureBox, children);
    }

    public static Map<OpenDirection, OpenRectangle2f> readShapeTextureUVs(IODataObject object, OpenVector3f size) {
        switch (object.type()) {
            case ARRAY: {
                if (object.size() < 2) {
                    break;
                }
                float u = object.at(0).floatValue();
                float v = object.at(1).floatValue();
                boolean mirror = false;
                if (u < 0) {
                    u = -u;
                    mirror = true;
                }
                var textureData = new SkinTextureData("", 255, 255);
                var textureBox = new SkinTextureBox(size.x(), size.y(), size.z(), mirror, new OpenVector2f(u, v), textureData);
                var uvs = new EnumMap<OpenDirection, OpenRectangle2f>(OpenDirection.class);
                for (var dir : OpenDirection.values()) {
                    var key = textureBox.getTexture(dir);
                    if (key != null) {
                        uvs.put(dir, new OpenRectangle2f(key.u(), key.v(), key.width(), key.height()));
                    }
                }
                return uvs;
            }
            case DICTIONARY: {
                var textureData = new SkinTextureData("", 255, 255);
                var textureBox = new SkinTextureBox(size.x(), size.y(), size.z(), false, null, textureData);
                for (var dir : OpenDirection.values()) {
                    var ob = object.get(dir.serializedName());
                    if (ob.size() >= 4) {
                        float u = ob.at(0).floatValue();
                        float v = ob.at(1).floatValue();
                        float n = ob.at(2).floatValue();
                        float m = ob.at(3).floatValue();
                        textureBox.putTextureRect(dir, new OpenRectangle2f(u, v, n - u, m - v));
                    }
                }
                var uvs = new EnumMap<OpenDirection, OpenRectangle2f>(OpenDirection.class);
                for (var dir : OpenDirection.values()) {
                    var key = textureBox.getTexture(dir);
                    if (key != null) {
                        uvs.put(dir, new OpenRectangle2f(key.u(), key.v(), key.width(), key.height()));
                    }
                }
                return uvs;
            }
            default: {
                break;
            }
        }
        return null;
    }

    public static IRegistryHolder<?> readEntityType(IODataObject object) {
        return AbstractEntityTypeBuilder.lazy(object.stringValue());
    }

    public static OpenResourceKey readResourceLocation(IODataObject object) {
        return OpenResourceKey.parse(object.stringValue());
    }


    public static <T> void registerClass(String registryName, Class<T> clazz) {
        NAMED_CLASSES.put(OpenResourceKey.parse(registryName), clazz);
    }

    public static <T> Class<?> getClass(OpenResourceKey registryName) {
        return NAMED_CLASSES.get(registryName);
    }

    public static void registerPlugin(String registryName, Supplier<? extends ArmaturePlugin> provider) {
        registerPlugin(registryName, context -> provider.get());
    }

    public static void registerPlugin(String registryName, Function<ArmatureTransformerContext, ? extends ArmaturePlugin> provider) {
        NAMED_PLUGINS.put(registryName, provider);
    }

    public static Function<ArmatureTransformerContext, ? extends ArmaturePlugin> getPlugin(String registryName) {
        return NAMED_PLUGINS.get(registryName);
    }

    public static void registerModifier(String registryName, Supplier<? extends JointModifier> provider) {
        NAMED_MODIFIERS.put(registryName, provider);
    }

    public static Supplier<? extends JointModifier> getModifier(String registryName) {
        return NAMED_MODIFIERS.get(registryName);
    }
}

