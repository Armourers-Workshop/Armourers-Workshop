package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityOutfitMaker extends AbstractTileEntityInventory {

    private static final String TAG_OUTFIT_NAME = "outfitName";
    private static final String TAG_OUTFIT_FLAVOUR = "outfitFlavour";

    public static final int OUTFIT_SKINS = 5;
    public static final int OUTFIT_ROWS = 4;
    private static final int INVENTORY_SIZE = OUTFIT_SKINS * OUTFIT_ROWS + 2;

    private String outfitName = "";
    private String outfitFlavour = "";

    public TileEntityOutfitMaker() {
        super(INVENTORY_SIZE);
    }

    public String getOutfitName() {
        return outfitName;
    }

    public void setOutfitName(String outfitName) {
        this.outfitName = outfitName;
        dirtySync();
    }

    public String getOutfitFlavour() {
        return outfitFlavour;
    }

    public void setOutfitFlavour(String outfitFlavour) {
        this.outfitFlavour = outfitFlavour;
        dirtySync();
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.OUTFIT_MAKER;
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString(TAG_OUTFIT_NAME, outfitName);
        compound.setString(TAG_OUTFIT_FLAVOUR, outfitFlavour);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(TAG_OUTFIT_NAME, NBT.TAG_STRING)) {
            outfitName = compound.getString(TAG_OUTFIT_NAME);
        }
        if (compound.hasKey(TAG_OUTFIT_FLAVOUR, NBT.TAG_STRING)) {
            outfitFlavour = compound.getString(TAG_OUTFIT_FLAVOUR);
        }
    }
}
