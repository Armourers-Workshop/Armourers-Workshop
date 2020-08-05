package moe.plushie.armourers_workshop.common.tileentities;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.advanced_skin_builder.GuiAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAdvancedSkinBuilder extends ModTileEntity implements IGuiFactory {

    private static final String TAG_TYPE = "skinType";
    private static final int CONS_PARTS_MAX = 10;
    
    private ISkinType skinType;
    private SkinProperties skinProps;

    private ArrayList<AdvancedPartNode> advancedPartNodes = new ArrayList<AdvancedPartNode>();

    public TileEntityAdvancedSkinBuilder() {
        super();
    }

    public ArrayList<AdvancedPartNode> getAdvancedPartNodes() {
        return advancedPartNodes;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 5, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = super.getUpdateTag();
        //writeCommonToNBT(compound);
        return compound;
    }
    
    public Packet getDescriptionPacket() {
        return getUpdatePacket();
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        //readCommonFromNBT(compound);
        syncWithClients();
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        //readCommonFromNBT(compound);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        //writeCommonToNBT(compound);
        return compound;
    }
    


    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerAdvancedSkinBuilder(player.inventory, this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiAdvancedSkinBuilder(player, this);
    }
}
