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
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.extened.AWMatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class SkinRenderer<T extends Entity, M extends Model> {

    protected final EntityProfile profile;
    protected final Transformer<M> transformer = new Transformer<>();

    protected final ArrayList<ModelRenderer> overriders = new ArrayList<>();

    public SkinRenderer(EntityProfile profile) {
        this.profile = profile;
    }

    public void init(EntityRenderer<T> entityRenderer) {
    }

    public void initTransformers() {
    }

    public boolean prepare(T entity, M model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemCameraTransforms.TransformType transformType) {
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


    public void apply(T entity, M model, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart, float partialTicks, MatrixStack matrixStack) {
        ITransform<M> op = getPartTransform(entity, model, transformType, bakedPart);
        if (op != null && model != null) {
            op.apply(matrixStack, model, transformType, bakedPart);
            SkinUtils.apply(AWMatrixStack.wrap(matrixStack), bakedPart.getPart(), partialTicks, entity);
        }
    }

    public void apply(T entity, M model, EquipmentSlotType slotType, float partialTicks, MatrixStack matrixStack) {
    }


    public void willRender(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        overriders.clear();
    }

    public void willRenderModel(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        overriders.forEach(m -> m.visible = false);
    }

    public void render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, ItemCameraTransforms.TransformType transformType, int light, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        if (profile != null) {
            ISkinType type = bakedSkin.getType();
            if (type instanceof ISkinArmorType && !profile.canSupport(type)) {
                return;
            }
        }
        int index = 0;
        Skin skin = bakedSkin.getSkin();
        ColorScheme scheme1 = bakedSkin.resolve(entity, scheme);
        SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
        SkinRenderObjectBuilder builder = bufferBuilder.getBuffer(skin);
        for (BakedSkinPart bakedPart : bakedSkin.getSkinParts()) {
            if (!prepare(entity, model, bakedSkin, bakedPart, transformType)) {
                continue;
            }
            boolean shouldRenderPart = bakedSkin.shouldRenderPart(bakedPart, entity, transformType);
            matrixStack.pushPose();
            apply(entity, model, transformType, bakedPart, partialTicks, matrixStack);
            builder.addPartData(bakedPart, scheme1, light, partialTicks, matrixStack, shouldRenderPart);
            if (shouldRenderPart && ModConfig.Client.debugSkinPartBounds) {
                builder.addShapeData(bakedPart.getRenderShape().bounds(), ColorUtils.getPaletteColor(index++), matrixStack);
            }
            if (shouldRenderPart && ModConfig.Client.debugSkinPartOrigin) {
                builder.addShapeData(AWConstants.ZERO, matrixStack);
            }
            matrixStack.popPose();
        }

        if (ModConfig.Client.debugSkinBounds) {
            builder.addShapeData(bakedSkin.getRenderShape(entity, model, transformType).bounds(), Color.RED, matrixStack);
        }
        if (ModConfig.Client.debugSkinBounds) {
            builder.addShapeData(AWConstants.ZERO, matrixStack);
        }
    }

    public void didRender(T entity, M model, int light, float partialRenderTick, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        for (ModelRenderer modelRenderer : overriders) {
            modelRenderer.visible = true;
        }
        overriders.clear();
    }

    protected void addOverrider(ModelRenderer modelRenderer) {
        if (!modelRenderer.visible) {
            return;
        }
        modelRenderer.visible = false;
        overriders.add(modelRenderer);
    }

    public ITransform<M> getPartTransform(T entity, M model, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart) {
        ISkinPartType partType = bakedPart.getType();
        ITransform<M> transform = null;
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
    public interface ITransform<M> {
        void apply(MatrixStack matrixStack, M model, ItemCameraTransforms.TransformType transformType, BakedSkinPart bakedPart);
    }

    public static class Transformer<M> {

        final HashMap<ISkinPartType, ITransform<M>> armors = new HashMap<>();
        final HashMap<ItemCameraTransforms.TransformType, ITransform<M>> items = new HashMap<>();

        public static <M> void none(MatrixStack matrixStack, M model) {
        }

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
