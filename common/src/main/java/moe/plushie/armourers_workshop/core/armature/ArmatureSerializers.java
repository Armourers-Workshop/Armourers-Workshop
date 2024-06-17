package moe.plushie.armourers_workshop.core.armature;

import moe.plushie.armourers_workshop.api.common.IEntityTypeProvider;
import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.ext.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import moe.plushie.armourers_workshop.core.texture.TextureBox;
import moe.plushie.armourers_workshop.core.texture.TextureData;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ArmatureSerializers {

    private static final HashMap<IResourceLocation, Class<?>> NAMED_CLASSES = new HashMap<>();
    private static final HashMap<String, Supplier<? extends JointModifier>> NAMED_MODIFIERS = new HashMap<>();
    private static final HashMap<String, Function<ArmatureTransformerContext, ? extends ArmaturePlugin>> NAMED_PLUGINS = new HashMap<>();

    public static Vector3f readVector(IDataPackObject object, Vector3f defaultValue) {
        switch (object.type()) {
            case ARRAY: {
                if (object.size() != 3) {
                    break;
                }
                float f1 = object.at(0).floatValue();
                float f2 = object.at(1).floatValue();
                float f3 = object.at(2).floatValue();
                return new Vector3f(f1, f2, f3);
            }
            case DICTIONARY: {
                float f1 = object.get("x").floatValue();
                float f2 = object.get("y").floatValue();
                float f3 = object.get("z").floatValue();
                return new Vector3f(f1, f2, f3);
            }
            default: {
                break;
            }
        }
        return defaultValue;
    }

    public static ITransformf readTransform(IDataPackObject object) {
        if (object.isNull()) {
            return SkinTransform.IDENTITY;
        }
        Vector3f translate = readVector(object.get("translate"), Vector3f.ZERO);
        Vector3f scale = readVector(object.get("scale"), Vector3f.ONE);
        Vector3f rotation = readVector(object.get("rotation"), Vector3f.ZERO);
        Vector3f pivot = readVector(object.get("pivot"), Vector3f.ZERO);
        Vector3f offset = readVector(object.get("offset"), Vector3f.ZERO);
        return SkinTransform.create(translate, rotation, scale, pivot, offset);
    }

    public static JointShape readShape(IDataPackObject object) {
        if (object.isNull()) {
            return null;
        }
        Vector3f origin = readVector(object.get("origin"), Vector3f.ZERO);
        Vector3f size = readVector(object.get("size"), Vector3f.ZERO);
        float inflate = object.get("inflate").floatValue();
        ITransformf transform = readTransform(object);
        Map<Direction, Rectangle2f> textureBox = readShapeTextureUVs(object.get("uv"), size);
        ArrayList<JointShape> children = new ArrayList<>();
        object.get("children").allValues().forEach(it -> children.add(readShape(it)));
        return new JointShape(origin, size, inflate, transform, textureBox, children);
    }

    public static Map<Direction, Rectangle2f> readShapeTextureUVs(IDataPackObject object, Vector3f size) {
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
                TextureData textureData = new TextureData("", 255, 255);
                TextureBox textureBox = new TextureBox(size.getX(), size.getY(), size.getZ(), mirror, new Vector2f(u, v), textureData);
                EnumMap<Direction, Rectangle2f> uvs = new EnumMap<>(Direction.class);
                for (Direction dir : Direction.values()) {
                    ITextureKey key = textureBox.getTexture(dir);
                    if (key != null) {
                        uvs.put(dir, new Rectangle2f(key.getU(), key.getV(), key.getWidth(), key.getHeight()));
                    }
                }
                return uvs;
            }
            case DICTIONARY: {
                TextureData textureData = new TextureData("", 255, 255);
                TextureBox textureBox = new TextureBox(size.getX(), size.getY(), size.getZ(), false, null, textureData);
                for (Direction dir : Direction.values()) {
                    IDataPackObject ob = object.get(dir.getName());
                    if (ob.size() >= 4) {
                        float u = ob.at(0).floatValue();
                        float v = ob.at(1).floatValue();
                        float n = ob.at(2).floatValue();
                        float m = ob.at(3).floatValue();
                        textureBox.putTextureRect(dir, new Rectangle2f(u, v, n - u, m - v));
                    }
                }
                EnumMap<Direction, Rectangle2f> uvs = new EnumMap<>(Direction.class);
                for (Direction dir : Direction.values()) {
                    ITextureKey key = textureBox.getTexture(dir);
                    if (key != null) {
                        uvs.put(dir, new Rectangle2f(key.getU(), key.getV(), key.getWidth(), key.getHeight()));
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

    public static IEntityTypeProvider<?> readEntityType(IDataPackObject object) {
        return IEntityTypeProvider.of(object.stringValue());
    }

    public static IResourceLocation readResourceLocation(IDataPackObject object) {
        return OpenResourceLocation.parse(object.stringValue());
    }


    public static <T> void registerClass(String registryName, Class<T> clazz) {
        NAMED_CLASSES.put(OpenResourceLocation.parse(registryName), clazz);
    }

    public static <T> Class<?> getClass(IResourceLocation registryName) {
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

