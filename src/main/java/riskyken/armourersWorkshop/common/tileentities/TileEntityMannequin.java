package riskyken.armourersWorkshop.common.tileentities;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.render.EntityTextureInfo;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.GameProfileUtils;
import riskyken.armourersWorkshop.utils.GameProfileUtils.IGameProfileCallback;
import riskyken.armourersWorkshop.utils.UtilBlocks;
import riskyken.armourersWorkshop.utils.UtilItems;

public class TileEntityMannequin extends AbstractTileEntityInventory implements IGameProfileCallback {
    
    private static final String TAG_OWNER = "owner";
    private static final String TAG_ROTATION = "rotation";
    private static final String TAG_IS_DOLL = "isDoll";
    private static final String TAG_HEIGHT_OFFSET = "heightOffset";
    private static final String TAG_SKIN_COLOUR = "skinColour";
    private static final String TAG_HAIR_COLOUR = "hairColour";
    private static final String TAG_OFFSET_X = "offsetX";
    private static final String TAG_OFFSET_Y = "offsetY";
    private static final String TAG_OFFSET_Z = "offsetZ";
    private static final String TAG_RENDER_EXTRAS = "renderExtras";
    private static final int INVENTORY_SIZE = 7;
    
    private GameProfile gameProfile = null;
    private GameProfile newProfile = null;
    private MannequinFakePlayer fakePlayer = null;
    
    private BipedRotations bipedRotations;
    private int rotation;
    
    private float offsetX = 0F;
    
    private float offsetY = 0F;
    
    private float offsetZ = 0F;
    
    private boolean renderExtras = true;
    
    /** Is this mannequin a one block tall doll model? */
    private boolean isDoll;
    
    private int heightOffset;
    
    /** Skin colour of this mannequin. */
    private int skinColour = 0xFF99684D;
    
    /** Hair colour of this mannequin. */
    private int hairColour = 0xFF291C15;
    
    /** Should the tile entity drop as an item when broken? */
    private boolean dropItems = true;
    
     /** Keeps track if the inventory skins have been updated so the render can update. */
    @SideOnly(Side.CLIENT)
    private boolean skinsUpdated;
    
    /** Texture used when rendering this mannequin. */
    @SideOnly(Side.CLIENT)
    public EntityTextureInfo skinTexture;
    
    @SideOnly(Side.CLIENT)
    public ISkinPointer[] sp;
    
    public TileEntityMannequin() {
        this(false);
    }
    
    public void gotUpdateFromClient(float offsetX, float offsetY, float offsetZ,
            int skinColour, int hairColour, String username, boolean renderExtras) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.skinColour = skinColour;
        this.hairColour = hairColour;
        if (gameProfile == null) {
            setGameProfile(username);
        } else {
            if (!gameProfile.getName().equals(username)) {
                setGameProfile(username);
            }
        }
        this.renderExtras = renderExtras;
        
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public boolean isRenderExtras() {
        return renderExtras;
    }

