package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.client.gui.hologramprojector.GuiHologramProjector;
import moe.plushie.armourers_workshop.common.inventory.ContainerHologramProjector;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.property.TileProperty;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityHologramProjector extends AbstractTileEntityInventory implements IGuiFactory {

    private static final int INVENTORY_SIZE = 1;

    private final TileProperty<Integer> offsetX = new TileProperty<Integer>(this, "offset_x", Integer.class, 0);
    private final TileProperty<Integer> offsetY = new TileProperty<Integer>(this, "offset_y", Integer.class, 16);
    private final TileProperty<Integer> offsetZ = new TileProperty<Integer>(this, "offset_z", Integer.class, 0);

    private final TileProperty<Integer> angleX = new TileProperty<Integer>(this, "angle_x", Integer.class, 0);
    private final TileProperty<Integer> angleY = new TileProperty<Integer>(this, "angle_y", Integer.class, 0);
    private final TileProperty<Integer> angleZ = new TileProperty<Integer>(this, "angle_z", Integer.class, 0);

    private final TileProperty<Integer> rotationOffsetX = new TileProperty<Integer>(this, "rotation_offset_x", Integer.class, 0);
    private final TileProperty<Integer> rotationOffsetY = new TileProperty<Integer>(this, "rotation_offset_y", Integer.class, 0);
    private final TileProperty<Integer> rotationOffsetZ = new TileProperty<Integer>(this, "rotation_offset_z", Integer.class, 0);

    private final TileProperty<Integer> rotationSpeedX = new TileProperty<Integer>(this, "rotation_speed_x", Integer.class, 0);
    private final TileProperty<Integer> rotationSpeedY = new TileProperty<Integer>(this, "rotation_speed_y", Integer.class, 0);
    private final TileProperty<Integer> rotationSpeedZ = new TileProperty<Integer>(this, "rotation_speed_z", Integer.class, 0);

    private final TileProperty<Boolean> glowing = new TileProperty<Boolean>(this, "glowing", Boolean.class, true);
    private final TileProperty<PowerMode> powerMode = new TileProperty<PowerMode>(this, "power_mode", PowerMode.class, PowerMode.IGNORED);
    private final TileProperty<Boolean> powered = new TileProperty<Boolean>(this, "powered", Boolean.class, false);

    private static boolean showRotationPoint;

    public TileEntityHologramProjector() {
        super(INVENTORY_SIZE);
    }

    public TileProperty<Integer> getOffsetX() {
        return offsetX;
    }

    public TileProperty<Integer> getOffsetY() {
        return offsetY;
    }

    public TileProperty<Integer> getOffsetZ() {
        return offsetZ;
    }

    public TileProperty<Integer> getAngleX() {
        return angleX;
    }

    public TileProperty<Integer> getAngleY() {
        return angleY;
    }

    public TileProperty<Integer> getAngleZ() {
        return angleZ;
    }

    public TileProperty<Integer> getRotationOffsetX() {
        return rotationOffsetX;
    }

    public TileProperty<Integer> getRotationOffsetY() {
        return rotationOffsetY;
    }

    public TileProperty<Integer> getRotationOffsetZ() {
        return rotationOffsetZ;
    }

    public TileProperty<Integer> getRotationSpeedX() {
        return rotationSpeedX;
    }

    public TileProperty<Integer> getRotationSpeedY() {
        return rotationSpeedY;
    }

    public TileProperty<Integer> getRotationSpeedZ() {
        return rotationSpeedZ;
    }

    public TileProperty<Boolean> getGlowing() {
        return glowing;
    }

    public TileProperty<PowerMode> getPowerMode() {
        return powerMode;
    }

    public TileProperty<Boolean> getPowered() {
        return powered;
    }

    @Override
    public String getName() {
        return LibBlockNames.HOLOGRAM_PROJECTOR;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public boolean isShowRotationPoint() {
        return showRotationPoint;
    }

    public void setShowRotationPoint(boolean checked) {
        showRotationPoint = checked;
    }

    public void updatePoweredState() {
        if (getWorld() != null && !world.isRemote) {
            setPoweredState(getWorld().getStrongPower(getPos()) > 0);
        }
    }

    public void setPoweredState(boolean powered) {
        if (this.powered.get() != powered) {
            this.powered.set(powered);
            dirtySync();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = new AxisAlignedBB(-2, -2, -2, 3, 3, 3);
        EnumFacing dir = EnumFacing.byIndex(getBlockMetadata());
        bb = bb.offset(getPos());

        float scale = 0.0625F;

        switch (dir) {
        case UP:
            bb.offset(offsetX.get() * scale, offsetY.get() * scale, offsetZ.get() * scale);
            break;
        case DOWN:
            bb.offset(-offsetX.get() * scale, -offsetY.get() * scale, offsetZ.get() * scale);
            break;
        case EAST:
            bb.offset(offsetY.get() * scale, -offsetX.get() * scale, offsetZ.get() * scale);
            break;
        case WEST:
            bb.offset(-offsetY.get() * scale, offsetX.get() * scale, offsetZ.get() * scale);
            break;
        case NORTH:
            bb.offset(-offsetX.get() * scale, -offsetZ.get() * scale, -offsetY.get() * scale);
            break;
        case SOUTH:
            bb.offset(-offsetX.get() * scale, offsetZ.get() * scale, offsetY.get() * scale);
            break;
        default:
            break;
        }

        return bb;
    }

    public static enum PowerMode implements IStringSerializable {
        IGNORED, HIGH, LOW;

        @Override
        public String getName() {
            return name();
        }
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerHologramProjector(player.inventory, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiHologramProjector(player.inventory, this);
    }
}
