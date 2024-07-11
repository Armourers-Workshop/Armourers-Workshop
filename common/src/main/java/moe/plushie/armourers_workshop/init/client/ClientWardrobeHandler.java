package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedFirstPersonArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedItemModel;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.FallbackEntityRenderPatch;
import moe.plushie.armourers_workshop.core.client.skinrender.patch.LivingEntityRenderPatch;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.utils.EmbeddedSkinStack;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.function.Supplier;

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

    public static void onRenderSpecificHand(LivingEntity entity, float partialTicks, int packedLight, AbstractItemTransformType transformType, PoseStack poseStackIn, MultiBufferSource buffersIn, Runnable cancelHandler) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var buffers = AbstractBufferSource.wrap(buffersIn);
        var armature = BakedFirstPersonArmature.defaultBy(transformType);
        var context = SkinRenderContext.alloc(renderData, packedLight, partialTicks, transformType, poseStack, buffers);

        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        int count = render(entity, armature, context, renderData::getArmorSkins);
        if (count != 0 && !ModDebugger.handOverride) {
            cancelHandler.run();
        }
        context.release();

        poseStack.popPose();
    }


    public static void onRenderEntityPre(Entity entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffersIn, int packedLight) {
        FallbackEntityRenderPatch.activate(entity, partialTicks, packedLight, poseStackIn, buffersIn, null);
    }

    public static void onRenderEntity(Entity entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffersIn, int packedLight) {
        FallbackEntityRenderPatch.apply(entity, null);
    }

    public static void onRenderEntityPost(Entity entity, float partialTicks, PoseStack poseStackIn, MultiBufferSource buffersIn, int packedLight) {
        FallbackEntityRenderPatch.deactivate(entity, null);
    }


    public static void onRenderLivingEntityPre(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer) {
        LivingEntityRenderPatch.activate(entity, partialTicks, packedLight, poseStackIn, buffersIn, entityRenderer, null);
    }

    public static void onRenderLivingEntity(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer) {
        LivingEntityRenderPatch.apply(entity, null);
    }

    public static void onRenderLivingEntityPost(LivingEntity entity, float partialTicks, int packedLight, PoseStack poseStackIn, MultiBufferSource buffersIn, LivingEntityRenderer<?, ?> entityRenderer) {
        LivingEntityRenderPatch.deactivate(entity, null);
    }

    @Nullable
    public static EmbeddedSkinStack getEmbeddedSkinStack(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, AbstractItemTransformType transformType) {
        if (RENDERING_GUI_ITEM != itemStack) {
            // when the wardrobe has override skin of the item,
            // we easily got a conclusion of the needs embedded skin.
            var renderData = EntityRenderData.of(entity);
            if (renderData != null) {
                for (var entry : renderData.getItemSkins(itemStack, entity instanceof MannequinEntity)) {
                    return new EmbeddedSkinStack(0, entry);
                }
            }
        }
        var descriptor = SkinDescriptor.of(itemStack);
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

    public static void renderEmbeddedSkin(@Nullable LivingEntity entity, @Nullable Level level, ItemStack itemStack, EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStackIn, MultiBufferSource buffersIn, BakedModel bakedModel, int packedLight, int overlay, CallbackInfo callback) {
        int counter = 0;
        switch (transformType) {
            case GUI:
            case GROUND:
            case FIXED: {
                counter = _renderEmbeddedSkinInBox(embeddedStack, transformType, leftHandHackery, poseStackIn, buffersIn, packedLight, overlay);
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
                        counter = _renderEmbeddedSkinInBox(embeddedStack, transformType, leftHandHackery, poseStackIn, buffersIn, packedLight, overlay);
                    } else {
                        // use this case:
                        //  YDM's Weapon Master
                        counter = _renderEmbeddedSkin(embeddedStack, transformType, leftHandHackery, poseStackIn, buffersIn, packedLight, overlay);
                    }
                    break;
                }
                var renderData = EntityRenderData.of(entity);
                if (renderData != null) {
//                    poseStack.translate(0, 1, -2);
//                    RenderUtils.drawPoint(poseStack, null, 2, buffers);
                    var poseStack = AbstractPoseStack.wrap(poseStackIn);
                    var buffers = AbstractBufferSource.wrap(buffersIn);
                    var armature = BakedArmature.defaultBy(Armatures.ANY);
                    var context = SkinRenderContext.alloc(renderData, packedLight, 0, transformType, poseStack, buffers);

                    poseStack.pushPose();
                    poseStack.scale(-SCALE, -SCALE, SCALE);

                    context.setItemSource(SkinItemSource.create(800, itemStack, transformType));
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

    private static int _renderEmbeddedSkinInBox(EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStackIn, MultiBufferSource buffersIn, int packedLight, int overlay) {
        int count = 0;
        var descriptor = embeddedStack.getDescriptor();
        var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, Tickets.INVENTORY);
        if (bakedSkin == null) {
            return count;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var buffers = AbstractBufferSource.wrap(buffersIn);
        var rotation = Vector3f.ZERO;
        var scale = Vector3f.ONE;

        poseStack.pushPose();

        // for skin of the custom item transforms, we will respect its options.
        if (bakedSkin.getItemModel() == null) {
            var itemModel = BakedItemModel.DEFAULT;
            var transform = itemModel.getTransform(transformType);

            // when skin not specify item transforms,
            // we need to apply a default item transforms.
            itemModel.applyTransform(poseStack, leftHandHackery, transformType);

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

    private static int _renderEmbeddedSkin(EmbeddedSkinStack embeddedStack, AbstractItemTransformType transformType, boolean leftHandHackery, PoseStack poseStackIn, MultiBufferSource buffersIn, int packedLight, int overlay) {
        int count = 0;
        var descriptor = embeddedStack.getDescriptor();
        var context = SkinRenderTesselator.create(descriptor, Tickets.INVENTORY);
        if (context == null) {
            return count;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var buffers = AbstractBufferSource.wrap(buffersIn);

        poseStack.pushPose();
        poseStack.scale(-SCALE, -SCALE, SCALE);

        context.setRenderData(EntityRenderData.of(context.getMannequin()));
        context.setLightmap(packedLight);
        context.setPartialTicks(0);
        context.setItemSource(SkinItemSource.create(800, embeddedStack.getItemStack(), transformType));
        context.setColorScheme(descriptor.getColorScheme());

        count = context.draw(poseStack, buffers);

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

    public static int render(Entity entity, BakedArmature bakedArmature, SkinRenderContext context, Supplier<Iterable<EntitySlot>> provider) {
        int r = 0;
        for (var entry : provider.get()) {
            var bakedSkin = entry.getBakedSkin();
            var itemSource = context.getItemSource();
            var itemStack = itemSource.getItem();
            if (itemStack.isEmpty()) {
                itemStack = entry.getItemStack();
            }
            if (itemSource == SkinItemSource.EMPTY) {
                itemSource = SkinItemSource.create(itemStack);
            }
            itemSource.setItem(itemStack);
            itemSource.setRenderPriority(entry.getRenderPriority());
            context.setItemSource(itemSource);
            context.setOverlay(entry.getOverrideOverlay(entity));
            bakedSkin.setupAnim(entity, bakedArmature, context);
            var colorScheme = bakedSkin.resolve(entity, entry.getBakedScheme());
            SkinRenderer.render(entity, bakedArmature, bakedSkin, colorScheme, context);
            r += SkinRenderHelper.getRenderCount(bakedSkin);
        }
        return r;
    }

    private static boolean shouldRenderInBox(EmbeddedSkinStack embeddedStack) {
        // for the item required render to box.
        if (embeddedStack.getMode() == 2) {
            return true;
        }
        var skinType = embeddedStack.getDescriptor().getType();
        if (skinType == SkinTypes.ITEM_BOAT || skinType == SkinTypes.ITEM_FISHING || skinType == SkinTypes.HORSE) {
            return true;
        }
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
}
