package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedFirstPersonArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedItemModel;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TickUtils;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.function.Predicate;
import java.util.function.Supplier;

import manifold.ext.rt.api.auto;

@Environment(EnvType.CLIENT)
public class ClientWardrobeHandler {

    private static Runnable INVENTORY_RENDER_POST_EVENT = null;
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

    public static void onRenderTrident(ThrownTrident entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
        auto renderData = SkinRenderData.of(entity);
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

        auto armature = BakedArmature.defaultBy(Armatures.ANY);
        auto context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), poseStack, buffers);
        int count = render(entity, armature, context, renderData::getItemSkins);
        if (count != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderArrow(AbstractArrow entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        auto entry = getEntry(renderData.getItemSkins(), part -> part.getType() == SkinPartTypes.ITEM_ARROW);
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

        auto armature = BakedArmature.defaultBy(Armatures.ANY);
        auto context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), poseStack, buffers);
        int count = render(entity, armature, context, () -> Collections.singletonList(entry));
        if (count != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderFishingHook(FishingHook entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, CallbackInfo callback) {
        auto player = entity.getPlayerOwner();
        auto renderData = SkinRenderData.of(entity);
        if (player == null || renderData == null) {
            return;
        }
        auto itemStack = player.getMainHandItem();
        if (!itemStack.is(Items.FISHING_ROD)) {
            itemStack = player.getOffhandItem();
        }
        auto entry = createEntry(itemStack, part -> part.getType() == SkinPartTypes.ITEM_FISHING_HOOK);
        if (entry == null) {
            return; // we just need to render with the arrows.
        }
        poseStack.pushPose();

        Vector3f rotation = Minecraft.getInstance().getCameraOrientation().toYXZ();
        poseStack.mulPose(OpenQuaternionf.fromYXZ(rotation.getY(), 0, 0));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0f));

        poseStack.scale(-SCALE, -SCALE, SCALE);
        poseStack.translate(-0.5, -3.0, 0.0);

        auto armature = BakedArmature.defaultBy(Armatures.ANY);
        auto context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), poseStack, buffers);
        int count = render(entity, armature, context, () -> Collections.singletonList(entry));
        if (count != 0 && !ModDebugger.fishingHook) {
            callback.cancel();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderBoat(Boat entity, Model model, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers) {
        auto renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
//        SkinRendererManager.getInstance().willRender(entity, model, null, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
//        SkinRendererManager.getInstance().willRenderModel(entity, model, null, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));

        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);


        // transformer =

        BakedArmature armature = BakedArmature.defaultBy(Armatures.BOAT);
        SkinRenderContext context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), AbstractItemTransformType.NONE, poseStack, buffers);
        render(entity, armature, context, renderData::getArmorSkins);
        context.release();

        poseStack.popPose();


