package moe.plushie.armourers_workshop.common.tileentities;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.gui.mannequin.GuiMannequin;
import moe.plushie.armourers_workshop.common.GameProfileCache;
import moe.plushie.armourers_workshop.common.GameProfileCache.IGameProfileCallback;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.inventory.ContainerMannequin;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.tileentities.property.TileProperty;
import moe.plushie.armourers_workshop.common.world.AsyncWorldUpdateGameProfileDownload;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMannequin extends AbstractTileEntityInventory implements IGameProfileCallback, IGuiFactory {
    
    public static final int CONS_OFFSET_MAX = 3;
    public static final int CONS_INVENTORY_ROW_SIZE = 7;
    public static final int CONS_INVENTORY_ROWS_COUNT = 5;
    public static final int CONS_INVENTORY_SIZE = CONS_INVENTORY_ROW_SIZE * CONS_INVENTORY_ROWS_COUNT;
    
    public final TileProperty<GameProfile> PROP_OWNER = new TileProperty<GameProfile>(this, "owner", GameProfile.class, null);
    public final TileProperty<Integer> PROP_ROTATION = new TileProperty<Integer>(this, "rotation", Integer.class, 0);
    public final TileProperty<Boolean> PROP_DOLL = new TileProperty<Boolean>(this, "doll", Boolean.class, false);
    public final TileProperty<ExtraColours> PROP_EXTRA_COLOURS = new TileProperty<ExtraColours>(this, "extra_colours", ExtraColours.class, null);
    public final TileProperty<Float> PROP_OFFSET_X = new TileProperty<Float>(this, "offset_x", Float.class, 0F);
    public final TileProperty<Float> PROP_OFFSET_Y = new TileProperty<Float>(this, "offset_y", Float.class, 0F);
    public final TileProperty<Float> PROP_OFFSET_Z = new TileProperty<Float>(this, "offset_z", Float.class, 0F);
    public final TileProperty<Boolean> PROP_RENDER_EXTRAS = new TileProperty<Boolean>(this, "render_extras", Boolean.class, true);
    public final TileProperty<Boolean> PROP_FLYING = new TileProperty<Boolean>(this, "flying", Boolean.class, false);
    public final TileProperty<Boolean> PROP_VISIBLE = new TileProperty<Boolean>(this, "visible", Boolean.class, true);
    public final TileProperty<TextureType> PROP_TEXTURE_TYPE = new TileProperty<TextureType>(this, "texture_type", TextureType.class, TextureType.USER);
    public final TileProperty<String> PROP_IMAGE_URL = new TileProperty<String>(this, "image_url", String.class, null);
    public final TileProperty<BipedRotations> PROP_BIPED_ROTATIONS = new TileProperty<BipedRotations>(this, "image_url", BipedRotations.class, null);
    public final TileProperty<Boolean> PROP_NOCLIP = new TileProperty<Boolean>(this, "noclip", Boolean.class, false);
    
    public TileEntityMannequin(boolean isDoll) {
        super(CONS_INVENTORY_SIZE);
        PROP_DOLL.set(isDoll);
        PROP_BIPED_ROTATIONS.set(new BipedRotations());
        PROP_EXTRA_COLOURS.set(ExtraColours.EMPTY_COLOUR);
    }
    
    public TileEntityMannequin() {
        this(false);
    }
    
    @Override
    public void onPropertyChanged(TileProperty<?> property) {
        if (property == PROP_OWNER) {
            AsyncWorldUpdateGameProfileDownload profileDownload = new AsyncWorldUpdateGameProfileDownload(getPos(), getWorld());
            GameProfileCache.getGameProfile(PROP_OWNER.get(), profileDownload);
        }
        super.onPropertyChanged(property);
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
        if (getWorld().isRemote) {
            //setSkinsUpdated(true);
        }
        syncWithClients();
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        readFromNBT(compound);
        dirtySync();
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = new AxisAlignedBB(-1, 0, -1, 2, 3, 2);
        return bb.offset(getPos());
    }

    @Override
    public String getName() {
        return LibBlockNames.MANNEQUIN;
    }

    @Override
    public void profileDownloaded(GameProfile gameProfile) {
        ModLogger.log("got profile " + gameProfile);
        PROP_OWNER.loadType(gameProfile);
        dirtySync();
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerMannequin(player.inventory, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiMannequin(player.inventory, this);
    }
}
