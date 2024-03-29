package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStorage;
import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
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

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class ClientWardrobeHandler {

    public static ItemStack RENDERING_GUI_ITEM = null;
    public static final float SCALE = 1 / 16f;

    public static void init() {
    }

    public static void startRenderGuiItem(ItemStack itemStack) {
        RENDERING_GUI_ITEM = itemStack;
    }

    public static void endRenderGuiItem(ItemStack itemStack) {
        RENDERING_GUI_ITEM = null;
    }

    public static void onRenderTrident(ThrownTrident entity, Model model, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        poseStack.pushPose();

        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;

        poseStack.mulPose(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot) + 90.0F));

        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-90));

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

    public static void onRenderArrow(AbstractArrow entity, Model model, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
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

        poseStack.mulPose(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot)));

        float f9 = (float) entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathUtils.sin(f9 * 3.0F) * f9;
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
        }

        poseStack.mulPose(Vector3f.YP.rotationDegrees(-90));
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

    public static void onRenderSpecificHand(LivingEntity entity, float partialTicks, int packedLight, AbstractItemTransformType transformType, PoseStack poseStack, MultiBufferSource buffers, Runnable cancelHandler) {
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

    public static void onRenderLivingPre(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    public static void onRenderLiving(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRenderModel(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    public static void onRenderLivingPost(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    @Nullable
    public static EmbeddedSkinStack getEmbeddedSkinStack(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, AbstractItemTransformType transformType) {
        if (RENDERING_GUI_ITEM != itemStack) {
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
        if (itemStack.is(ModItems.SKIN.get())) {
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

    public static void renderEmbeddedSkin(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, BakedModel bakedModel, int packedLight, int overlay, CallbackInfo callback) {
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
                    context.setReference(800, itemStack);
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

    public static void _renderEmbeddedSkinInBox(EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay) {
        SkinDescriptor descriptor = embeddedStack.getDescriptor();
        poseStack.pushPose();
        TransformationProvider.handleTransforms(poseStack, BakedModelStorage.getSkinBakedModel(), transformType, leftHandHackery);
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        SkinItemRenderer.getInstance().renderByItem(descriptor.sharedItemStack(), transformType, poseStack, buffers, packedLight, overlay);
        poseStack.popPose();
    }

    public static int _renderEmbeddedSkin(EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay) {
        int counter = 0;
        SkinDescriptor descriptor = embeddedStack.getDescriptor();
        SkinRenderTesselator context = SkinRenderTesselator.create(descriptor, Tickets.INVENTORY);
        if (context == null) {
            return counter;
        }
        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        context.setRenderData(SkinRenderData.of(context.getMannequin()));
        context.setLightmap(packedLight);
        context.setPartialTicks(0);
        context.setReference(embeddedStack.getItemStack(), 800, null);
        context.setColorScheme(descriptor.getColorScheme());

        counter = context.draw(poseStack, buffers);

        poseStack.popPose();
        return counter;
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
        auto renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null) {
            return 0;
        }
        IModel modelHolder = ModelHolder.ofNullable(model);
        for (SkinRenderData.Entry entry : provider.get()) {
            ItemStack itemStack = context.getReference();
            if (itemStack.isEmpty()) {
                itemStack = entry.getItemStack();
            }
            context.setReference(entry.getRenderPriority(), itemStack);
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
