package moe.plushie.armourers_workshop.common.tileentities;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.armourer.GuiArmourer;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.exception.SkinSaveException;
import moe.plushie.armourers_workshop.common.inventory.ContainerArmourer;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.painting.IBlockPainter;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.world.ArmourerWorldHelper;
import moe.plushie.armourers_workshop.common.world.undo.UndoManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityArmourer extends AbstractTileEntityInventory implements IGuiFactory {
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_TYPE = "skinType";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_SHOW_HELPER = "showHelper";
    private static final String TAG_PAINT_DATA = "paintData";
    private static final String TAG_TEXTURE = "texture";
    
    private static final int HEIGHT_OFFSET = 1;
    private static final int INVENTORY_SIZE = 2;
    private static final AxisAlignedBB AABB = new AxisAlignedBB(-32, -32, -44, 64, 64, 64);
    
    private EnumFacing direction;
    private ISkinType skinType;
    private boolean showGuides;
    private boolean showHelper;
    private SkinProperties skinProps;
    private int[] paintData;
    public boolean loadedArmourItem = false;
    
    private PlayerTexture texture = new PlayerTexture("", TextureType.USER);
    private PlayerTexture textureOld = new PlayerTexture("", TextureType.USER);
    
    @SideOnly(Side.CLIENT)
    public SkinTexture skinTexture;
    
    public TileEntityArmourer() {
        super(INVENTORY_SIZE);
        this.direction = EnumFacing.NORTH;
        this.skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName("armourers:head");
        this.showGuides = true;
        this.showHelper = true;
        this.skinProps = new SkinProperties();
        clearPaintData(false);
    }
    
    public int[] getPaintData() {
        return paintData;
    }
    
    public void setPaintData(int[] paintData) {
        this.paintData = paintData;
    }
    
    public void updatePaintData(int x, int y, int colour) {
        paintData[x + (y * SkinTexture.TEXTURE_WIDTH)] = colour;
        this.markDirty();
        syncWithClients();
    }
    
    public int getPaintData(int x, int y) {
        return paintData[x + (y * SkinTexture.TEXTURE_WIDTH)];
    }
    
    public void clearPaintData(boolean update) {
        this.paintData = new int[SkinTexture.TEXTURE_SIZE];
        for (int i = 0; i < SkinTexture.TEXTURE_SIZE; i++) {
            this.paintData[i] = 0x00FFFFFF;
        }
        if (update) {
            dirtySync();
        }
    }
    
    public void toolUsedOnArmourer(IBlockPainter tool, World world, ItemStack stack, EntityPlayer player) {
        UndoManager.begin(player);
        applyToolToBlocks(tool, world, stack, player);
        UndoManager.end(player);
    }
    
    private void applyToolToBlocks(IBlockPainter tool, World world, ItemStack stack, EntityPlayer player) {
        if (skinType != null) {
            ArrayList<BlockPos> paintableCubes = ArmourerWorldHelper.getListOfPaintableCubes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), skinType);
            for (int i = 0; i < paintableCubes.size(); i++) {
                BlockPos bl = paintableCubes.get(i);
                IBlockState blockState = world.getBlockState(bl);
                //IPantableBlock pBlock = (IPantableBlock) blockState.getBlock();
                Block block = blockState.getBlock();
                
                if (block instanceof IPantableBlock) {
                    for (int side = 0; side < 6; side++) {
                        EnumFacing face = EnumFacing.VALUES[side];
                        tool.usedOnBlockSide(stack, player, world, bl, block, face, false);
                    }
                }
            }
        }
    }
    
    public void onPlaced() {
        createBoundingBoxes();
    }
    
    public void preRemove() {
        removeBoundingBoxes();
    }
    
    public int getHeightOffset() {
        return HEIGHT_OFFSET;
    }
    

    public void copySkinCubes(EntityPlayerMP player, ISkinPartType srcPart, ISkinPartType desPart, boolean mirror) {
        try {
            ArmourerWorldHelper.copySkinCubes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), srcPart, desPart, mirror);
        } catch (SkinSaveException e) {
            player.sendMessage(new TextComponentString(e.getMessage()));
        }
    }

    public void clearArmourCubes(ISkinPartType partType) {
        if (skinType != null) {
            ArmourerWorldHelper.clearEquipmentCubes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), skinType, skinProps, partType);
            SkinProperties newSkinProps = new SkinProperties();
            SkinProperties.PROP_BLOCK_MULTIBLOCK.setValue(newSkinProps, SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps));
            setSkinProps(newSkinProps);
            dirtySync();
        }
    }
    
    public void clearMarkers(ISkinPartType partType) {
        if (skinType != null) {
            ArmourerWorldHelper.clearMarkers(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), skinType, skinProps, partType);
            dirtySync();
        }
    }
    
    protected void removeBoundingBoxes() {
        if (skinType != null) {
            ArmourerWorldHelper.removeBoundingBoxes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), skinType);
        }
    }
    
    protected void createBoundingBoxes() {
        if (skinType != null) {
            ArmourerWorldHelper.createBoundingBoxes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), getPos(), skinType, skinProps);
        }
    }
    
    public void setDirection(EnumFacing direction) {
        this.direction = direction;
        dirtySync();
    }
    
    public EnumFacing getDirection() {
        return direction;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return AABB.offset(getPos());
    }

    public ISkinType getSkinType() {
        return skinType;
    }
    
    public boolean isShowGuides() {
        return showGuides;
    }
    
    public boolean isShowHelper() {
        return showHelper;
    }
    
    public void setTexture(PlayerTexture texture) {
        this.textureOld = this.texture;
        this.texture = texture;
        dirtySync();
    }
    
    public PlayerTexture getTexture() {
        return texture;
    }
    
    public PlayerTexture getTextureOld() {
        return textureOld;
    }
    
    public void setSkinType(ISkinType skinType) {
        if (this.skinType == skinType) {
            return;
        }
        removeBoundingBoxes();
        this.skinType = skinType;
        SkinProperties oldProperties = skinProps;
        skinProps = new SkinProperties();
        SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(skinProps, SkinProperties.PROP_ALL_CUSTOM_NAME.getValue(oldProperties));
        SkinProperties.PROP_ALL_FLAVOUR_TEXT.setValue(skinProps, SkinProperties.PROP_ALL_FLAVOUR_TEXT.getValue(oldProperties));
        clearPaintData(true);
        createBoundingBoxes(); 
        dirtySync();
    }
    
    public void toggleGuides() {
        this.showGuides = !this.showGuides;
        dirtySync();
    }
    
    public void toggleHelper() {
        this.showHelper = !this.showHelper;
        dirtySync();
    }
    
    public SkinProperties getSkinProps() {
        return skinProps;
    }
    
    public void setSkinProps(SkinProperties skinProps) {
        boolean updateBounds = false;
        if (skinType != null && skinType.getVanillaArmourSlotId() != -1) {
            updateBounds = skinType.haveBoundsChanged(this.skinProps, skinProps);
        }
        this.skinProps = skinProps;
        if (updateBounds) {
            removeBoundingBoxes();
            createBoundingBoxes();
        }
        dirtySync();
    }
    
    @Override
    public String getName() {
        return LibBlockNames.ARMOURER;
    }
    
    @Override
    public double getMaxRenderDistanceSquared() {
        return super.getMaxRenderDistanceSquared() * 10;
    }
    
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 5, getUpdateTag());
    }
    
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = super.getUpdateTag();
        writeCommonToNBT(compound);
        return compound;
    }
    
    public Packet getDescriptionPacket() {
        return getUpdatePacket();
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        PlayerTexture playerTexture = texture;
        readBaseFromNBT(compound);
        readCommonFromNBT(compound);
        if (!texture.equals(playerTexture)) {
            textureOld = playerTexture;
        }
        syncWithClients();
        loadedArmourItem = true;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCommonFromNBT(compound);
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCommonToNBT(compound);
        return compound;
    }
    
    @Override
    public void readCommonFromNBT(NBTTagCompound compound) {
        super.readCommonFromNBT(compound);
        direction = EnumFacing.byIndex(compound.getByte(TAG_DIRECTION));
        skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(compound.getString(TAG_TYPE));
        
        showGuides = compound.getBoolean(TAG_SHOW_GUIDES);
        if (compound.hasKey(TAG_SHOW_HELPER)) {
            showHelper = compound.getBoolean(TAG_SHOW_HELPER);
        }
        skinProps = new SkinProperties();
        skinProps.readFromNBT(compound);
        if (compound.hasKey(TAG_TEXTURE, NBT.TAG_COMPOUND)) {
            texture = PlayerTexture.fromNBT(compound.getCompoundTag(TAG_TEXTURE));
        }
        if (compound.hasKey(TAG_PAINT_DATA)) {
            paintData = compound.getIntArray(TAG_PAINT_DATA);
        }
    }
    
    @Override
    public void writeCommonToNBT(NBTTagCompound compound) {
        super.writeCommonToNBT(compound);
        compound.setByte(TAG_DIRECTION, (byte) direction.ordinal());
        if (skinType != null) {
            compound.setString(TAG_TYPE, skinType.getRegistryName());
        }
        compound.setBoolean(TAG_SHOW_GUIDES, showGuides);
        compound.setBoolean(TAG_SHOW_HELPER, showHelper);
        skinProps.writeToNBT(compound);
        NBTTagCompound textureCompound = new NBTTagCompound();
        texture.writeToNBT(textureCompound);
        compound.setTag(TAG_TEXTURE, textureCompound);
        compound.setIntArray(TAG_PAINT_DATA, this.paintData);
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerArmourer(player.inventory, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiArmourer(player.inventory, this);
    }
}
