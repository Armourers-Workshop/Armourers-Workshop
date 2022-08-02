package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.DataSerializers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;

public class SeatEntity extends LivingEntity {

    private int holdingTick;
    private BlockPos blockPos;

    public SeatEntity(EntityType<? extends SeatEntity> entityType, Level level) {
        super(entityType, level);
        this.yRot = 0.0f;
        this.yHeadRot = this.yRot;
        this.maxUpStep = 0.0f;
        this.holdingTick = ModConfig.Client.prefersSeatHoldingTick;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.blockPos = DataSerializers.getBlockPos(nbt, Constants.Key.TILE_ENTITY_REFER, BlockPos.ZERO);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        DataSerializers.putBlockPos(nbt, Constants.Key.TILE_ENTITY_REFER, blockPos, BlockPos.ZERO);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level != null && !this.level.isClientSide()) {
            this.autoKill();
        }
    }

    @Override
    public void travel(Vec3 p_213352_1_) {
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
        return level != null && blockPos != null && level.getBlockEntity(blockPos) instanceof SkinnableBlockEntity;
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
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
        return false;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_184582_1_) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slotType, ItemStack itemStack) {
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
