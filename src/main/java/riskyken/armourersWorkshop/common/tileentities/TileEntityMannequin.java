package riskyken.armourersWorkshop.common.tileentities;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StringUtils;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.data.BipedRotations;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.utils.GameProfileUtils;
import riskyken.armourersWorkshop.utils.GameProfileUtils.IGameProfileCallback;
import riskyken.armourersWorkshop.utils.UtilBlocks;

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
    
    protected GameProfile gameProfile = null;
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
    
    public TileEntityMannequin() {
        this(false);
    }
    
    public TileEntityMannequin(boolean isDoll) {
        super(INVENTORY_SIZE);
        bipedRotations = new BipedRotations();
        this.isDoll = isDoll;
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
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        super.setInventorySlotContents(i, itemstack);
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
            float f = 0.7F;
            double xV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double yV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double zV = (double)(worldObj.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(worldObj, (double)xCoord + xV, (double)yCoord + yV, (double)zCoord + zV, stack);
            worldObj.spawnEntityInWorld(entityitem);
            UtilBlocks.dropInventoryBlocks(worldObj, this, xCoord, yCoord, zCoord);
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
            this.gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
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
            NBTUtil.func_152460_a(profileTag, this.gameProfile);
            compound.setTag(TAG_OWNER, profileTag);
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
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
        //worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