    public TileEntityMannequin(boolean isDoll) {
        super(INVENTORY_SIZE);
        bipedRotations = new BipedRotations();
        this.isDoll = isDoll;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean haveSkinsUpdated() {
        if (skinsUpdated) {
            skinsUpdated = false;
            return true;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    private void setSkinsUpdated(boolean skinsUpdated) {
        this.skinsUpdated = skinsUpdated;
    }
    
    private static String[] specialPeople = {
            "eba64cb1-0d29-4434-8d5e-31004b00488c", //RiskyKen
            "b027a4f4-d480-426c-84a3-a9cb029f4b72", //Vic
            "4fda0709-ada7-48a6-b4bf-0bbce8c40dfa", //Nanoha
            "b9e99f95-09fe-497a-8a77-1ccc839ab0f4"  //VermillionX
            };
  
    private static float[][] specialColours = {
            {249F / 255, 223F / 255, 140F / 255},
            {208F / 255, 212F / 255, 248F / 255},
            {1F, 173F / 255, 1F},
            {45F / 255, 45F / 255, 45F / 255}
            };
    
    public boolean hasSpecialRender() {
        if (gameProfile == null) {
            return false;
        }
        
        if (gameProfile.getId() == null) {
            return false;
        }
        
        for (int i = 0; i < specialPeople.length; i++) {
            if (gameProfile.getId().toString().equals(specialPeople[i])) {
                return true;
            }
        }
        
        return false;
    }
    
    public float[] getSpecialRenderColour() {
        float[] colour = new float[3];
        if (gameProfile == null) {
            return colour;
        }
        
        for (int i = 0; i < specialColours.length; i++) {
            if (gameProfile.getId().toString().equals(specialPeople[i])) {
                return specialColours[i];
            }
        }
        
        return colour;
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
        if (worldObj.isRemote) {
            setSkinsUpdated(true);
        }
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    public void setOwner(ItemStack stack) {
        if (stack.hasDisplayName()) {
            setGameProfile(stack.getDisplayName());
            
            stack.stackSize--;
            updateProfileData();
        }
    }
    
    public void setGameProfile(String username) {
        gameProfile = null;
        if (!StringUtils.isNullOrEmpty(username)) {
            setGameProfile(new GameProfile(null, username));
        }
    }
    
    public boolean getIsDoll() {
        return isDoll;
    }
    
    public void setDoll(boolean isDoll) {
        this.isDoll = isDoll;
    }
    
    public void setDropItems(boolean dropItems) {
        this.dropItems = dropItems;
    }
    
    public int getSkinColour() {
        return skinColour;
    }
    
    public void setSkinColour(int skinColour) {
        this.skinColour = skinColour;
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    public int getHairColour() {
        return hairColour;
    }
    
    public void setHairColour(int hairColour) {
        this.hairColour = hairColour;
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    @Override
    public void invalidate() {
        if (!worldObj.isRemote & dropItems) {
            ItemStack stack = new ItemStack(ModBlocks.mannequin);
            if (isDoll) {
                stack = new ItemStack(ModBlocks.doll);
            }
            if (gameProfile != null) {
                NBTTagCompound profileTag = new NBTTagCompound();
                NBTUtil.writeGameProfile(profileTag, gameProfile);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
                //stack.setStackDisplayName(gameProfile.getName());
            }
            UtilItems.spawnItemInWorld(getWorld(), getPos(), stack);
            UtilBlocks.dropInventoryBlocks(worldObj, this, getPos());
        }
        super.invalidate();
    }
    
    public void setGameProfile(GameProfile gameProfile) {
        this.newProfile = null;
        this.gameProfile = gameProfile;
        if (!worldObj.isRemote) {
            updateProfileData();
        }
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    public int getHeightOffset() {
        return heightOffset;
    }
    
    public void setHeightOffset(int heightOffset) {
        this.heightOffset = heightOffset;
    }
    
    private void updateHeightOffset() {
    }
    
    public void setRotation(int rotation) {
        this.rotation = rotation;
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public GameProfile getGameProfile() {
        return gameProfile;
    }
    
    public BipedRotations getBipedRotations() {
        return bipedRotations;
    }
    
    public void setBipedRotations(BipedRotations bipedRotations) {
        this.bipedRotations = bipedRotations;
        updateHeightOffset();
        markDirty();
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
        
    public MannequinFakePlayer getFakePlayer() {
        return fakePlayer;
    }
    
    public void setFakePlayer(MannequinFakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }
    
    private void updateProfileData(){
        GameProfileUtils.updateProfileData(gameProfile, this);
    }
    
    @Override
    public void readCommonFromNBT(NBTTagCompound compound) {
        super.readCommonFromNBT(compound);
        bipedRotations.loadNBTData(compound);
        this.isDoll = compound.getBoolean(TAG_IS_DOLL);
        this.rotation = compound.getInteger(TAG_ROTATION);
        if (compound.hasKey(TAG_SKIN_COLOUR, 3)) {
            this.skinColour = compound.getInteger(TAG_SKIN_COLOUR);
        }
        if (compound.hasKey(TAG_HAIR_COLOUR, 3)) {
            this.hairColour = compound.getInteger(TAG_HAIR_COLOUR);
        }
        this.offsetX = compound.getFloat(TAG_OFFSET_X);
        this.offsetY = compound.getFloat(TAG_OFFSET_Y);
        this.offsetZ = compound.getFloat(TAG_OFFSET_Z);
        if (compound.hasKey(TAG_RENDER_EXTRAS)) {
            this.renderExtras = compound.getBoolean(TAG_RENDER_EXTRAS);
        }
        if (compound.hasKey(TAG_OWNER, 10)) {
            this.gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
        }
    }
    
    @Override
    public void writeCommonToNBT(NBTTagCompound compound) {
        super.writeCommonToNBT(compound);
        bipedRotations.saveNBTData(compound);
        compound.setBoolean(TAG_IS_DOLL, this.isDoll);
        compound.setInteger(TAG_ROTATION, this.rotation);
        compound.setInteger(TAG_SKIN_COLOUR, this.skinColour);
        compound.setInteger(TAG_HAIR_COLOUR, this.hairColour);
        compound.setFloat(TAG_OFFSET_X, this.offsetX);
        compound.setFloat(TAG_OFFSET_Y, this.offsetY);
        compound.setFloat(TAG_OFFSET_Z, this.offsetZ);
        compound.setBoolean(TAG_RENDER_EXTRAS, this.renderExtras);
        if (this.newProfile != null) {
            this.gameProfile = newProfile;
            this.newProfile = null;
        }
        if (this.gameProfile != null) {
            NBTTagCompound profileTag = new NBTTagCompound();
            NBTUtil.writeGameProfile(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
        }
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        gameProfile = null;
        readFromNBT(compound);
        skinsUpdated = true;
        worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        
        //bb = new AxisAlignedBB(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
        return bb;
    }

    @Override
    public String getName() {
        return LibBlockNames.MANNEQUIN;
    }

    @Override
    public void profileUpdated(GameProfile gameProfile) {
        newProfile = gameProfile;
        markDirty();
        //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
