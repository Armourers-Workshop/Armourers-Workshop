package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings.MovementType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAdvancedSkinBuilder extends AbstractTileEntityInventory {

    private static final int CONS_PARTS_MAX = 10;

    private SkinPartSettings[] partSettings;

    public TileEntityAdvancedSkinBuilder() {
        super(CONS_PARTS_MAX);
        partSettings = new SkinPartSettings[CONS_PARTS_MAX];
        for (int i = 0; i < CONS_PARTS_MAX; i++) {
            partSettings[i] = new SkinPartSettings();
        }
    }
    
    public SkinPartSettings getPartSettings(int index) {
        return partSettings[index];
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
        dirtySync();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public static class SkinPartSettings {

        public int parentIndex = -1;
        public Vec3d posOffset = Vec3d.ZERO;
        public Vec3d rotOffset = Vec3d.ZERO;
        public boolean mirror = false;
        public float moveAngleMin = 0F;
        public float moveAngleMax = 0F;
        public int moveSpeed = 0;
        public MovementType moveType = MovementType.LINEAR;
    }
}
