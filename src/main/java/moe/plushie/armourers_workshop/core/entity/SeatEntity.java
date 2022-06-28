package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.tileentity.SkinnableTileEntity;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.utils.AWDataSerializers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@SuppressWarnings("NullableProblems")
public class SeatEntity extends LivingEntity {

    private int holdingTick;
    private BlockPos blockPos;

    public SeatEntity(EntityType<? extends SeatEntity> entityType, World world) {
        super(entityType, world);
        this.yRot = 0.0f;
        this.yHeadRot = this.yRot;
        this.maxUpStep = 0.0f;
        this.holdingTick = ModConfig.Client.prefersSeatHoldingTick;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        this.blockPos = AWDataSerializers.getBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, BlockPos.ZERO);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        AWDataSerializers.putBlockPos(nbt, AWConstants.NBT.TILE_ENTITY_REFER, blockPos, BlockPos.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level != null && !this.level.isClientSide()) {
            this.autoKill();
        }
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

    public void autoKill() {
        if (this.holdingTick > 0) {
            this.holdingTick--;
        }
        if (this.holdingTick <= 0 && this.isAlive() && !this.isWorking()) {
            kill();
        }
    }

    @Override
    protected void removePassenger(Entity entity) {
        super.removePassenger(entity);
        this.holdingTick = ModConfig.Client.prefersSeatHoldingTick;
    }

    public boolean isWorking() {
        if (getPassengers().isEmpty()) {
            return false;
        }
        return level != null && blockPos != null && level.getBlockEntity(blockPos) instanceof SkinnableTileEntity;
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
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
        return false;
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

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}
