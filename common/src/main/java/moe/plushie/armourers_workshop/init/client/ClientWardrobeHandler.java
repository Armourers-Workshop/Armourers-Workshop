package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedFirstPersonArmature;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.model.EmbeddedItemModel;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.FindableSkinManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.FallbackEntityRenderPatch;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.LivingEntityRenderPatch;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ClientWardrobeHandler {

    private static Runnable INVENTORY_RENDER_POST_EVENT = null;
    public static ItemStack RENDERING_GUI_ITEM = null;

    public static final float SCALE = 1 / 16f;

    public static void init() {
    }

    public static void tick(Entity entity) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            renderData.tick(entity);
        }
        for (var passenger : entity.getPassengers()) {
            tick(passenger);
        }
    }

    public static void startRenderGuiItem(ItemStack itemStack) {
        RENDERING_GUI_ITEM = itemStack;
    }

    public static void endRenderGuiItem(ItemStack itemStack) {
        RENDERING_GUI_ITEM = null;
    }

    public static void onRenderSpecificHand(LivingEntity entity, float partialTicks, int packedLight, OpenItemDisplayContext displayContext, PoseStack poseStackIn, MultiBufferSource buffersIn, Runnable cancelHandler) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var renderingTasks = renderData.armorSkins();
        if (renderingTasks.isEmpty()) {
            return;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var bufferSource = AbstractBufferSource.wrap(buffersIn);
        var armature = BakedFirstPersonArmature.defaultBy(displayContext);

        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        var overrideHandModel = renderData.overriddenManager().overrideHandModel(displayContext);
        var context = SkinRenderContext.alloc(renderData, packedLight, partialTicks, displayContext);

        context.setOverlay(OverlayTexture.NO_OVERLAY);
        context.setLightmap(packedLight);
        context.setPartialTicks(partialTicks);
        context.setAnimationTicks(TickUtils.animationTicks());

        context.setPoseStack(poseStack);
        context.setBufferSource(bufferSource);
        context.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

        context.setOutlineColor(0); // no show in head?

        int count = render(entity, armature, context, renderData.armorSkins());
        if (count != 0 && overrideHandModel && !ModDebugger.handOverride) {
            cancelHandler.run();
        }
        context.release();

        poseStack.popPose();
    }

    public static void onRenderEntityPre(Entity entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int packedLight, EntityRenderer<?> entityRenderer) {
        FallbackEntityRenderPatch.activate(entity, partialTicks, packedLight, poseStackIn, entityRenderer, null);
    }

    public static void onRenderEntity(Entity entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int packedLight, EntityRenderer<?> entityRenderer) {
        FallbackEntityRenderPatch.apply(entity, poseStackIn, bufferSourceIn, null);
    }

    public static void onRenderEntityPost(Entity entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, int packedLight, EntityRenderer<?> entityRenderer) {
        FallbackEntityRenderPatch.deactivate(entity, null);
    }


    public static void onRenderLivingEntityPre(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, LivingEntityRenderer<?, ?> entityRenderer) {
        LivingEntityRenderPatch.activate(entity, partialTicks, packedLight, poseStackIn, entityRenderer, null);
    }

    public static void onRenderLivingEntity(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, LivingEntityRenderer<?, ?> entityRenderer) {
        LivingEntityRenderPatch.apply(entity, poseStackIn, bufferSourceIn, null);
    }

    public static void onRenderLivingEntityPost(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, LivingEntityRenderer<?, ?> entityRenderer) {
        LivingEntityRenderPatch.deactivate(entity, null);
    }

    public static EmbeddedItemModel getEmbeddedItemModel(ItemStack itemStack, @Nullable LivingEntity entity, @Nullable Level level, BakedModel bakedModel) {
        // when the wardrobe has override skin of the item,
        // we easily got a conclusion of the needs embedded skin.
        // SkinRenderMode.inGUI()
        if (RENDERING_GUI_ITEM != itemStack) {
            var renderData = EntityRenderData.of(entity);
            if (renderData != null) {
                for (var entry : renderData.getItemSkins(itemStack, entity instanceof MannequinEntity)) {
                    return EmbeddedItemModel.fromWardrobe(entity, level, entry);
                }
            }
        }
        // Try to get skin descriptor from item stack.
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            // Try to get skin descriptor from item model config.
            descriptor = FindableSkinManager.getInstance().getSkin(bakedModel);
            if (!descriptor.isEmpty()) {
                return EmbeddedItemModel.fromComponent(entity, level, descriptor, itemStack);
            }
            return null;
        }
        // when the item is a skin item itself,
        // we easily got a conclusion of the needs embedded skin.
        if (itemStack.is(ModItems.SKIN.get())) {
            return EmbeddedItemModel.fromSelf(entity, level, descriptor, itemStack);
        }
        // we allow server manually control the item whether to use the embedded renderer.
        if (descriptor.options().embeddedItemRenderer() != 0) {
            if (descriptor.options().embeddedItemRenderer() == 2) {
                return EmbeddedItemModel.fromComponent(entity, level, descriptor, itemStack);
            }
            return null;
        }
        // when the skin item, we no required enable of embed skin option in the config.
        if (ModConfig.enableEmbeddedSkinRenderer() || descriptor.type() == SkinTypes.ITEM) {
            return EmbeddedItemModel.fromComponent(entity, level, descriptor, itemStack);
        }
        return null;
    }

    public static void renderEmbeddedItemModel(ItemStack itemStack, OpenItemDisplayContext displayContext, BakedModel bakedModel, EmbeddedItemModel itemModel, int packedLight, int overlay, boolean leftHandHackery, PoseStack poseStackIn, MultiBufferSource buffersIn, CallbackInfo callback) {
        // when the original not uses block light in the gui rendering case,
        // the vanilla will be set to lighting to flat items,
        // but we require lighting is 3d items.
        if (SkinRenderMode.inGUI() && !bakedModel.usesBlockLight()) {
            Lighting.setupFor3DItems();
        }
        int counter = 0;
        switch (displayContext) {
            case GUI:
            case GROUND:
            case FIXED: {
                counter = _renderEmbeddedSkinInBox(itemStack, displayContext, bakedModel, itemModel, packedLight, overlay, 0, poseStackIn, buffersIn);
                break;
            }
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND: {
                // first person can't support render outline.
                var outlineColor = 0;
                var entity = itemModel.entity();
                if (entity != null && displayContext.isThirdPerson()) {
                    outlineColor = entity.getOutlineColor();
                }

                // in special case, entity hold item type skin.
                // so we need replace it to custom renderer.
                var sourceSlot = itemModel.sourceSlot();
                if (sourceSlot == null) {
                    if (itemModel.shouldRenderInBox()) {
                        counter = _renderEmbeddedSkinInBox(itemStack, displayContext, bakedModel, itemModel, packedLight, overlay, outlineColor, poseStackIn, buffersIn);
                    } else {
                        // use this case:
                        //  YDM's Weapon Master
                        counter = _renderEmbeddedSkin(itemStack, displayContext, bakedModel, itemModel, packedLight, overlay, outlineColor, poseStackIn, buffersIn);
                    }
                    break;
                }
                // the backpack skin can't apply into hand item renderer by the wardrobe,
                // it only rendering in the entity back by third-party mods:
                //   Sophisticated Backpacks
                //   Traveler's Backpack
                if (sourceSlot.skinType() == SkinTypes.ITEM_BACKPACK && sourceSlot.slotType() == EntitySlot.Type.IN_WARDROBE) {
                    return;
                }
                var renderData = EntityRenderData.of(entity);
                if (renderData != null) {
//                    poseStack.translate(0, 1, -2);
//                    RenderUtils.drawPoint(poseStack, null, 2, buffers);
                    var poseStack = AbstractPoseStack.wrap(poseStackIn);
                    var bufferSource = AbstractBufferSource.wrap(buffersIn);
                    var armature = BakedArmature.defaultBy(Armatures.ANY);

                    poseStack.pushPose();
                    poseStack.scale(-SCALE, -SCALE, SCALE);

                    var context = SkinRenderContext.alloc(renderData, packedLight, 0, displayContext);

                    context.setOverlay(OverlayTexture.NO_OVERLAY);
                    context.setLightmap(packedLight);
                    context.setPartialTicks(0);
                    context.setAnimationTicks(TickUtils.animationTicks());

                    context.setPoseStack(poseStack);
                    context.setBufferSource(bufferSource);
                    context.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

                    context.setOutlineColor(outlineColor);

                    context.setItemSource(SkinItemSource.create(800, itemStack, displayContext, itemModel.properties()));
                    context.setUseItemTransforms(true);
                    counter = render(entity, armature, context, Collections.singleton(sourceSlot));
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
        // restore the lighting if we changed.
        if (SkinRenderMode.inGUI() && !bakedModel.usesBlockLight()) {
            Lighting.setupForFlatItems();
        }
        if (counter != 0 && !ModDebugger.itemOverride) {
            callback.cancel();
        }
    }

    private static int _renderEmbeddedSkinInBox(ItemStack itemStack, OpenItemDisplayContext displayContext, BakedModel bakedModel, EmbeddedItemModel itemModel, int packedLight, int overlay, int outlineColor, PoseStack poseStackIn, MultiBufferSource buffersIn) {
        int count = 0;
        var descriptor = itemModel.sourceSkin();
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.INVENTORY);
        if (bakedSkin == null) {
            return count;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var buffers = AbstractBufferSource.wrap(buffersIn);
        var rotation = OpenVector3f.ZERO;
        var scale = OpenVector3f.ONE;

        poseStack.pushPose();

        var itemSource = SkinItemSource.create(descriptor.sharedItemStack());
        itemSource.setScale(scale);
        itemSource.setRotation(rotation);
        itemSource.setDisplayContext(displayContext);

        var scheme = descriptor.paintScheme();
        count = ExtendedItemRenderer.renderSkinInBox(bakedSkin, scheme, 0, packedLight, outlineColor, itemSource, poseStack, buffers);

        poseStack.popPose();

        return count;
    }

    private static int _renderEmbeddedSkin(ItemStack itemStack, OpenItemDisplayContext displayContext, BakedModel bakedModel, EmbeddedItemModel itemModel, int packedLight, int overlay, int outlineColor, PoseStack poseStackIn, MultiBufferSource buffersIn) {
        int count = 0;
        var descriptor = itemModel.sourceSkin();
        var tesselator = SkinRenderTesselator.create(descriptor, Tickets.INVENTORY);
        if (tesselator == null) {
            return count;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var bufferSource = AbstractBufferSource.wrap(buffersIn);

        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        tesselator.setRenderData(EntityRenderData.of(tesselator.mannequin()));

        tesselator.setPartialTicks(0);
        tesselator.setLightmap(packedLight);

        tesselator.setPoseStack(poseStack);
        tesselator.setBufferSource(bufferSource);
        tesselator.setModelViewStack(AbstractPoseStack.create(RenderSystem.getExtendedModelViewStack()));

        tesselator.setColorScheme(descriptor.paintScheme());
        tesselator.setItemSource(SkinItemSource.create(800, itemModel.sourceStack(), displayContext, itemModel.properties()));
        tesselator.setUseItemTransforms(true);
        tesselator.setDisplayBox(null);
        tesselator.setDisplayContext(displayContext);
        tesselator.setOutlineColor(outlineColor);

        count = tesselator.draw();

        poseStack.popPose();
        return count;
    }

    public static void onRenderInventoryEntityPre(LivingEntity entity, int x, int y, int scale, float mouseX, float mouseY) {
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

    public static void onRenderInventoryEntityPost(LivingEntity entity) {
        if (INVENTORY_RENDER_POST_EVENT != null) {
            INVENTORY_RENDER_POST_EVENT.run();
            INVENTORY_RENDER_POST_EVENT = null;
        }
    }

    public static int render(Entity entity, BakedArmature bakedArmature, SkinRenderContext context, Iterable<EntitySlot> entries) {
        int r = 0;
        for (var entry : entries) {
            var bakedSkin = entry.skin();
            var itemSource = context.itemSource();
            var itemStack = itemSource.item();
            if (itemStack.isEmpty()) {
                itemStack = entry.itemStack();
            }
            if (itemSource == SkinItemSource.EMPTY) {
                itemSource = SkinItemSource.create(itemStack);
            }
            itemSource.setItem(itemStack);
            itemSource.setRenderPriority(entry.renderPriority());
            context.setItemSource(itemSource);
            context.setOverlay(entry.getOverrideOverlay(entity));
            bakedSkin.setupAnim(entity, bakedArmature, context);
            var paintScheme = bakedSkin.resolve(entity, entry.paintScheme());
            if (context.isUseItemTransforms()) {
                var itemTransform = bakedSkin.itemTransform();
                itemTransform.apply(context.poseStack(), entity, bakedSkin, context);
            }
            SkinRenderer.render(entity, bakedArmature, bakedSkin, paintScheme, context);
            r += SkinRenderHelper.getRenderCount(bakedSkin);
        }
        return r;
    }

}
