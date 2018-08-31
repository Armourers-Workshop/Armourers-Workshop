package riskyken.armourers_workshop.common.tileentities;

import com.mojang.authlib.GameProfile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.skin.data.ISkinPointer;
import riskyken.armourers_workshop.client.render.EntityTextureInfo;
import riskyken.armourers_workshop.client.render.MannequinFakePlayer;
import riskyken.armourers_workshop.common.GameProfileCache;
import riskyken.armourers_workshop.common.GameProfileCache.IGameProfileCallback;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.data.BipedRotations;
import riskyken.armourers_workshop.common.data.TextureType;
import riskyken.armourers_workshop.common.lib.LibBlockNames;

public class TileEntityMannequin extends AbstractTileEntityInventory implements IGameProfileCallback {
    
    public static final int CONS_OFFSET_MAX = 3;
    
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
    private static final String TAG_FLYING = "flying";
    private static final String TAG_VISIBLE = "visible";
    private static final String TAG_TEXTURE_TYPE = "textureType";
    private static final String TAG_IMAGE_URL = "imageUrl";
    
    public static final int INVENTORY_ROW_SIZE = 7;
    public static final int INVENTORY_ROWS_COUNT = 5;
    public static final int INVENTORY_SIZE = INVENTORY_ROW_SIZE * INVENTORY_ROWS_COUNT;
    
    private GameProfile gameProfile = null;
    private GameProfile newProfile = null;
    
    @SideOnly(Side.CLIENT)
    private MannequinFakePlayer fakePlayer;
    
    private BipedRotations bipedRotations;
    private int rotation;
    
    private float offsetX = 0F;
    
    private float offsetY = 0F;
    
    private float offsetZ = 0F;
    
    private boolean renderExtras = true;
    
    private boolean flying = false;
    
    private boolean visible = true;
    
    private TextureType textureType = TextureType.USER;
    
    private String imageUrl = null;
    
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
    
    public TileEntityMannequin(boolean isDoll) {
        super(INVENTORY_SIZE);
        bipedRotations = new BipedRotations();
        this.isDoll = isDoll;
    }
    
    public void gotUpdateFromClient(float offsetX, float offsetY, float offsetZ,
            int skinColour, int hairColour, String username, boolean renderExtras,
            boolean flying, boolean visible, TextureType textureType) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.skinColour = skinColour;
        this.hairColour = hairColour;
        this.flying = flying;
        this.visible = visible;
        this.textureType = textureType;
        if (this.textureType == TextureType.USER) {
            if (gameProfile == null) {
                setGameProfile(username);
            } else {
                if (!gameProfile.getName().equals(username)) {
                    setGameProfile(username);
                }
            }
            this.imageUrl = null;
        } else {
            this.imageUrl = username;
            this.gameProfile = null;
        }

        this.renderExtras = renderExtras;
        
        dirtySync();
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
    
