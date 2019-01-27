package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.client.gui.GuiAdvancedSkinPart;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinPart;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAdvancedSkinPart extends AbstractTileEntityInventory implements IGuiFactory {

    public TileEntityAdvancedSkinPart() {
        super(1);
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
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerAdvancedSkinPart(player.inventory, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiAdvancedSkinPart(player, this);
    }
}
