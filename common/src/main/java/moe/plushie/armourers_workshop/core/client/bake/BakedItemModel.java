package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Environment(EnvType.CLIENT)
public class BakedItemModel extends BuiltInModel {

    public static final BakedItemModel DEFAULT = BakedItemModel.from(new DefaultItemTransforms(), false);

    private final Variant[] variants;

    public BakedItemModel(ItemTransforms itemTransforms, Variant[] variants, boolean usesBlockLight) {
        super(itemTransforms, ItemOverrides.EMPTY, getMissingAtlasSprite(), usesBlockLight);
        this.variants = variants;
    }

    public BakedModel resolve(BakedModel bakedModel, ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int i) {
        for (var variant : variants) {
            if (variant.test(itemStack, level, entity, i)) {
                return variant.model;
            }
        }
        return bakedModel;
    }

    public static BakedItemModel from(SkinItemTransforms itemTransforms, boolean usesBlockLight) {
        return from(Collections.emptyList(), itemTransforms, usesBlockLight);
    }

    public static BakedItemModel from(Collection<String> overrideNames, SkinItemTransforms itemTransforms, boolean usesBlockLight) {
        var baker = new Baker(itemTransforms, usesBlockLight);
        var entries = new ArrayList<Variant>();
        for (var name : overrideNames) {
            var registryName = ResourceLocation.parse(name);
            var model = baker.bake(name, new Variant[0]);
            entries.add(new Variant(registryName, model));
        }
        return baker.bake("", entries.toArray(new Variant[0]));
    }

    private static TextureAtlasSprite getMissingAtlasSprite() {
        return Minecraft.getInstance().getModelManager().getMissingModel().getParticleIcon();
    }

    public static class Variant {

        private final ResourceLocation name;
        private final BakedModel model;

        public Variant(ResourceLocation name, BakedModel model) {
            this.name = name;
            this.model = model;
        }

        public boolean test(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int i) {
            var func = ItemProperties.getProperty(itemStack, name);
            if (func != null) {
                return func.call(itemStack, level, entity, i) >= 1;
            }
            return false;
        }
    }

    public static class DefaultItemTransforms extends SkinItemTransforms {

        public DefaultItemTransforms() {
            put(AbstractItemTransformType.GUI, translate(0, 0, 0), rotation(30, 135, 0), scale(1, 1, 1));
            put(AbstractItemTransformType.FIXED, translate(0, 0, -2), rotation(0, 0, 0), scale(1, 1, 1));
            put(AbstractItemTransformType.GROUND, translate(0, 0, 0), rotation(0, 0, 0), scale(0.5f, 0.5f, 0.5f));
            put(AbstractItemTransformType.THIRD_PERSON_RIGHT_HAND, translate(0, 0, 0), rotation(0, 90, -5), scale(0.8f, 0.8f, 0.8f));
            put(AbstractItemTransformType.THIRD_PERSON_LEFT_HAND, translate(0, 0, 0), rotation(0, 90, -5), scale(0.8f, 0.8f, 0.8f));
            put(AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND, translate(0, 0, 0), rotation(0, 90, -5), scale(0.8f, 0.8f, 0.8f));
            put(AbstractItemTransformType.FIRST_PERSON_LEFT_HAND, translate(0, 0, 0), rotation(0, 90, -5), scale(0.8f, 0.8f, 0.8f));
        }

        private void put(AbstractItemTransformType key, Vector3f translate, Vector3f rotation, Vector3f scale) {
            put(key, SkinTransform.create(translate, rotation, scale));
        }

        private static Vector3f translate(float x, float y, float z) {
            return new Vector3f(x, y, z);
        }

        private static Vector3f rotation(float x, float y, float z) {
            return new Vector3f(x, y, z);
        }

        private static Vector3f scale(float x, float y, float z) {
            return new Vector3f(x, y, z);
        }
    }

    public static class Baker {

        private String prefix = "";

        private final boolean usesBlockLight;
        private final SkinItemTransforms itemTransforms;

        public Baker(SkinItemTransforms itemTransforms, boolean usesBlockLight) {
            this.itemTransforms = itemTransforms;
            this.usesBlockLight = usesBlockLight;
        }

        public BakedItemModel bake(String prefix, Variant[] entries) {
            this.prefix = prefix;
            return new BakedItemModel(getTransforms(), entries, usesBlockLight);
        }

        private ItemTransforms getTransforms() {
            var thirdPersonLeftHand = getTransform(AbstractItemTransformType.THIRD_PERSON_LEFT_HAND);
            var thirdPersonRightHand = getTransform(AbstractItemTransformType.THIRD_PERSON_RIGHT_HAND);
            var firstPersonLeftHand = getTransform(AbstractItemTransformType.FIRST_PERSON_LEFT_HAND);
            var firstPersonRightHand = getTransform(AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND);
            var head = getTransform(AbstractItemTransformType.HEAD);
            var gui = getTransform(AbstractItemTransformType.GUI);
            var ground = getTransform(AbstractItemTransformType.GROUND);
            var fixed = getTransform(AbstractItemTransformType.FIXED);
            return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed);
        }

        private ItemTransform getTransform(AbstractItemTransformType key) {
            var transform = itemTransforms.get(prefix + ";" + key.getName());
            if (transform == null) {
                // when the child is not found, use the parent set.
                transform = itemTransforms.get(key);
            }
            return ItemTransform.from(transform);
        }
    }
}