    public boolean isFlying() {
        return flying;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public TextureType getTextureType() {
        return textureType;
    }
    
    public String getImageUrl() {
        return imageUrl;
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
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
        if (getWorld().isRemote) {
            setSkinsUpdated(true);
        }
        syncWithClients();
    }
    
    public void setOwner(ItemStack stack) {
        if (stack.hasDisplayName()) {
            setGameProfile(stack.getDisplayName());
            
            stack.shrink(1);
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
    
    public boolean getDropItems() {
        return dropItems;
    }
    
    public int getSkinColour() {
        return skinColour;
    }
    
    public void setSkinColour(int skinColour) {
        this.skinColour = skinColour;
        dirtySync();
    }
    
    public int getHairColour() {
        return hairColour;
    }
    
    public void setHairColour(int hairColour) {
        this.hairColour = hairColour;
        dirtySync();
    }
    
    public ItemStack getDropStack() {
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
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString(TAG_IMAGE_URL, imageUrl);
        }
        return stack;
    }
    
    public void setGameProfile(GameProfile gameProfile) {
        this.newProfile = null;
        this.gameProfile = gameProfile;
        if (gameProfile != null) {
            textureType = TextureType.USER;
        }
        if (!getWorld().isRemote) {
            updateProfileData();
        }
        dirtySync();
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            textureType = TextureType.URL;
        }
        dirtySync();
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
        dirtySync();
    }
    
    public int getRotation() {
        return rotation;
    }
    
    public GameProfile getGameProfile() {
        if (newProfile != null) {
            gameProfile = newProfile;
            newProfile = null;
        }
        return gameProfile;
    }
    
    public BipedRotations getBipedRotations() {
        return bipedRotations;
    }
    
    public void setBipedRotations(BipedRotations bipedRotations) {
        this.bipedRotations = bipedRotations;
        updateHeightOffset();
        dirtySync();
    }
    
    @SideOnly(Side.CLIENT)
    public MannequinFakePlayer getFakePlayer() {
        return fakePlayer;
    }
    
    private void updateProfileData() {
        GameProfile newProfile = GameProfileCache.getGameProfile(gameProfile, this);
        if (newProfile != null) {
            profileDownloaded(newProfile);
        }
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
        
        offsetX = MathHelper.clamp(offsetX, -CONS_OFFSET_MAX, CONS_OFFSET_MAX);
        offsetY = MathHelper.clamp(offsetY, -CONS_OFFSET_MAX, CONS_OFFSET_MAX);
        offsetZ = MathHelper.clamp(offsetZ, -CONS_OFFSET_MAX, CONS_OFFSET_MAX);
        
        if (compound.hasKey(TAG_RENDER_EXTRAS)) {
            this.renderExtras = compound.getBoolean(TAG_RENDER_EXTRAS);
        }
        if (compound.hasKey(TAG_FLYING)) {
            this.flying = compound.getBoolean(TAG_FLYING);
        }
        if (compound.hasKey(TAG_VISIBLE)) {
            this.visible = compound.getBoolean(TAG_VISIBLE);
        }
        if (compound.hasKey(TAG_TEXTURE_TYPE, Constants.NBT.TAG_BYTE)) {
            this.textureType = TextureType.values()[compound.getByte(TAG_TEXTURE_TYPE)];
        }
        if (compound.hasKey(TAG_OWNER, Constants.NBT.TAG_COMPOUND)) {
            this.gameProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_OWNER));
        } else {
            this.gameProfile = null;
        }
        if (compound.hasKey(TAG_IMAGE_URL, Constants.NBT.TAG_STRING)) {
            this.imageUrl = compound.getString(TAG_IMAGE_URL);
        } else {
            this.imageUrl = null;
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
        compound.setBoolean(TAG_FLYING, this.flying);
        compound.setBoolean(TAG_VISIBLE, this.visible);
        compound.setByte(TAG_TEXTURE_TYPE, (byte) this.textureType.ordinal());
        if (this.newProfile != null) {
            this.gameProfile = newProfile;
            this.newProfile = null;
        }
        if (this.gameProfile != null) {
            NBTTagCompound profileTag = new NBTTagCompound();
            NBTUtil.writeGameProfile(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
        }
        if (this.imageUrl != null) {
            compound.setString(TAG_IMAGE_URL, this.imageUrl);
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
        return new SPacketUpdateTileEntity(getPos(), 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        gameProfile = null;
        readFromNBT(compound);
        skinsUpdated = true;
        if (getWorld() != null && getWorld().isRemote) {
            setupFakePlayer();
        }
        dirtySync();
    }
    
    @SideOnly(Side.CLIENT)
    private void setupFakePlayer() {
        if (fakePlayer != null) {
            return;
        }
        /*
        fakePlayer = new MannequinFakePlayer(worldObj, new GameProfile(null, "[Mannequin]"));
        fakePlayer.posX = xCoord;
        fakePlayer.posY = yCoord;
        fakePlayer.posZ = zCoord;
        fakePlayer.prevPosX = xCoord;
        fakePlayer.prevPosY = yCoord;
        fakePlayer.prevPosZ = zCoord;
        */
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = new AxisAlignedBB(-1, 0, -1, 2, 3, 2);
        bb.offset(getPos());
        return bb;
    }

    @Override
    public String getName() {
        return LibBlockNames.MANNEQUIN;
    }

    @Override
    public void profileDownloaded(GameProfile gameProfile) {
        newProfile = gameProfile;
        markDirty();
        if (getWorld() != null) {
            syncWithClients();
        }
    }
}
