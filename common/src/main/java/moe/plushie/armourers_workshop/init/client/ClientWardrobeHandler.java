package moe.plushie.armourers_workshop.init.client;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.model.IModelHolder;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.model.BakedModelStroage;
import moe.plushie.armourers_workshop.core.client.model.FirstPersonPlayerModel;
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
import moe.plushie.armourers_workshop.utils.MathUtils;
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

    public static void onRenderTrident(ThrownTrident entity, Model model, float p_225623_2_, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        matrixStack.pushPose();

        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot) + 90.0F));

        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));

        matrixStack.scale(-SCALE, -SCALE, SCALE);
        matrixStack.translate(0, 11, 0);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(light, TickUtils.ticks(), matrixStack, buffers);
        int count = render(entity, null, model, context, renderData::getItemSkins);
        if (count != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
        context.clean();

        matrixStack.popPose();
    }

    public static void onRenderArrow(AbstractArrow entity, Model model, float p_225623_2_, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers, CallbackInfo callback) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        SkinRenderData.Entry entry = getEntry(renderData.getItemSkins(), part -> part.getType() == SkinPartTypes.ITEM_ARROW);
        if (entry == null) {
            return; // we just need to render with the arrows.
        }
        matrixStack.pushPose();

        float xRot = entity.getXRot();
        float yRot = entity.getYRot();
        float xRotO = entity.xRotO;
        float yRotO = entity.yRotO;

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathUtils.lerp(partialTicks, yRotO, yRot) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathUtils.lerp(partialTicks, xRotO, xRot)));

        float f9 = (float) entity.shakeTime - partialTicks;
        if (f9 > 0.0F) {
            float f10 = -MathUtils.sin(f9 * 3.0F) * f9;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(f10));
        }

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90));
        matrixStack.scale(-SCALE, -SCALE, SCALE);
        matrixStack.translate(0, 0, -1);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(light, TickUtils.ticks(), matrixStack, buffers);
        int count = render(entity, null, model, context, () -> Collections.singletonList(entry));
        if (count != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
        context.clean();

        matrixStack.popPose();
    }

//    public static void onRenderArmorPre(Entity entity, EntityModel<?> entityModel, int light, PoseStack matrixStack, MultiBufferSource buffers) {
//        // apply the model baby scale.
//        if (entityModel.young && entityModel instanceof HumanoidModel<?>) {
//            HumanoidModel<?> bipedModel = (HumanoidModel<?>) entityModel;
//            float scale = 1.0f / bipedModel.babyBodyScale;
//            matrixStack.scale(scale, scale, scale);
//            matrixStack.translate(0.0f, bipedModel.bodyYOffset / 16.0f, 0.0f);
//        }
//    }
//
//    public static void onRenderArmor(Entity entity, Model model, int light, PoseStack matrixStack, MultiBufferSource buffers) {
//        SkinRenderData renderData = SkinRenderData.of(entity);
//        if (renderData == null) {
//            return;
//        }
//        matrixStack.pushPose();
//        matrixStack.scale(SCALE, SCALE, SCALE);
//
//        render(entity, model, light, matrixStack, buffers, null, renderData::getArmorSkins);
//
//        matrixStack.popPose();
//    }

