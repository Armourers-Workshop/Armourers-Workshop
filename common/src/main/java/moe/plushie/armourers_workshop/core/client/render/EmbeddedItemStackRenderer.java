package moe.plushie.armourers_workshop.core.client.render;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.core.client.animation.AnimationManager;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.other.DiscoveerableSkinManager;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntitySlot;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.render.model.EmbeddedItemModel;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.ItemStackRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.MannequinRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.SkinRenderState;
import moe.plushie.armourers_workshop.core.data.ticket.TicketManager;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public class EmbeddedItemStackRenderer {

    @Nullable
    public static EmbeddedItemModel extract(ItemStack itemStack, ItemStackRenderState itemRenderState, @Nullable Entity itemOwner) {
        // when the wardrobe has override skin of the item,
        // we easily got a conclusion of the needs embedded skin.
        if (!itemRenderState.shouldRenderInGUI()) {
            var renderData = EntityRenderData.of(itemOwner);
            if (renderData != null) {
                var entries = renderData.getItemSkins(itemStack, itemOwner instanceof MannequinEntity);
                for (var entry : entries) {
                    return EmbeddedItemModel.fromWardrobe(entry, itemRenderState);
                }
            }
        }
        // Try to get skin descriptor from item stack.
        var descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            // Try to get skin descriptor from item model config.
            descriptor = DiscoveerableSkinManager.getInstance().get(itemRenderState.itemModel());
            if (!descriptor.isEmpty()) {
                return EmbeddedItemModel.fromComponent(descriptor, itemRenderState);
            }
            return null;
        }
        // when the item is a skin item itself,
        // we easily got a conclusion of the needs embedded skin.
        if (itemStack.is(ModItems.SKIN.get())) {
            return EmbeddedItemModel.fromSelf(descriptor, itemRenderState);
        }
        // we allow server manually control the item whether to use the embedded renderer.
        if (descriptor.options().embeddedItemRenderer() != 0) {
            if (descriptor.options().embeddedItemRenderer() == 2) {
                return EmbeddedItemModel.fromComponent(descriptor, itemRenderState);
            }
            return null;
        }
        // when the skin item, we no required enable of embed skin option in the config.
        if (ModConfig.enableEmbeddedSkinRenderer() || descriptor.type() == SkinTypes.ITEM) {
            return EmbeddedItemModel.fromComponent(descriptor, itemRenderState);
        }
        return null;
    }

    public static int render(EmbeddedItemModel itemModel, int lightmap, int overlay, OpenItemDisplayContext displayContext, IGraphicsContext context) {
        switch (displayContext) {
            case GUI:
            case GROUND:
            case FIXED: {
                return renderInBox(itemModel, lightmap, overlay, 0, displayContext, context);
            }
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND: {
                // the first person can't support render outline.
                var outlineColor = 0;
                var entityRenderState = itemModel.entityRenderState();
                if (entityRenderState != null && displayContext.isThirdPerson()) {
                    outlineColor = entityRenderState.outlineColor();
                }

                // in special case, entity hold item type skin.
                // so we need replace it to custom renderer.
                var entitySlot = itemModel.slot();
                if (entitySlot == null) {
                    //
                    if (itemModel.shouldRenderInBox()) {
                        return renderInBox(itemModel, lightmap, overlay, outlineColor, displayContext, context);
                    }
                    // use this case:
                    //  YDM's Weapon Master
                    return renderInOther(itemModel, lightmap, overlay, outlineColor, displayContext, context);
                }

                // the backpack skin can't apply into hand item renderer by the wardrobe,
                // it's only rendering in the entity back by third-party mods:
                //   Sophisticated Backpacks
                //   Traveler's Backpack
                if (entitySlot.type() == SkinTypes.ITEM_BACKPACK && entitySlot.source() == EntitySlot.Source.IN_WARDROBE) {
                    return 0;
                }

                // we can't found the rendering entity, why?
                if (entityRenderState == null) {
                    entityRenderState = MannequinRenderState.getPlaceholder();
                }

                return renderInHand(itemModel, entityRenderState, lightmap, overlay, outlineColor, displayContext, context);
            }
        }
        return 0;
    }

    private static int renderInBox(EmbeddedItemModel itemModel, int lightmap, int overlay, int outlineColor, OpenItemDisplayContext displayContext, IGraphicsContext context) {
        // configure the item source.
        var itemSource = SkinItemSource.create(itemModel.skin().sharedItemStack());
        itemSource.setScale(OpenVector3f.ONE);
        itemSource.setRotation(OpenVector3f.ZERO);
        itemSource.setDisplayBox(OpenRectangle3f.ONE);
        itemSource.setDisplayContext(displayContext);
        itemSource.setRenderPriority(0);
        itemSource.setItemModelResolver(itemModel.renderState().itemModelResolver());
        // configure the skin render state.
        var model = loadSkin(itemModel.skin(), itemSource);
        model.setPartialTicks(1.0f);
        model.setAnimationTicks(TickUtils.animationTicks());
        model.setAnimationManager(AnimationManager.NONE);
        model.setItemSource(itemSource);
        model.setOutlineColor(outlineColor);
        return render(model, null, lightmap, overlay, context);
    }

    private static int renderInOther(EmbeddedItemModel itemModel, int lightmap, int overlay, int outlineColor, OpenItemDisplayContext displayContext, IGraphicsContext context) {
        // configure the item source.
        var itemSource = SkinItemSource.create(itemModel.renderState().itemStack());
        itemSource.setProperties(itemModel.properties());
        itemSource.setDisplayContext(displayContext);
        itemSource.setDisplayBox(null);
        itemSource.setRenderPriority(800);
        itemSource.setItemModelResolver(itemModel.renderState().itemModelResolver());
        // configure the skin render state.
        var model = loadSkin(itemModel.skin(), itemSource);
        model.setPartialTicks(1.0f);
        model.setAnimationTicks(TickUtils.animationTicks());
        model.setAnimationManager(AnimationManager.NONE);
        model.setItemSource(itemSource);
        model.setOutlineColor(outlineColor);
        return render(model, null, lightmap, overlay, context);
    }

    private static int renderInHand(EmbeddedItemModel itemModel, EntityRenderState entityRenderState, int lightmap, int overlay, int outlineColor, OpenItemDisplayContext displayContext, IGraphicsContext context) {
        // configure the item source.
        var itemSource = SkinItemSource.create(itemModel.renderState().itemStack());
        itemSource.setProperties(itemModel.properties());
        itemSource.setDisplayContext(displayContext);
        itemSource.setDisplayBox(null);
        itemSource.setRenderPriority(800);
        itemSource.setItemModelResolver(itemModel.renderState().itemModelResolver());
        // configure the skin render state.
        var model = loadSkin(itemModel.skin(), itemSource);
        model.setPartialTicks(entityRenderState.partialTicks());
        model.setAnimationTicks(entityRenderState.animationTicks());
        model.setAnimationManager(entityRenderState.animationManager());
        model.setItemSource(itemSource);
        model.setOutlineColor(outlineColor);
        return render(model, entityRenderState, lightmap, overlay, context);
    }

    private static int render(SkinRenderState model, @Nullable EntityRenderState renderState, int lightmap, int overlay, IGraphicsContext context) {
        if (model.isEmpty()) {
            return 0;
        }
        context.saveGraphicsState();
        context.scaleCTM(-0.0625f, -0.0625f, 0.0625f);

        var count = model.render(renderState, null, lightmap, overlay, context);

        context.restoreGraphicsState();

        return count;
    }

    private static SkinRenderState loadSkin(SkinDescriptor descriptor, SkinItemSource itemSource) {
        var slots = new SkinRenderState();
        var skin = SkinBakery.getInstance().loadSkin(TicketManager.INVENTORY.get(descriptor));
        if (skin != null) {
            var slot = new EntitySlot(skin, SkinPaintScheme.EMPTY, itemSource.itemStack(), descriptor);
            slots.prepare(Collections.newList(slot));
        }
        return slots;
    }
}
