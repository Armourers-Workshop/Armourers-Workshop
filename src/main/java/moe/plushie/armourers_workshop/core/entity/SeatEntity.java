package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@SuppressWarnings("NullableProblems")
public class SeatEntity extends LivingEntity {

    private int holdingTick = 0;
    private BlockPos blockPos;

    public SeatEntity(EntityType<? extends SeatEntity> entityType, World world) {
        super(entityType, world);
        this.yRot = 0.0f;
        this.yHeadRot = this.yRot;
        this.maxUpStep = 0.0f;
    }

    @Override
    public void tick() {
        if (this.holdingTick > 0) {
            this.holdingTick--;
        }
        if (this.holdingTick <= 0 && this.isAlive() && !this.isWorking()) {
            remove();
            return;
        }
        super.tick();
    }

    @Override
    public void travel(Vector3d p_213352_1_) {
        if (isAlive() && !getPassengers().isEmpty()) {
            Entity passenger = getPassengers().get(0);
            this.yRot = passenger.yRot;
            this.yRotO = this.yRot;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yBodyRot;
        }
    }

    @Override
    public void kill() {
        this.remove();
    }

    @Override
    protected void removePassenger(Entity p_184225_1_) {
        super.removePassenger(p_184225_1_);
        this.holdingTick = 60;
    }

    public boolean isWorking() {
        if (getPassengers().isEmpty()) {
            return false;
        }
        return level != null && level.getBlockEntity(blockPos) instanceof SkinnableTileEntity;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public double getPassengersRidingOffset() {
        return -0.15f;
    }


    @Override
    public HandSide getMainArm() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
        return true;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType p_184582_1_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlotType slotType, ItemStack itemStack) {
    }

    @Override
    public void setPosRaw(double x, double y, double z) {
        super.setPosRaw(x, y, z);
        int i = MathHelper.floor(x - 0.5f);
        int j = MathHelper.floor(y - 0.5f);
        int k = MathHelper.floor(z - 0.5f);
        if (blockPos == null || i != blockPos.getX() || j != blockPos.getY() || k != blockPos.getZ()) {
            blockPos = new BlockPos(i, j, k);
        }
    }
}
