package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EntitySlot {

    protected final ItemStack itemStack;
    protected final SkinDescriptor descriptor;
    protected final BakedSkin bakedSkin;
    protected final ColorScheme bakedScheme;
    protected final Type slotType;
    protected final float renderPriority;
    protected final boolean overrideOverlayColor;

    public EntitySlot(ItemStack itemStack, SkinDescriptor descriptor, BakedSkin bakedSkin, ColorScheme entityScheme, float renderPriority, Type slotType) {
        this.itemStack = itemStack;
        this.descriptor = descriptor;
        this.bakedSkin = bakedSkin;
        this.bakedScheme = baking(descriptor.getColorScheme(), entityScheme, slotType);
        this.renderPriority = renderPriority;
        this.slotType = slotType;
        this.overrideOverlayColor = bakedSkin.getProperties().get(SkinProperty.OVERRIDE_OVERLAY_COLOR);
    }

    public static ColorScheme baking(ColorScheme skinScheme, ColorScheme entityScheme, Type slotType) {
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

    public float getRenderPriority() {
        return renderPriority;
    }

    public BakedSkin getBakedSkin() {
        return bakedSkin;
    }

    public ColorScheme getBakedScheme() {
        return bakedScheme;
    }

    public SkinDescriptor getDescriptor() {
        return descriptor;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getOverrideOverlay(Entity entity) {
        // when use overlay color, we only support living entity.
        if (overrideOverlayColor || !(entity instanceof LivingEntity livingEntity)) {
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
