package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.AWConstants;
import moe.plushie.armourers_workshop.core.api.ISkinArmorType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import moe.plushie.armourers_workshop.core.api.action.ICanHeld;
import moe.plushie.armourers_workshop.core.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderBuffer;
import moe.plushie.armourers_workshop.core.render.buffer.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import moe.plushie.armourers_workshop.core.utils.SkinUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3f;

import java.awt.*;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;


public class SkinRenderer<T extends Entity, M extends Model> {

    protected final EntityType<T> type;
    protected final EntityProfile<T> profile;
    protected final Transformer<M> transformer = new Transformer<>();

    public SkinRenderer(EntityType<T> entityType) {
        this.type = entityType;
        this.profile = EntityProfile.getProfile(entityType);
    }

    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemCameraTransforms.TransformType transformType) {
        ISkinPartType partType = bakedPart.getType();
        if (partType instanceof ICanHeld) {
            if (transformer.items.containsKey(transformType)) {
                return true;
            }
        }
        return transformer.armors.containsKey(bakedPart.getType());
    }

    public void override(T entity, M model, SkinWardrobe wardrobe) {
    }

    public void apply(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemCameraTransforms.TransformType transformType, float partialTicks, MatrixStack matrixStack) {
        ISkinPartType partType = bakedPart.getType();
        if (partType instanceof ICanHeld) {
            ITransform<M> op = transformer.items.get(transformType);
            if (op != null) {
                op.apply(matrixStack, model, transformType, bakedPart);
                SkinUtils.apply(matrixStack, entity, bakedPart.getPart(), partialTicks);
                return;
            }
        }
        ITransform<M> op = transformer.armors.get(partType);
        if (op != null && model != null) {
            op.apply(matrixStack, model, transformType, bakedPart);
            SkinUtils.apply(matrixStack, entity, bakedPart.getPart(), partialTicks);
        }
    }

    public void render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, ItemCameraTransforms.TransformType transformType, int light, float partialTicks, MatrixStack matrixStack, SkinRenderBuffer buffers) {
        ISkinType type = bakedSkin.getType();
        if (type instanceof ISkinArmorType && !profile.canSupport(type)) {
            return;
        }
        int index = 0;
        Skin skin = bakedSkin.getSkin();
        ColorScheme scheme1 = bakedSkin.resolve(entity, scheme);
        SkinVertexBufferBuilder builder = buffers.getBuffer(skin);
        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
            if (!prepare(entity, model, bakedSkin, bakedPart, transformType)) {
                continue;
            }
            boolean shouldRenderPart = bakedSkin.shouldRenderPart(bakedPart, entity, transformType);
            matrixStack.pushPose();
            apply(entity, model, bakedSkin, bakedPart, transformType, partialTicks, matrixStack);
            builder.addPartData(bakedPart, scheme1, light, partialTicks, matrixStack, shouldRenderPart);
            if (shouldRenderPart) {
                if (AWConfig.showDebugTargetPosition) {
                    builder.addShapeData(AWConstants.ZERO, matrixStack);
                }
                if (AWConfig.showDebugPartFrame) {
                    builder.addShapeData(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(index++), matrixStack);
                }
            }
            matrixStack.popPose();
        }

        if (AWConfig.showDebugFullFrame) {
            builder.addShapeData(bakedSkin.getRenderShape(entity, model, transformType).bounds(), Color.RED, matrixStack);
        }
    }

    public EntityType<T> getType() {
        return type;
    }

    public EntityProfile<T> getProfile() {
        return profile;
    }


    @FunctionalInterface
    public interface ITransform<M> {
        void apply(MatrixStack matrixStack, M model, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart);
    }

    public static class Transformer<M> {

        final HashMap<ISkinPartType, ITransform<M>> armors = new HashMap<>();
        final HashMap<ItemCameraTransforms.TransformType, ITransform<M>> items = new HashMap<>();

        public void registerArmor(ISkinPartType partType, Function<M, ModelRenderer> transformer) {
            registerArmor(partType, (matrixStack, model, transformType, bakedPart) -> apply(matrixStack, transformer.apply(model)));
        }

        public void registerArmor(ISkinPartType partType, BiConsumer<MatrixStack, M> transformer) {
            registerArmor(partType, (matrixStack, model, transformType, bakedPart) -> transformer.accept(matrixStack, model));
        }

        public void registerArmor(ISkinPartType partType, ITransform<M> transformer) {
            armors.put(partType, transformer);
        }


        public void registerItem(ItemCameraTransforms.TransformType transformType, BiConsumer<MatrixStack, M> transformer) {
            registerItem(transformType, (matrixStack, model, transformType1, bakedPart) -> transformer.accept(matrixStack, model));
        }

        public void registerItem(ItemCameraTransforms.TransformType transformType, ITransform<M> transformer) {
            items.put(transformType, transformer);
        }


        public void apply(MatrixStack matrixStack, ModelRenderer modelRenderer) {
            matrixStack.translate(modelRenderer.x, modelRenderer.y, modelRenderer.z);
            if (modelRenderer.zRot != 0) {
                matrixStack.mulPose(Vector3f.ZP.rotation(modelRenderer.zRot));
            }
            if (modelRenderer.yRot != 0) {
                matrixStack.mulPose(Vector3f.YP.rotation(modelRenderer.yRot));
            }
            if (modelRenderer.xRot != 0) {
                matrixStack.mulPose(Vector3f.XP.rotation(modelRenderer.xRot));
            }
        }
    }
}
