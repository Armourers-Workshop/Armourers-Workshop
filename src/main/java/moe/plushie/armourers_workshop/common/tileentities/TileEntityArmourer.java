package moe.plushie.armourers_workshop.common.tileentities;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.blocks.BlockLocation;
import moe.plushie.armourers_workshop.common.data.TextureType;
import moe.plushie.armourers_workshop.common.exception.SkinSaveException;
import moe.plushie.armourers_workshop.common.items.ItemSkin;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.painting.IBlockPainter;
import moe.plushie.armourers_workshop.common.skin.ArmourerWorldHelper;
import moe.plushie.armourers_workshop.common.skin.ISkinHolder;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperty;
import moe.plushie.armourers_workshop.common.skin.data.SkinTexture;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class TileEntityArmourer extends AbstractTileEntityInventory {
    
    private static final String TAG_DIRECTION = "direction";
    private static final String TAG_TYPE = "skinType";
    private static final String TAG_SHOW_GUIDES = "showGuides";
    private static final String TAG_SHOW_OVERLAY = "showOverlay";
    private static final String TAG_SHOW_HELPER = "showHelper";
    private static final String TAG_PAINT_DATA = "paintData";
    private static final String TAG_TEXTURE = "texture";
    
    private static final int HEIGHT_OFFSET = 1;
    private static final int INVENTORY_SIZE = 2;
    
    private EnumFacing direction;
    private ISkinType skinType;
    private boolean showGuides;
    private boolean showOverlay;
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
        this.showOverlay = true;
        this.showGuides = true;
        this.showHelper = true;
        this.skinProps = new SkinProperties();
        clearPaintData(false);
    }
    
    public int[] getPaintData() {
        return paintData;
    }
    
    public void updatePaintData(int x, int y, int colour) {
        paintData[x + (y * SkinTexture.TEXTURE_WIDTH)] = colour;
        this.markDirty();
        syncWithClients();
    }
    
    public int getPaintData(int x, int y) {
        return paintData[x + (y * SkinTexture.TEXTURE_WIDTH)];
    }
    
    /**
     * Get blocks in the world and saved them onto an items NBT data.
     * @param player The player that pressed the save button.
     * @param name Custom name for the item.
     */
    public void saveArmourItem(EntityPlayerMP player, String customName, String tags) {
        if (getWorld().isRemote) {
            return;
        }

        ItemStack stackInput = getStackInSlot(0);
        ItemStack stackOutput = getStackInSlot(1);
        
        if (!player.capabilities.isCreativeMode) {
            if (stackInput.isEmpty()) {
                return;
            }
        }
        
        if (!stackOutput.isEmpty()) {
            return;
        }
        
        ISkinHolder inputItem = null;
        if (!player.capabilities.isCreativeMode) {
            if (!(stackInput.getItem() instanceof ISkinHolder)) {
                return;
            }
            inputItem = (ISkinHolder)stackInput.getItem();
        } else {
            inputItem = (ISkinHolder) ModItems.equipmentSkinTemplate;
        }

        Skin armourItemData = null;
        SkinProperties skinProps = new SkinProperties();
        skinProps.setProperty(Skin.KEY_AUTHOR_NAME, player.getName());
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
            skinProps.setProperty(Skin.KEY_AUTHOR_UUID, player.getGameProfile().getId().toString());
        }
        skinProps.setProperty(Skin.KEY_CUSTOM_NAME, customName);
        
        for (int i = 0; i < skinType.getProperties().size(); i++) {
            SkinProperty skinProp = (SkinProperty) skinType.getProperties().get(i);
            skinProp.setValue(skinProps, skinProp.getValue(this.skinProps));
        }
        
        try {
            armourItemData = ArmourerWorldHelper.saveSkinFromWorld(getWorld(), skinProps, skinType,
                    paintData, getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), direction);
        } catch (SkinSaveException e) {
            switch (e.getType()) {
            case NO_DATA:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case MARKER_ERROR:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case MISSING_PARTS:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case BED_AND_SEAT:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            case INVALID_MULTIBLOCK:
                player.sendMessage(new TextComponentString(e.getMessage()));
                break;
            }
        }
        ModLogger.log("save");
        if (armourItemData == null) {
            return;
        }
        
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(armourItemData, (LibraryFile)null);
        
        stackOutput = inputItem.makeStackForEquipment(armourItemData);
        if (stackOutput.isEmpty()) {
            return;
        }
        if (!player.capabilities.isCreativeMode) {
            this.decrStackSize(0, 1);
        }
        setInventorySlotContents(1, stackOutput);
    }

    /**
     * Reads the NBT data from an item and places blocks in the world.
     * @param player The player that pressed the load button.
     */
    public void loadArmourItem(EntityPlayerMP player) {
        if (getWorld().isRemote) {
            return;
        }
        ItemStack stackInput = this.getStackInSlot(0);
        ItemStack stackOuput = this.getStackInSlot(1);
        
        if (stackInput.isEmpty()) {
            return;
        }
        if (!stackOuput.isEmpty()) {
            return;
        }
        if (!(stackInput.getItem() instanceof ItemSkin)) {
            return;
        }
        SkinDescriptor skinPointerInput = SkinNBTHelper.getSkinDescriptorFromStack(stackInput);
        if (skinPointerInput == null) {
            return;
        }
        if (skinType == null) {
            return;
        }
        if (skinType != skinPointerInput.getIdentifier().getSkinType()) {
            if (!(skinType == SkinTypeRegistry.skinLegs && skinPointerInput.getIdentifier().getSkinType() == SkinTypeRegistry.oldSkinSkirt)) {
                return;
            }
        }
        
        Skin skin = CommonSkinCache.INSTANCE.getSkin(skinPointerInput);
        if (skin == null) {
            return;
        }
        
        setSkinProps(new SkinProperties(skin.getProperties()));
        
        ArmourerWorldHelper.loadSkinIntoWorld(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), skin, direction);
        if (skin.hasPaintData()) {
            this.paintData = skin.getPaintData().clone();
        } else {
            clearPaintData(true);
        }
        dirtySync();
        
        this.setInventorySlotContents(0, ItemStack.EMPTY);
        this.setInventorySlotContents(1, stackInput);
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
            ArrayList<BlockLocation> paintableCubes = ArmourerWorldHelper.getListOfPaintableCubes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), skinType);
            for (int i = 0; i < paintableCubes.size(); i++) {
                BlockLocation bl = paintableCubes.get(i);
                IBlockState blockState = world.getBlockState(new BlockPos(bl.x, bl.y, bl.z));
                //IPantableBlock pBlock = (IPantableBlock) blockState.getBlock();
                Block block = blockState.getBlock();
                
                if (block instanceof IPantableBlock) {
                    for (int side = 0; side < 6; side++) {
                        EnumFacing face = EnumFacing.VALUES[i];
                        tool.usedOnBlockSide(stack, player, world, bl.getPos(), block, face);
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
            SkinProperties newSkinProps = new SkinProperties();
            SkinProperties.PROP_BLOCK_MULTIBLOCK.setValue(newSkinProps, SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skinProps));
            setSkinProps(newSkinProps);
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
            boolean hadBounds = !SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(this.skinProps);
            if (hadBounds) {
                ArmourerWorldHelper.createBoundingBoxes(getWorld(), getPos().offset(EnumFacing.UP, HEIGHT_OFFSET), getPos(), skinType);
            }
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
        return INFINITE_EXTENT_AABB;
    }

    public ISkinType getSkinType() {
        return skinType;
    }
    
    public boolean isShowGuides() {
        return showGuides;
    }
    
    public boolean isShowOverlay() {
        return showOverlay;
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
        skinProps = new SkinProperties();
        clearPaintData(true);
        createBoundingBoxes(); 
        dirtySync();
    }
    
    public void toggleGuides() {
        this.showGuides = !this.showGuides;
        dirtySync();
    }
    
    public void toggleOverlay() {
        this.showOverlay = !this.showOverlay;
        dirtySync();;
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
            boolean hadBounds = !SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(this.skinProps);
            boolean haveBounds = !SkinProperties.PROP_ARMOUR_OVERRIDE.getValue(skinProps);
            if (hadBounds != haveBounds) {
                updateBounds = true;
            }
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
        return LibBlockNames.ARMOURER_BRAIN;
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
    /*
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        // TODO Auto-generated method stub
        super.handleUpdateTag(tag);
    }*/
    
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
        showOverlay = compound.getBoolean(TAG_SHOW_OVERLAY);
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
        compound.setBoolean(TAG_SHOW_OVERLAY, showOverlay);
        compound.setBoolean(TAG_SHOW_HELPER, showHelper);
        skinProps.writeToNBT(compound);
        NBTTagCompound textureCompound = new NBTTagCompound();
        texture.writeToNBT(textureCompound);
        compound.setTag(TAG_TEXTURE, textureCompound);
        compound.setIntArray(TAG_PAINT_DATA, this.paintData);
    }
}
