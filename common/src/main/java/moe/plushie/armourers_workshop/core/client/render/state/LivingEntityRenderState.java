package moe.plushie.armourers_workshop.core.client.render.state;

import moe.plushie.armourers_workshop.api.client.state.ILivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
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

    public static void extract(LivingEntity entity, LivingEntityRenderState renderState) {
        renderState.scale = entity.getScale();
        renderState.hurtTime = entity.hurtTime;
        renderState.deathTime = entity.deathTime;
        renderState.usingItem = entity.getUseItem();
        renderState.ticksUsingItem = entity.getTicksUsingItem();
        renderState.boundingBox = entity.getBoundingBox();
        renderState.isFlying = entity.isFallFlying();
        renderState.isFallFlying = entity.isFallFlying();
        renderState.isBaby = entity.isBaby();
    }
}
