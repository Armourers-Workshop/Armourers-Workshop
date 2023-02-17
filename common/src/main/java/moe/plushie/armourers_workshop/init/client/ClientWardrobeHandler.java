package moe.plushie.armourers_workshop.init.client;

import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStroage;
import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.client.model.MannequinModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.platform.TransformationProvider;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ModelHolder;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Environment(value = EnvType.CLIENT)
public class ClientWardrobeHandler {

    public final static float SCALE = 1 / 16f;

    public static void init() {
    }

    public static void onRenderTrident(ThrownTrident entity, Model model, float partialTicks, int packedLight, IPoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        poseStack.pushPose();

        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;

        poseStack.rotate(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        poseStack.rotate(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot) + 90.0F));

        poseStack.rotate(Vector3f.ZP.rotationDegrees(180));
        poseStack.rotate(Vector3f.YP.rotationDegrees(-90));

        poseStack.scale(-SCALE, -SCALE, SCALE);
        poseStack.translate(0, 11, 0);

        SkinRenderContext context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), poseStack, buffers);
        int count = render(entity, model, context, renderData::getItemSkins);
        if (count != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderArrow(AbstractArrow entity, Model model, float partialTicks, int packedLight, IPoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        SkinRenderData.Entry entry = getEntry(renderData.getItemSkins(), part -> part.getType() == SkinPartTypes.ITEM_ARROW);
        if (entry == null) {
            return; // we just need to render with the arrows.
        }
        poseStack.pushPose();

        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;

        poseStack.rotate(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        poseStack.rotate(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot)));

        float f9 = (float) entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathUtils.sin(f9 * 3.0F) * f9;
            poseStack.rotate(Vector3f.ZP.rotationDegrees(f10));
        }

        poseStack.rotate(Vector3f.YP.rotationDegrees(-90));
        poseStack.scale(-SCALE, -SCALE, SCALE);
        poseStack.translate(0, 0, -1);

        SkinRenderContext context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), poseStack, buffers);
        int count = render(entity, model, context, () -> Collections.singletonList(entry));
        if (count != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderSpecificHand(LivingEntity entity, float partialTicks, int packedLight, ItemTransforms.TransformType transformType, IPoseStack poseStack, MultiBufferSource buffers, Runnable cancelHandler) {
        FirstPersonPlayerModel<?> model = FirstPersonPlayerModel.getInstance();
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        SkinRenderContext context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), transformType, poseStack, buffers);
        int count = render(entity, model, context, renderData::getArmorSkins);
        if (count != 0 && !ModDebugger.handOverride) {
            cancelHandler.run();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderLivingPre(LivingEntity entity, float partialTicks, int packedLight, IPoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    public static void onRenderLiving(LivingEntity entity, float partialTicks, int packedLight, IPoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRenderModel(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    public static void onRenderLivingPost(LivingEntity entity, float partialTicks, int packedLight, IPoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    @Nullable
    public static EmbeddedSkinStack getEmbeddedSkinStack(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, ItemTransforms.TransformType transformType) {
        // this a silly solution, but I don't want to depend more mixins.
        // in ground: level and entity is empty.
        // in gui: level is empty.
        if (level != null && entity != null) {
            // when the wardrobe has override skin of the item,
            // we easily get a conclusion to needs embedded skin.
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData != null) {
                for (SkinRenderData.Entry entry : renderData.getItemSkins(itemStack, entity instanceof MannequinEntity)) {
                    return new EmbeddedSkinStack(entry);
                }
            }
        }
        // when the item is a skin item itself,
        // we easily get a conclusion to no needs embedded skin
        if (itemStack.getItem() == ModItems.SKIN.get()) {
            return null;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return null;
        }
        // we allow server manually control the item whether to use the embedded renderer.
        if (descriptor.getOptions().getEmbeddedItemRenderer() != 0) {
            if (descriptor.getOptions().getEmbeddedItemRenderer() == 2) {
                return new EmbeddedSkinStack(descriptor, itemStack);
            }
            return null;
        }
        // when the skin item, we no required enable of embbed skin option in the config.
        if (ModConfig.enableEmbeddedSkinRenderer() || descriptor.getType() == SkinTypes.ITEM) {
            return new EmbeddedSkinStack(descriptor, itemStack);
        }
        return null;
    }

    public static void renderEmbeddedSkin(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, EmbeddedSkinStack embeddedStack, ItemTransforms.TransformType transformType, boolean leftHandHackery, IPoseStack poseStack, MultiBufferSource buffers, BakedModel bakedModel, int packedLight, int overlay, CallbackInfo callback) {
        int counter = 0;
        switch (transformType) {
            case GUI:
            case GROUND:
            case FIXED: {
                _renderEmbeddedSkinInBox(embeddedStack, transformType, leftHandHackery, poseStack, buffers, packedLight, overlay);
                callback.cancel();
                break;
            }
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND: {
                // in special case, entity hold item type skin.
                // so we need replace it to custom renderer.
                if (embeddedStack.getEntry() == null) {
                    if (shouldRenderInBox(embeddedStack)) {
                        _renderEmbeddedSkinInBox(embeddedStack, transformType, leftHandHackery, poseStack, buffers, packedLight, overlay);
                    } else {
                        _renderEmbeddedSkin(embeddedStack, transformType, leftHandHackery, poseStack, buffers, packedLight, overlay);
                    }
                    callback.cancel();
                    return;
                }
                SkinRenderData renderData = SkinRenderData.of(entity);
                if (renderData != null) {
//                    poseStack.translate(0, 1, -2);
//                    RenderUtils.drawPoint(poseStack, null, 2, buffers);
                    poseStack.pushPose();
                    poseStack.scale(-SCALE, -SCALE, SCALE);
                    SkinRenderContext context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), transformType, poseStack, buffers);
                    context.setItem(itemStack, 9);
                    counter = render(entity, null, context, () -> Collections.singleton(embeddedStack.getEntry()));
                    if (counter != 0 && !ModDebugger.itemOverride) {
                        callback.cancel();
                    }
                    context.release();
                    poseStack.popPose();
                }
                break;
            }
            default: {
                // we not support unknown operates.
                break;
            }
        }
    }

    public static void _renderEmbeddedSkinInBox(EmbeddedSkinStack embeddedStack, ItemTransforms.TransformType transformType, boolean leftHandHackery, IPoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay) {
        SkinDescriptor descriptor = embeddedStack.getDescriptor();
        poseStack.pushPose();
        TransformationProvider.handleTransforms(poseStack, BakedModelStroage.getSkinBakedModel(), transformType, leftHandHackery);
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        SkinItemRenderer.getInstance().renderByItem(descriptor.sharedItemStack(), transformType, poseStack.cast(), buffers, packedLight, overlay);
        poseStack.popPose();
    }

    public static int _renderEmbeddedSkin(EmbeddedSkinStack embeddedStack, ItemTransforms.TransformType transformType, boolean leftHandHackery, IPoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay) {
        int r = 0;
        SkinDescriptor descriptor = embeddedStack.getDescriptor();
        BakedSkin bakedSkin = BakedSkin.of(descriptor);
        if (bakedSkin == null) {
            return r;
        }
        LivingEntity entity = SkinItemRenderer.getInstance().getMannequinEntity();
        MannequinModel<?> model = SkinItemRenderer.getInstance().getMannequinModel();
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        SkinRenderData renderData = SkinRenderData.of(entity);
        IModelHolder<Model> modelHolder = ModelHolder.of(model);
        if (renderer == null || renderData == null || modelHolder == null) {
            return r;
        }
        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);
        SkinRenderContext context = SkinRenderContext.alloc(renderData, packedLight, 0, poseStack, buffers);
        context.setItem(embeddedStack.getItemStack(), 9);
        context.setTransforms(entity, renderer.getOverrideModel(modelHolder));
        renderer.render(entity, modelHolder, bakedSkin, descriptor.getColorScheme(), context);
        context.release();
        poseStack.popPose();
        return r;
    }

    public static void onRenderEntityInInventoryPre(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY) {
        if (!ModConfig.Client.enableEntityInInventoryClip) {
            return;
        }
        int left, top, width, height;
        switch (scale) {
            case 20: // in creative container screen
                width = 32;
                height = 43;
                left = x - width / 2 + 1;
                top = y - height + 4;
                break;

            case 30: // in survival container screen
                width = 49;
                height = 70;
                left = x - width / 2 - 1;
                top = y - height + 3;
                break;

            default:
                return;
        }
        RenderSystem.addClipRect(left, top, width, height);
    }

    public static void onRenderEntityInInventoryPost(LivingEntity entity) {
        if (!ModConfig.Client.enableEntityInInventoryClip) {
            return;
        }
        RenderSystem.removeClipRect();
    }

    private static int render(Entity entity, Model model, SkinRenderContext context, Supplier<Iterable<SkinRenderData.Entry>> provider) {
        int r = 0;
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null) {
            return 0;
        }
        IModelHolder<Model> modelHolder = ModelHolder.ofNullable(model);
        for (SkinRenderData.Entry entry : provider.get()) {
            context.slotIndex = entry.getSlotIndex();
            if (context.itemStack == ItemStack.EMPTY) {
                context.itemStack = entry.getItemStack();
            }
            context.setTransforms(entity, renderer.getOverrideModel(modelHolder));
            r += renderer.render(entity, modelHolder, entry.getBakedSkin(), entry.getBakedScheme(), context);
        }
        return r;
    }

    private static boolean shouldRenderInBox(EmbeddedSkinStack embeddedStack) {
        ISkinType skinType = embeddedStack.getDescriptor().getType();
        // for the tool type skin, don't render in the box.
        if (skinType instanceof ISkinToolType) {
            return false;
        }
        // for the item type skin, don't render in the box.
        if (skinType == SkinTypes.ITEM) {
            return false;
        }
        return true;
    }

    private static SkinRenderData.Entry getEntry(Iterable<SkinRenderData.Entry> entries, Predicate<BakedSkinPart> predicate) {
        for (SkinRenderData.Entry entry1 : entries) {
            for (BakedSkinPart part : entry1.getBakedSkin().getSkinParts()) {
                if (predicate.test(part)) {
                    return entry1;
                }
            }
        }
        return null;
    }
}