//    public static void onRenderItem(Entity entity, ItemStack itemStack, ItemTransforms.TransformType transformType, int light, PoseStack matrixStack, MultiBufferSource buffers, CallbackInfo callback) {
//        SkinRenderData renderData = SkinRenderData.of(entity);
//        if (renderData == null) {
//            return;
//        }
//        matrixStack.pushPose();
//        matrixStack.scale(-SCALE, -SCALE, SCALE);
//
//        boolean replaceSkinItem = entity instanceof MannequinEntity;
//        int count = render(entity, null, light, matrixStack, buffers, transformType, () -> renderData.getItemSkins(itemStack, replaceSkinItem));
//        if (count != 0) {
//            callback.cancel();
//        }
//
//        matrixStack.popPose();
//    }
//
//    public static ItemStack getRenderSkinStack(ItemStack itemStack, boolean isRenderInGUI) {
//        if (isRenderInGUI && !ModConfig.Common.enableEmbeddedSkinRenderer) {
//            return itemStack;
//        }
//        if (itemStack.getItem() == ModItems.SKIN.get()) {
//            return itemStack;
//        }
//        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
//        if (!descriptor.isEmpty()) {
//            return descriptor.sharedItemStack();
//        }
//        return itemStack;
//    }

    public static void onRenderSpecificHand(LivingEntity entity, float p_225623_2_, int light, float partialTicks, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffers, Runnable cancelHandler) {
        FirstPersonPlayerModel<?> model = FirstPersonPlayerModel.getInstance();
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        matrixStack.pushPose();
        matrixStack.scale(-SCALE, -SCALE, SCALE);

        SkinRenderContext context = SkinRenderContext.getInstance();
        context.setup(light, TickUtils.ticks(), transformType, matrixStack, buffers);
        int count = render(entity, null, model, context, renderData::getArmorSkins);
        if (count != 0 && !ModDebugger.handOverride) {
            cancelHandler.run();
        }
        context.clean();

        matrixStack.popPose();
    }

    public static void onRenderLivingPre(LivingEntity entity, float p_225623_2_, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> {
                SkinRenderContext renderContext = SkinRenderContext.getInstance();
                renderContext.setup(light, partialTicks, matrixStack, buffers);
                return renderContext;
            });
        }
    }

    public static void onRenderLiving(LivingEntity entity, float p_225623_2_, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRenderModel(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> {
                SkinRenderContext renderContext = SkinRenderContext.getInstance();
                renderContext.setup(light, partialTicks, matrixStack, buffers);
                return renderContext;
            });
        }
    }

    public static void onRenderLivingPost(LivingEntity entity, float p_225623_2_, float partialTicks, int light, PoseStack matrixStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> {
                SkinRenderContext renderContext = SkinRenderContext.getInstance();
                renderContext.setup(light, partialTicks, matrixStack, buffers);
                return renderContext;
            });
        }
    }

    public static boolean shouldRenderEmbeddedSkin(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, ItemTransforms.TransformType transformType) {
        // this a silly solution, but I don't want to depend more mixins.
        // in ground: level and entity is empty.
        // in gui: level is empty.
        if (level != null && entity != null) {
            // when the wardrobe has override skin of the item,
            // we easily get a conclusion to needs embedded skin.
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData != null && !Iterables.isEmpty(renderData.getItemSkins(itemStack, entity instanceof MannequinEntity))) {
                return true;
            }
        }
        // when the item is a skin item itself,
        // we easily get a conclusion to no needs embedded skin
        if (itemStack.getItem() == ModItems.SKIN.get()) {
            return false;
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return false;
        }
        // when the skin item, we no required enable of embbed skin option in the config.
        return ModConfig.enableEmbeddedSkinRenderer() || descriptor.getType() == SkinTypes.ITEM ;
    }

    public static void renderEmbeddedSkin(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, ItemTransforms.TransformType transformType, boolean leftHandHackery, PoseStack matrixStack, MultiBufferSource buffers, BakedModel bakedModel, int packedLight, int overlay, CallbackInfo callback) {
        int counter = 0;
        switch (transformType) {
            case GUI:
            case GROUND:
            case FIXED: {
                SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
                if (descriptor.isEmpty()) {
                    return;
                }
                matrixStack.pushPose();
                TransformationProvider.handleTransforms(matrixStack, BakedModelStroage.getSkinBakedModel(), transformType, leftHandHackery);
                matrixStack.translate(-0.5D, -0.5D, -0.5D);
                SkinItemRenderer.getInstance().renderByItem(descriptor.sharedItemStack(), transformType, matrixStack, buffers, packedLight, overlay);
                callback.cancel();
                matrixStack.popPose();
                break;
            }
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND: {
                // in special case, entity hold a item type skin.
                // so we need replace it to custom renderer.
                SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
                if (!descriptor.isEmpty() && !(descriptor.getType() instanceof ISkinToolType)) {
                    matrixStack.pushPose();
                    TransformationProvider.handleTransforms(matrixStack, BakedModelStroage.getSkinBakedModel(), transformType, leftHandHackery);
                    matrixStack.translate(-0.5D, -0.5D, -0.5D);
                    SkinItemRenderer.getInstance().renderByItem(descriptor.sharedItemStack(), transformType, matrixStack, buffers, packedLight, overlay);
                    callback.cancel();
                    matrixStack.popPose();
                    return;
                }
                SkinRenderData renderData = SkinRenderData.of(entity);
                if (renderData != null) {
//                    matrixStack.translate(0, 1, -2);
//                    RenderUtils.drawPoint(matrixStack, null, 2, buffers);
                    matrixStack.pushPose();
                    matrixStack.scale(-SCALE, -SCALE, SCALE);
                    boolean replaceSkinItem = entity instanceof MannequinEntity;
                    SkinRenderContext context = SkinRenderContext.getInstance();
                    context.setup(packedLight, TickUtils.ticks(), transformType, matrixStack, buffers);
                    counter = render(entity, itemStack, null, context, () -> renderData.getItemSkins(itemStack, replaceSkinItem));
                    if (counter != 0 && !ModDebugger.itemOverride) {
                        callback.cancel();
                    }
                    context.clean();
                    matrixStack.popPose();
                }
                break;
            }
            default: {
                // we not support unknown operates.
                break;
            }
        }
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

    private static int render(Entity entity, ItemStack itemStack, Model model, SkinRenderContext context, Supplier<Iterable<SkinRenderData.Entry>> provider) {
        int r = 0;
        SkinRenderer<Entity, Model, IModelHolder<Model>> renderer = SkinRendererManager.getInstance().getRenderer(entity, model, null);
        if (renderer == null) {
            return 0;
        }
        IModelHolder<Model> modelHolder = SkinRendererManager.wrap(model);
        for (SkinRenderData.Entry entry : provider.get()) {
            if (itemStack == null) {
                itemStack = entry.getItemStack();
            }
            r += renderer.render(entity, modelHolder, entry.getBakedSkin(), entry.getBakedScheme(), itemStack, entry.getSlotIndex(), context);
        }
        return r;
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
