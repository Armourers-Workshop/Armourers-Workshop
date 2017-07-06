package riskyken.armourersWorkshop.common.tileentities;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.render.EntityTextureInfo;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.BlockUtils;
import riskyken.armourersWorkshop.utils.GameProfileUtils;
import riskyken.armourersWorkshop.utils.GameProfileUtils.IGameProfileCallback;

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
    private static final String TAG_FLYING = "flying";
    private static final String TAG_VISIBLE = "visible";
    private static final String TAG_TEXTURE_TYPE = "textureType";
    private static final String TAG_IMAGE_URL = "imageUrl";
    private static final int INVENTORY_SIZE = 7;
    
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
        
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
            "b9e99f95-09fe-497a-8a77-1ccc839ab0f4", //VermillionX
            "0d98df01-26da-496c-ba7c-744a20a7b2c2", //Servantfly
            "eda5e4cb-3b09-4b2c-b56c-d27d658d2e5d", //Gray_Mooo
            "3e6a5f19-bb37-4f9a-94e0-7ccd67ef1d61", //Flummie2000
            "e10ebd90-7922-4777-9cf6-76ecc70848ec", //LordPhrozen
            "3683eab5-5a23-4cdb-b1f5-38090f1ba4a8", //TheEpicJames
            "55b1659a-810f-4687-a514-b3201b09fd69"  //V972
            };
  
    private static float[][] specialColours = {
            {249F / 255, 223F / 255, 140F / 255},
            {208F / 255, 212F / 255, 248F / 255},
            {1F, 173F / 255, 1F},
            {45F / 255, 45F / 255, 45F / 255},
            {0F, 247F / 255, 141F / 255},
            {1F, 0F, 0F},
            {92F / 255, 32F / 255, 102F / 255},
            {66F / 255, 244F / 255, 110F / 255},
            {1F, 153F / 255, 0F},
            {67F / 255, 232F / 255, 113F / 255}
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
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
        if (worldObj.isRemote) {
            setSkinsUpdated(true);
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public int getHairColour() {
        return hairColour;
    }
    
    public void setHairColour(int hairColour) {
        this.hairColour = hairColour;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
                NBTUtil.func_152460_a(profileTag, gameProfile);
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setTag(TAG_OWNER, profileTag);
                //stack.setStackDisplayName(gameProfile.getName());
            }
            if (!StringUtils.isNullOrEmpty(imageUrl)) {
                stack.setTagCompound(new NBTTagCompound());
                stack.getTagCompound().setString(TAG_IMAGE_URL, imageUrl);
            }
            float f = 0.7F;
            double xV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double yV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double zV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(worldObj, (double)xCoord + xV, (double)yCoord + yV, (double)zCoord + zV, stack);
            worldObj.spawnEntityInWorld(entityitem);
            BlockUtils.dropInventoryBlocks(worldObj, this, xCoord, yCoord, zCoord);
        }
        super.invalidate();
    }
    
    public void setGameProfile(GameProfile gameProfile) {
        this.newProfile = null;
        this.gameProfile = gameProfile;
        if (gameProfile != null) {
            textureType = TextureType.USER;
        }
        if (!worldObj.isRemote) {
            updateProfileData();
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if (!StringUtils.isNullOrEmpty(imageUrl)) {
            textureType = TextureType.URL;
        }
        markDirty();
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @SideOnly(Side.CLIENT)
    public MannequinFakePlayer getFakePlayer() {
        return fakePlayer;
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
            this.gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
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
            NBTUtil.func_152460_a(profileTag, this.gameProfile);
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
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCommonToNBT(compound);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        gameProfile = null;
        readFromNBT(compound);
        skinsUpdated = true;
        if (worldObj != null && worldObj.isRemote) {
            setupFakePlayer();
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @SideOnly(Side.CLIENT)
    private void setupFakePlayer() {
        if (fakePlayer != null) {
            return;
        }
        fakePlayer = new MannequinFakePlayer(worldObj, new GameProfile(null, "[Mannequin]"));
        fakePlayer.posX = xCoord;
        fakePlayer.posY = yCoord;
        fakePlayer.posZ = zCoord;
        fakePlayer.prevPosX = xCoord;
        fakePlayer.prevPosY = yCoord;
        fakePlayer.prevPosZ = zCoord;
    }
    
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        bb = AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
        return bb;
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.MANNEQUIN;
    }

    @Override
    public void profileUpdated(GameProfile gameProfile) {
        newProfile = gameProfile;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public static enum TextureType {
        USER,
        URL
    }
}
