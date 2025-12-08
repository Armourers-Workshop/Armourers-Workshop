package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class EntitySlot {

    protected final ItemStack itemStack;
    protected final SkinDescriptor descriptor;
    protected final BakedSkin skin;
    protected final SkinPaintScheme paintScheme;
    protected final Source source;
    protected final float renderPriority;
    protected final boolean useOverlayColor;

    public EntitySlot(BakedSkin skin, SkinPaintScheme entityScheme, ItemStack itemStack, SkinDescriptor descriptor) {
        this(skin, entityScheme, itemStack, descriptor, 0, Source.UNKNOWN);
    }

    public EntitySlot(BakedSkin skin, SkinPaintScheme entityScheme, ItemStack itemStack, SkinDescriptor descriptor, float renderPriority, Source source) {
        this.itemStack = itemStack;
        this.descriptor = descriptor;
        this.skin = skin;
        this.paintScheme = baking(descriptor.paintScheme(), entityScheme, source);
        this.renderPriority = renderPriority;
        this.source = source;
        this.useOverlayColor = skin.properties().get(SkinProperty.USE_OVERLAY_COLOR);
    }

    private static SkinPaintScheme baking(SkinPaintScheme skinScheme, SkinPaintScheme entityScheme, Source slotType) {
        // when player held item we can't use the entity scheme.
        if (slotType == Source.UNKNOWN || slotType == Source.IN_HELD) {
            return skinScheme;
        }
        if (skinScheme.isEmpty()) {
            return entityScheme;
        }
        if (entityScheme.isEmpty()) {
            return skinScheme;
        }
        var bakedScheme = skinScheme.copy();
        bakedScheme.setReference(entityScheme);
        return bakedScheme;
    }

    public int resolveOverlay(@Nullable EntityRenderState renderState, int overlay) {
        // we only support keep living entity overlay color.
        if (!useOverlayColor || !(renderState instanceof LivingEntityRenderState renderState1)) {
            return overlay;
        }
        var bl = renderState1.hurtTime() > 0 || renderState1.deathTime() > 0;
        return OverlayTexture.pack(0.0f, bl);
    }

    public int resolveLightmap(@Nullable EntityRenderState renderState, int lightmap) {
        return lightmap;
    }

    public SkinItemSource resolveItemSource(@Nullable EntityRenderState renderState, @Nullable SkinItemSource itemSource) {
        //
        if (itemSource == null || itemSource == SkinItemSource.EMPTY) {
            var newItemSource = SkinItemSource.create(itemStack);
            newItemSource.setRenderPriority(renderPriority);
            return newItemSource;
        }
        var resolvedItemStack = itemSource.itemStack();
        if (resolvedItemStack.isEmpty()) {
            resolvedItemStack = itemStack;
        }
        itemSource.setItemStack(resolvedItemStack);
        itemSource.setRenderPriority(renderPriority);
        return itemSource;
    }

    public boolean shouldRenderInHeld(ItemStack itemStack) {
        return descriptor.accept(itemStack);
    }

    public float renderPriority() {
        return renderPriority;
    }

    public BakedSkin skin() {
        return skin;
    }

    public SkinType type() {
        return skin.type();
    }

    public Source source() {
        return source;
    }

    public SkinLightSource lightSource() {
        return skin.renderInfo().lightSource();
    }

    public SkinPaintScheme paintScheme() {
        return paintScheme;
    }

    public SkinDescriptor descriptor() {
        return descriptor;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public enum Source {
        UNKNOWN, IN_HELD, IN_EQUIPMENT, IN_WARDROBE, IN_CONTAINER,
    }
}
