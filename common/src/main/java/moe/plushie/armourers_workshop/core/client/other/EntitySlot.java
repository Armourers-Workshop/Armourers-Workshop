package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EntitySlot {

    protected final ItemStack itemStack;
    protected final SkinDescriptor descriptor;
    protected final BakedSkin bakedSkin;
    protected final SkinPaintScheme bakedScheme;
    protected final Type slotType;
    protected final float renderPriority;
    protected final boolean useOverlayColor;

    public EntitySlot(ItemStack itemStack, SkinDescriptor descriptor, BakedSkin bakedSkin, SkinPaintScheme entityScheme, float renderPriority, Type slotType) {
        this.itemStack = itemStack;
        this.descriptor = descriptor;
        this.bakedSkin = bakedSkin;
        this.bakedScheme = baking(descriptor.paintScheme(), entityScheme, slotType);
        this.renderPriority = renderPriority;
        this.slotType = slotType;
        this.useOverlayColor = bakedSkin.properties().get(SkinProperty.USE_OVERLAY_COLOR);
    }

    public static SkinPaintScheme baking(SkinPaintScheme skinScheme, SkinPaintScheme entityScheme, Type slotType) {
        // when player held item we can't use the entity scheme.
        if (slotType == Type.IN_HELD) {
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

    public boolean shouldRenderInHeld(ItemStack itemStack) {
        return descriptor.accept(itemStack);
    }

    public float renderPriority() {
        return renderPriority;
    }

    public BakedSkin skin() {
        return bakedSkin;
    }

    public SkinType skinType() {
        return bakedSkin.type();
    }

    public Type slotType() {
        return slotType;
    }

    public SkinPaintScheme paintScheme() {
        return bakedScheme;
    }

    public SkinDescriptor descriptor() {
        return descriptor;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public int getOverrideOverlay(Entity entity) {
        // we only support keep living entity overlay color.
        if (!useOverlayColor || !(entity instanceof LivingEntity livingEntity)) {
            return OverlayTexture.NO_OVERLAY;
        }
        int u = OverlayTexture.u(0.0F);
        int v = OverlayTexture.v(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0);
        return OverlayTexture.pack(u, v);
    }

    public enum Type {
        IN_HELD, IN_EQUIPMENT, IN_WARDROBE, IN_CONTAINER
    }
}
