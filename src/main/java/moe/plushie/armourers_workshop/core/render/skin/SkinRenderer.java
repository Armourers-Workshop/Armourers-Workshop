package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.action.ICanHeld;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderObjectBuilder;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinVertexBufferBuilder;
import moe.plushie.armourers_workshop.core.render.other.SkinModelManager;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModDebugger;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.extened.AWMatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SkinRenderer<T extends Entity, M extends Model> {

    protected final EntityProfile profile;
    protected final Transformer<T, M> transformer = new Transformer<>();

    protected final ArrayList<ModelRenderer> overriders = new ArrayList<>();

    public SkinRenderer(EntityProfile profile) {
        this.profile = profile;
    }

    public void init(EntityRenderer<T> entityRenderer) {
    }

    public void initTransformers() {
    }

    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        ISkinPartType partType = bakedPart.getType();
        if (partType == SkinPartTypes.BLOCK || partType == SkinPartTypes.BLOCK_MULTI) {
            return true;
        }
        if (partType instanceof ICanHeld) {
            if (transformer.items.containsKey(transformType)) {
                return true;
            }
        }
        return transformer.armors.containsKey(bakedPart.getType());
    }


    public void apply(T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart, BakedSkin bakedSkin, float partialTicks, MatrixStack matrixStack) {
        ITransform<T, M> op = getPartTransform(entity, model, itemStack, transformType, bakedPart);
        if (op != null && model != null) {
            op.apply(matrixStack, entity, model, itemStack, transformType, bakedPart);
            SkinUtils.apply(AWMatrixStack.wrap(matrixStack), bakedPart.getPart(), partialTicks, entity);
        }
    }

    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
    }

    public void willRender(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        overriders.clear();
        apply(entity, model, renderData.getOverriddenManager(), renderData);
    }

    public void willRenderModel(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        overriders.forEach(m -> m.visible = false);
    }

    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, int light, float partialTicks, int slotIndex, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        if (profile != null) {
            ISkinType type = bakedSkin.getType();
            if (type instanceof ISkinArmorType && !profile.canSupport(type)) {
                return 0;
            }
        }
        int counter = 0;
        Skin skin = bakedSkin.getSkin();
        ColorScheme scheme1 = bakedSkin.resolve(entity, scheme);
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        SkinRenderObjectBuilder builder = bufferBuilder.getBuffer(skin);
        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
            if (!prepare(entity, model, bakedSkin, bakedPart, itemStack, transformType)) {
                continue;
            }
            boolean shouldRenderPart = bakedSkin.shouldRenderPart(bakedPart, entity, itemStack, transformType);
            matrixStack.pushPose();
            apply(entity, model, itemStack, transformType, bakedPart, bakedSkin, partialTicks, matrixStack);
            builder.addPartData(bakedPart, scheme1, light, partialTicks, slotIndex, matrixStack, shouldRenderPart);
            if (shouldRenderPart && ModDebugger.skinPartBounds) {
                builder.addShapeData(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(counter), matrixStack);
            }
            if (shouldRenderPart && ModDebugger.skinPartOrigin) {
                builder.addShapeData(AWConstants.ZERO, matrixStack);
            }
            // we have some cases where we need to pre-render,
            // this is not a real render where we should not increase the number.
            if (shouldRenderPart) {
                counter += 1;
            }
//            RenderUtils.drawPoint(matrixStack, null, 32, buffers);
            matrixStack.popPose();
        }

        if (ModDebugger.skinBounds) {
            builder.addShapeData(bakedSkin.getRenderShape(entity, model, itemStack, transformType).bounds(), Color.RED, matrixStack);
        }
        if (ModDebugger.skinBounds) {
            builder.addShapeData(AWConstants.ZERO, matrixStack);
        }

        return counter;
    }

    public void didRender(T entity, M model, SkinRenderData renderData, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        for (ModelRenderer modelRenderer : overriders) {
            modelRenderer.visible = true;
        }
        overriders.clear();
    }

    protected void addModelOverride(ModelRenderer modelRenderer) {
        if (!modelRenderer.visible) {
            return;
        }
        modelRenderer.visible = false;
        overriders.add(modelRenderer);
    }

    public ITransform<T, M> getPartTransform(T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        ISkinPartType partType = bakedPart.getType();
        ITransform<T, M> transform = null;
        if (partType instanceof ICanHeld) {
            transform = transformer.items.get(transformType);
        }
        if (transform == null) {
            transform = transformer.armors.get(partType);
        }
        return transform;
    }

    public EntityProfile getProfile() {
        return profile;
    }

    @FunctionalInterface
    public interface ITransform<T, M> {
        void apply(MatrixStack matrixStack, T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart);
    }

    public static class Transformer<T, M> {

        final HashMap<ISkinPartType, ITransform<T, M>> armors = new HashMap<>();
        final HashMap<ItemCameraTransforms.TransformType, ITransform<T, M>> items = new HashMap<>();

        public static <M> void none(MatrixStack matrixStack, M model) {
        }

        public static <T extends Entity, M extends Model> void withModel(MatrixStack matrixStack, T entity, M model, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart) {
            final float f1 = 16f;
            final float f2 = 1 / 16f;
            final boolean flag = (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND || transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
//            ModDebugger.translate(matrixStack);
            matrixStack.scale(f1, f1, f1);
            IBakedModel bakedModel = SkinModelManager.getInstance().getModel(bakedPart.getType(), itemStack, entity.level, entity);
            ForgeHooksClient.handleCameraTransforms(matrixStack, bakedModel, transformType, flag);
            matrixStack.scale(f2, f2, f2);
//            ModDebugger.rotate(matrixStack);
//            ModDebugger.scale(matrixStack);
            if (flag) {
                matrixStack.scale(-1, 1, 1);
            }
        }

        public void registerArmor(ISkinPartType partType, Function<M, ModelRenderer> transformer) {
            registerArmor(partType, (matrixStack, entity, model, itemStack, transformType, bakedPart) -> apply(matrixStack, transformer.apply(model)));
        }

        public void registerArmor(ISkinPartType partType, BiConsumer<MatrixStack, M> transformer) {
            registerArmor(partType, (matrixStack, entity, model, itemStack, transformType, bakedPart) -> transformer.accept(matrixStack, model));
        }

        public void registerArmor(ISkinPartType partType, ITransform<T, M> transformer) {
            armors.put(partType, transformer);
        }

        public void registerItem(ItemCameraTransforms.TransformType transformType, BiConsumer<MatrixStack, M> transformer) {
            registerItem(transformType, (matrixStack, entity, model, itemStack, transformType1, bakedPart) -> transformer.accept(matrixStack, model));
        }

        public void registerItem(ItemCameraTransforms.TransformType transformType, ITransform<T, M> transformer) {
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
