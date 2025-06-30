package moe.plushie.armourers_workshop.core.entity;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.compatibility.core.AbstractLivingEntity;
import moe.plushie.armourers_workshop.core.blockentity.SkinnableBlockEntity;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;

public class SeatEntity extends AbstractLivingEntity implements IDataSerializable.Mutable {

    private int holdingTick;
    private BlockPos blockPos = BlockPos.ZERO;

    public SeatEntity(EntityType<? extends SeatEntity> entityType, Level level) {
        super(entityType, level);
        this.setInvisible(true);
        this.setInvulnerable(true);
        this.setYBodyRot(0.0f);
        //this.maxUpStep = 0.0f;
        this.holdingTick = ModConfig.Client.prefersSeatHoldingTick;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.deserialize(new TagSerializer(tag));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.serialize(new TagSerializer(tag));
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.REFER, blockPos);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        blockPos = serializer.read(CodingKeys.REFER);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getLevel().isClientSide()) {
            this.autoKill();
        }
    }

    @Override
    public void travel(Vec3 local) {
        if (isAlive() && !getPassengers().isEmpty()) {
            var passenger = getPassengers().get(0);
            this.setYBodyRot(passenger.getYRot());
        }
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
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
        return getLevel() != null && blockPos != null && getLevel().getBlockEntity(blockPos) instanceof SkinnableBlockEntity;
    }


    @Override
    public void setYBodyRot(float f) {
        super.setYBodyRot(f);
        this.setYRot(f);
        this.yRotO = f;
        this.yHeadRot = f;
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

    // REMOVE FROM 1.21
//    @Override
//    public double getPassengersRidingOffset() {
//        return -0.15f;
//    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
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


    private static class CodingKeys {

        public static final IDataSerializerKey<BlockPos> REFER = IDataSerializerKey.create("Refer", IDataCodec.BLOCK_POS, BlockPos.ZERO);
    }
}
