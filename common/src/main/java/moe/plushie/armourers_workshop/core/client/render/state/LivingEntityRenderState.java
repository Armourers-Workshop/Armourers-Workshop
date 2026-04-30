package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.state.ILivingEntityRenderState;
import moe.plushie.armourers_workshop.compat.api.entity.LivingEntityAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class LivingEntityRenderState extends EntityRenderState implements ILivingEntityRenderState {

    protected float scale;

    protected float hurtTime;

    protected float deathTime;

    protected ItemStack usingItem;

    protected int ticksUsingItem;

    protected AABB boundingBox;

    public ItemStack usingItem() {
        return usingItem;
    }

    public int ticksUsingItem() {
        return ticksUsingItem;
    }

    public float scale() {
        return scale;
    }

    public float hurtTime() {
        return hurtTime;
    }

    public float deathTime() {
        return deathTime;
    }

    public AABB boundingBox() {
        return boundingBox;
    }

    public static void extract(LivingEntityAccessor entity, LivingEntityRenderState renderState) {
        renderState.scale = entity.aw2$scale();
        renderState.hurtTime = entity.aw2$hurtTime();
        renderState.deathTime = entity.aw2$deathTime();
        renderState.usingItem = entity.aw2$getUseItem();
        renderState.ticksUsingItem = entity.aw2$getTicksUsingItem();
        renderState.boundingBox = entity.aw2$getBoundingBox();
        renderState.isFlying = entity.aw2$isFallFlying();
        renderState.isFallFlying = entity.aw2$isFallFlying();
        renderState.isBaby = entity.aw2$isBaby();
    }
}