//        SkinRendererManager.getInstance().didRender(entity, model, null, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
    }

    public static void onRenderSpecificHand(LivingEntity entity, float partialTicks, int packedLight, AbstractItemTransformType transformType, PoseStack poseStack, MultiBufferSource buffers, Runnable cancelHandler) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        auto armature = BakedFirstPersonArmature.defaultBy(transformType);
        auto context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), transformType, poseStack, buffers);
        int count = render(entity, armature, context, renderData::getArmorSkins);
        if (count != 0 && !ModDebugger.handOverride) {
            cancelHandler.run();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderLivingEntityPre(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    public static void onRenderLivingEntity(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().willRenderModel(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    public static void onRenderLivingEntityPost(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStack, MultiBufferSource buffers, LivingEntityRenderer<?, ?> entityRenderer) {
        SkinRenderData renderData = SkinRenderData.of(entity);
        if (renderData != null) {
            SkinRendererManager.getInstance().didRender(entity, entityRenderer.getModel(), entityRenderer, renderData, () -> SkinRenderContext.alloc(renderData, packedLight, partialTicks, poseStack, buffers));
        }
    }

    @Nullable
    public static EmbeddedSkinStack getEmbeddedSkinStack(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, AbstractItemTransformType transformType) {
        if (RENDERING_GUI_ITEM != itemStack) {
            // when the wardrobe has override skin of the item,
            // we easily got a conclusion of the needs embedded skin.
            SkinRenderData renderData = SkinRenderData.of(entity);
            if (renderData != null) {
                for (SkinRenderData.Entry entry : renderData.getItemSkins(itemStack, entity instanceof MannequinEntity)) {
                    return new EmbeddedSkinStack(0, entry);
                }
            }
        }
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            return null;
        }
        // when the item is a skin item itself,
        // we easily got a conclusion of the needs embedded skin.
        if (itemStack.is(ModItems.SKIN.get())) {
            return new EmbeddedSkinStack(2, descriptor, itemStack);
        }
        // we allow server manually control the item whether to use the embedded renderer.
        if (descriptor.getOptions().getEmbeddedItemRenderer() != 0) {
            if (descriptor.getOptions().getEmbeddedItemRenderer() == 2) {
                return new EmbeddedSkinStack(1, descriptor, itemStack);
            }
            return null;
        }
        // when the skin item, we no required enable of embed skin option in the config.
        if (ModConfig.enableEmbeddedSkinRenderer() || descriptor.getType() == SkinTypes.ITEM) {
            return new EmbeddedSkinStack(1, descriptor, itemStack);
        }
        return null;
    }

    public static void renderEmbeddedSkin(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, BakedModel bakedModel, int packedLight, int overlay, CallbackInfo callback) {
        int counter = 0;
        switch (transformType) {
            case GUI:
            case GROUND:
            case FIXED: {
                counter = _renderEmbeddedSkinInBox(embeddedStack, transformType, leftHandHackery, poseStack, buffers, packedLight, overlay);
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
                        counter = _renderEmbeddedSkinInBox(embeddedStack, transformType, leftHandHackery, poseStack, buffers, packedLight, overlay);
                    } else {
                        counter = _renderEmbeddedSkin(embeddedStack, transformType, leftHandHackery, poseStack, buffers, packedLight, overlay);
                    }
                    break;
                }
                SkinRenderData renderData = SkinRenderData.of(entity);
                if (renderData != null) {
//                    poseStack.translate(0, 1, -2);
//                    RenderUtils.drawPoint(poseStack, null, 2, buffers);
                    poseStack.pushPose();
                    poseStack.scale(-SCALE, -SCALE, SCALE);
                    auto armature = BakedArmature.defaultBy(Armatures.ANY);
                    auto context = SkinRenderContext.alloc(renderData, packedLight, TickUtils.ticks(), transformType, poseStack, buffers);
                    context.setReferenced(SkinItemSource.create(800, itemStack, transformType));
                    counter = render(entity, armature, context, () -> Collections.singleton(embeddedStack.getEntry()));
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
        if (counter != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
    }

    private static int _renderEmbeddedSkinInBox(EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay) {
        int count = 0;
        auto descriptor = embeddedStack.getDescriptor();
        auto bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.INVENTORY);
        if (bakedSkin == null) {
            return count;
        }
        auto rotation = Vector3f.ZERO;
        auto scale = Vector3f.ONE;

        poseStack.pushPose();

        // for skin of the custom item transforms, we will respect its options.
        if (bakedSkin.getItemModel() == null) {
            auto itemModel = BakedItemModel.DEFAULT;
            auto transform = itemModel.getTransform(transformType);

            // when skin not specify item transforms,
            // we need to apply a default item transforms.
            itemModel.applyTransform(transformType, leftHandHackery, poseStack);

            rotation = new Vector3f(-transform.rotation.x(), -transform.rotation.y(), transform.rotation.z());
            scale = new Vector3f(transform.scale.x(), transform.scale.y(), transform.scale.z());
        }

        SkinItemSource itemSource = SkinItemSource.create(descriptor.sharedItemStack());
        itemSource.setScale(scale);
        itemSource.setRotation(rotation);
        itemSource.setTransformType(transformType);

        ColorScheme scheme = descriptor.getColorScheme();
        count = ExtendedItemRenderer.renderSkinInBox(bakedSkin, scheme, scale, 0, packedLight, itemSource, poseStack, buffers);

        poseStack.popPose();

        return count;
    }

    private static int _renderEmbeddedSkin(EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int overlay) {
        int count = 0;
        SkinDescriptor descriptor = embeddedStack.getDescriptor();
        SkinRenderTesselator context = SkinRenderTesselator.create(descriptor, Tickets.INVENTORY);
        if (context == null) {
            return count;
        }
        poseStack.pushPose();
        poseStack.scale(-16, -16, 16);

        context.setRenderData(SkinRenderData.of(context.getMannequin()));
        context.setLightmap(packedLight);
        context.setPartialTicks(0);
        context.setReferenced(SkinItemSource.create(800, embeddedStack.getItemStack(), transformType));
        context.setColorScheme(descriptor.getColorScheme());

        count = context.draw(poseStack, buffers);

        poseStack.popPose();
        return count;
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
        INVENTORY_RENDER_POST_EVENT = RenderSystem::removeClipRect;
    }

    public static void onRenderEntityInInventoryPost(LivingEntity entity) {
        if (INVENTORY_RENDER_POST_EVENT != null) {
            INVENTORY_RENDER_POST_EVENT.run();
            INVENTORY_RENDER_POST_EVENT = null;
        }
    }

    private static int render(Entity entity, BakedArmature bakedArmature, SkinRenderContext context, Supplier<Iterable<SkinRenderData.Entry>> provider) {
        int r = 0;
        for (SkinRenderData.Entry entry : provider.get()) {
            SkinItemSource itemSource = context.getReferenced();
            ItemStack itemStack = itemSource.getItem();
            if (itemStack.isEmpty()) {
                itemStack = entry.getItemStack();
            }
            if (itemSource == SkinItemSource.EMPTY) {
                itemSource = SkinItemSource.create(itemStack);
            }
            itemSource.setItem(itemStack);
            itemSource.setRenderPriority(entry.getRenderPriority());
            context.setReferenced(itemSource);
            auto bakedSkin = entry.getBakedSkin();
            bakedSkin.setupAnim(entity, context.getPartialTicks(), itemSource);
            r += SkinRenderer.render(entity, bakedArmature, bakedSkin, entry.getBakedScheme(), context);
        }
        return r;
    }

    private static boolean shouldRenderInBox(EmbeddedSkinStack embeddedStack) {
        // for the item required render to box.
        if (embeddedStack.getMode() == 2) {
            return true;
        }
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

    private static SkinRenderData.Entry createEntry(ItemStack itemStack, Predicate<BakedSkinPart> predicate) {
        auto descriptor = SkinDescriptor.of(itemStack);
        auto bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.RENDERER);
        if (bakedSkin == null) {
            return null;
        }
        for (BakedSkinPart part : bakedSkin.getParts()) {
            if (predicate.test(part)) {
                return new SkinRenderData.Entry(itemStack, descriptor, bakedSkin, ColorScheme.EMPTY, 0, true);
            }
        }
        return null;
    }

    private static SkinRenderData.Entry getEntry(Iterable<SkinRenderData.Entry> entries, Predicate<BakedSkinPart> predicate) {
        for (SkinRenderData.Entry entry1 : entries) {
            for (BakedSkinPart part : entry1.getBakedSkin().getParts()) {
                if (predicate.test(part)) {
                    return entry1;
                }
            }
        }
        return null;
    }
}
