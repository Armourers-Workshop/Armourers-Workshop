package moe.plushie.armourers_workshop.common.init.entities;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.common.init.blocks.BlockSkinnable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySeat extends Entity implements IEntityAdditionalSpawnData {

    private int noRiderTime = 0;
    private Point3D offset;
    private EnumFacing rotation;

    public EntitySeat(World world, BlockPos pos, Point3D offset, EnumFacing rotation) {
        super(world);
        setPosition(pos.getX(), pos.getY(), pos.getZ());
        setSize(0F, 0F);
        this.offset = offset;
        this.rotation = rotation.getOpposite();
    }

    public EntitySeat(World world) {
        super(world);
        setSize(0F, 0F);
        this.offset = new Point3D(0, 0, 0);
        this.rotation = EnumFacing.EAST;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float scale = 0.0625F;

            float offsetX = (offset.getX() * scale) * rotation.getZOffset() + (-offset.getZ() * scale) * rotation.getXOffset();
            float offsetY = offset.getY() * scale;
            float offsetZ = (-offset.getZ() * scale) * rotation.getZOffset() + (-offset.getX() * scale) * rotation.getXOffset();

            passenger.setPosition(this.posX + 0.5 - offsetX, this.posY + passenger.getYOffset() + 0.5F - offsetY, this.posZ + 0.5F - offsetZ);

            if (passenger.isSneaking()) {
                passenger.setPosition(posX + 0.5F, posY + 2, posZ + 0.5F);
                setDead();
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        IBlockState state = world.getBlockState(getPosition());
        if (!(state.getBlock() instanceof BlockSkinnable)) {
            setDead();
            return;
        }

        if (getPassengers().size() == 0) {
            noRiderTime++;
            if (noRiderTime > 1) {
                setDead();
            }
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (player.getRidingEntity() != this) {
            super.onCollideWithPlayer(player);
        }
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(offset.getX());
        buf.writeInt(offset.getY());
        buf.writeInt(offset.getZ());
        buf.writeInt(rotation.ordinal());
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        offset = new Point3D(buf.readInt(), buf.readInt(), buf.readInt());
        rotation = EnumFacing.values()[buf.readInt()];
    }
}
