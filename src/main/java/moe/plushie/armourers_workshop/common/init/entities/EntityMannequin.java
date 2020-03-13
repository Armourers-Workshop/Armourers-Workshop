package moe.plushie.armourers_workshop.common.init.entities;

import java.io.IOException;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.GameProfileCache;
import moe.plushie.armourers_workshop.common.GameProfileCache.IGameProfileCallback;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.init.items.ItemMannequin;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMannequin extends Entity implements IGameProfileCallback {

    public static final DataSerializer<BipedRotations> BIPED_ROTATIONS_SERIALIZER = new DataSerializer<BipedRotations>() {

        @Override
        public void write(PacketBuffer buf, BipedRotations value) {
            value.writeToBuf(buf);
        }

        @Override
        public BipedRotations read(PacketBuffer buf) throws IOException {
            BipedRotations bipedRotations = new BipedRotations();
            bipedRotations.readFromBuf(buf);
            return bipedRotations;
        }

        @Override
        public DataParameter<BipedRotations> createKey(int id) {
            return new DataParameter<BipedRotations>(id, this);
        }

        @Override
        public BipedRotations copyValue(BipedRotations value) {
            return value;
        }
    };

    public static final DataSerializer<TextureData> TEXTURE_DATA_SERIALIZER = new DataSerializer<TextureData>() {

        @Override
        public void write(PacketBuffer buf, TextureData value) {
            value.writeToBuf(buf);
        }

        @Override
        public TextureData read(PacketBuffer buf) throws IOException {
            TextureData textureData = new TextureData();
            textureData.readFromBuf(buf);
            return textureData;
        }

        @Override
        public DataParameter<TextureData> createKey(int id) {
            return new DataParameter<TextureData>(id, this);
        }

        @Override
        public TextureData copyValue(TextureData value) {
            return value;
        }
    };

    static {
        DataSerializers.registerSerializer(BIPED_ROTATIONS_SERIALIZER);
        DataSerializers.registerSerializer(TEXTURE_DATA_SERIALIZER);
    }

    private static final String TAG_BIPED_ROTATIONS = "biped_rotations";
    private static final String TAG_TEXTURE_DATA = "texture_data";
    private static final String TAG_ROTATION = "rotation";
    private static final String TAG_RENDER_EXTRAS = "render_extras";
    private static final String TAG_FLYING = "flying";
    private static final String TAG_VISIBLE = "visible";
    private static final String TAG_NO_CLIP = "no_clip";
    private static final String TAG_SCALE = "scale";

    private static final DataParameter<BipedRotations> DATA_BIPED_ROTATIONS = EntityDataManager.<BipedRotations>createKey(EntityMannequin.class, BIPED_ROTATIONS_SERIALIZER);
    private static final DataParameter<TextureData> DATA_TEXTURE_DATA = EntityDataManager.<TextureData>createKey(EntityMannequin.class, TEXTURE_DATA_SERIALIZER);
    private static final DataParameter<Float> DATA_ROTATION = EntityDataManager.<Float>createKey(EntityMannequin.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> DATA_RENDER_EXTRAS = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_VISIBLE = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_NO_CLIP = EntityDataManager.<Boolean>createKey(EntityMannequin.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> DATA_SCALE = EntityDataManager.<Float>createKey(EntityMannequin.class, DataSerializers.FLOAT);

    private int hitCount = 0;
    
    public EntityMannequin(World worldIn) {
        super(worldIn);
        setSize(0.8F, 1.9F);
    }

    @Override
    protected void entityInit() {
        dataManager.register(DATA_BIPED_ROTATIONS, new BipedRotations());
        dataManager.register(DATA_TEXTURE_DATA, new TextureData());
        dataManager.register(DATA_ROTATION, Float.valueOf(0.0F));
        dataManager.register(DATA_RENDER_EXTRAS, Boolean.valueOf(true));
        dataManager.register(DATA_FLYING, Boolean.valueOf(false));
        dataManager.register(DATA_VISIBLE, Boolean.valueOf(true));
        dataManager.register(DATA_NO_CLIP, Boolean.valueOf(false));
        dataManager.register(DATA_SCALE, Float.valueOf(1F));
    }

    public void setBipedRotations(BipedRotations bipedRotations) {
        dataManager.set(DATA_BIPED_ROTATIONS, bipedRotations);
    }

    public BipedRotations getBipedRotations() {
        return dataManager.get(DATA_BIPED_ROTATIONS);
    }

    public void setTextureData(TextureData textureData, boolean updateProfile) {
        dataManager.set(DATA_TEXTURE_DATA, textureData);
        if (updateProfile) {
            if (!getEntityWorld().isRemote) {
                if (textureData.getTextureType() == TextureType.USER & textureData.getProfile() != null) {
                    GameProfileCache.getGameProfile(textureData.getProfile(), this);
                }
            }
        }
    }

    private void setTextureDataProfile(GameProfile gameProfile) {
        ModLogger.log("got profile back: " + gameProfile);
        dataManager.set(DATA_TEXTURE_DATA, new TextureData(gameProfile));
    }

    public TextureData getTextureData() {
        return dataManager.get(DATA_TEXTURE_DATA);
    }

    public void setRotation(float value) {
        dataManager.set(DATA_ROTATION, Float.valueOf(value));
    }

    public float getRotation() {
        return dataManager.get(DATA_ROTATION).floatValue();
    }

    public void setRenderExtras(boolean value) {
        dataManager.set(DATA_RENDER_EXTRAS, Boolean.valueOf(value));
    }

    public boolean isRenderExtras() {
        return dataManager.get(DATA_RENDER_EXTRAS).booleanValue();
    }

    public void setFlying(boolean value) {
        dataManager.set(DATA_FLYING, Boolean.valueOf(value));
    }

    public boolean isFlying() {
        return dataManager.get(DATA_FLYING).booleanValue();
    }

    public void setVisible(boolean value) {
        dataManager.set(DATA_VISIBLE, Boolean.valueOf(value));
    }

    public boolean isVisible() {
        return dataManager.get(DATA_VISIBLE).booleanValue();
    }

    public void setNoClip(boolean value) {
        dataManager.set(DATA_NO_CLIP, Boolean.valueOf(value));
    }

    public boolean isNoClip() {
        return dataManager.get(DATA_NO_CLIP).booleanValue();
    }

    public void setScale(float value) {
        dataManager.set(DATA_SCALE, Float.valueOf(value));
    }

    public float getScale() {
        return dataManager.get(DATA_SCALE).floatValue();
    }
    
    @Override
    public float getEyeHeight() {
        // TODO Auto-generated method stub
        return super.getEyeHeight() * getScale();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return true;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (hitCount > 80) {
            setDead();
        }
        if (hitCount > 0) {
            hitCount --;
        }
    }
    
    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        float halfWidthScaled = (width / 2F) * getScale();
        AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidthScaled, posY, posZ - halfWidthScaled, posX + halfWidthScaled, posY + (height * getScale()), posZ + halfWidthScaled);
        return bb;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        if (isNoClip()) {
            return null;
        }
        return getEntityBoundingBox();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return getEntityBoundingBox();
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return true;
    }

    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (itemStack.getItem() == ModItems.SOAP) {
            if (!world.isRemote) {
                setDead();
            }
            return EnumActionResult.SUCCESS;
        }
        if (player.isSneaking()) {
            if (!world.isRemote) {
                double angle = TrigUtils.getAngleDegrees(player.posX, player.posZ, posX, posZ) + 90D;
                setRotation((float) angle);
            }
        } else {
            if (itemStack.getItem() != ModItems.MANNEQUIN_TOOL) {
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.getInstance(), EnumGuiId.WARDROBE_ENTITY.ordinal(), getEntityWorld(), getEntityId(), 0, 0);
            }
            
        }
        return EnumActionResult.PASS;
    }
    
    @Override
    public void setDead() {
        if (!isDead) {
            if (!getEntityWorld().isRemote) {
                playSound(SoundEvents.ENTITY_ARMORSTAND_BREAK, 1F, 1F);
                ItemStack itemStack = createStackForEntity();
                entityDropItem(itemStack, 0F);
            }
        }
        super.setDead();
    }
    
    public ItemStack createStackForEntity() {
        return ItemMannequin.create(getTextureData(), getScale());
    }

    @Override
    public boolean hitByEntity(Entity entityIn) {
        if (!getEntityWorld().isRemote) {
            playSound(SoundEvents.ENTITY_ARMORSTAND_HIT, 0.8F, 1F);
            hitCount += 20;
        }
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey(TAG_BIPED_ROTATIONS, NBT.TAG_COMPOUND)) {
            BipedRotations bipedRotations = new BipedRotations();
            bipedRotations.loadNBTData(compound.getCompoundTag(TAG_BIPED_ROTATIONS));
            setBipedRotations(bipedRotations);
        }
        if (compound.hasKey(TAG_TEXTURE_DATA, NBT.TAG_COMPOUND)) {
            TextureData textureData = new TextureData();
            textureData.readFromNBT(compound.getCompoundTag(TAG_TEXTURE_DATA));
            setTextureData(textureData, false);
        }
        if (compound.hasKey(TAG_ROTATION, NBT.TAG_FLOAT)) {
            setRotation(compound.getFloat(TAG_ROTATION));
        }
        if (compound.hasKey(TAG_RENDER_EXTRAS, NBT.TAG_BYTE)) {
            setRenderExtras(compound.getBoolean(TAG_RENDER_EXTRAS));
        }
        if (compound.hasKey(TAG_FLYING, NBT.TAG_BYTE)) {
            setFlying(compound.getBoolean(TAG_FLYING));
        }
        if (compound.hasKey(TAG_VISIBLE, NBT.TAG_BYTE)) {
            setVisible(compound.getBoolean(TAG_VISIBLE));
        }
        if (compound.hasKey(TAG_NO_CLIP, NBT.TAG_BYTE)) {
            setNoClip(compound.getBoolean(TAG_NO_CLIP));
        }
        if (compound.hasKey(TAG_SCALE, NBT.TAG_FLOAT)) {
            setScale(compound.getFloat(TAG_SCALE));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag(TAG_BIPED_ROTATIONS, getBipedRotations().saveNBTData(new NBTTagCompound()));
        compound.setTag(TAG_TEXTURE_DATA, getTextureData().writeToNBT(new NBTTagCompound()));
        compound.setFloat(TAG_ROTATION, getRotation());
        compound.setBoolean(TAG_RENDER_EXTRAS, isRenderExtras());
        compound.setBoolean(TAG_FLYING, isFlying());
        compound.setBoolean(TAG_VISIBLE, isVisible());
        compound.setBoolean(TAG_NO_CLIP, isNoClip());
        compound.setFloat(TAG_SCALE, getScale());
    }

    public static class TextureData {

        private static final String TAG_TEXTURE_TYPE = "texture_type";
        private static final String TAG_PROFILE = "profile";
        private static final String TAG_URL = "url";

        private TextureType textureType = TextureType.NONE;
        private GameProfile profile = null;
        private String url = null;

        public TextureData() {
            textureType = TextureType.NONE;
        }

        public TextureData(GameProfile gameProfile) {
            textureType = TextureType.USER;
            this.profile = gameProfile;
        }

        public TextureData(String url) {
            textureType = TextureType.URL;
            this.url = url;
        }

        public TextureType getTextureType() {
            return textureType;
        }

        public GameProfile getProfile() {
            return profile;
        }

        public String getUrl() {
            return url;
        }

        public void readFromNBT(NBTTagCompound compound) {
            if (compound.hasKey(TAG_TEXTURE_TYPE, NBT.TAG_STRING)) {
                textureType = TextureType.valueOf(compound.getString(TAG_TEXTURE_TYPE));
            } else {
                textureType = TextureType.NONE;
            }
            switch (textureType) {
            case NONE:
                break;
            case USER:
                if (compound.hasKey(TAG_PROFILE, NBT.TAG_COMPOUND)) {
                    profile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag(TAG_PROFILE));
                } else {
                    textureType = TextureType.NONE;
                }
                break;
            case URL:
                if (compound.hasKey(TAG_URL, NBT.TAG_STRING)) {
                    url = compound.getString(TAG_URL);
                } else {
                    textureType = TextureType.NONE;
                }
                break;
            }
        }

        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            switch (textureType) {
            case NONE:
                break;
            case USER:
                if (profile != null) {
                    compound.setTag(TAG_PROFILE, NBTUtil.writeGameProfile(new NBTTagCompound(), profile));
                } else {
                    textureType = TextureType.NONE;
                }
                break;
            case URL:
                if (!StringUtils.isNullOrEmpty(url)) {
                    compound.setString(TAG_URL, url);
                } else {
                    textureType = TextureType.NONE;
                }
                break;
            }
            compound.setString(TAG_TEXTURE_TYPE, textureType.toString());
            return compound;
        }

        public void writeToBuf(ByteBuf buf) {
            ByteBufUtils.writeTag(buf, writeToNBT(new NBTTagCompound()));
        }

        public void readFromBuf(ByteBuf buf) {
            readFromNBT(ByteBufUtils.readTag(buf));
        }

        @Override
        public String toString() {
            return "TextureData [textureType=" + textureType + ", profile=" + profile + ", url=" + url + "]";
        }
    }

    @Override
    public void profileDownloaded(GameProfile gameProfile) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

            @Override
            public void run() {
                setTextureDataProfile(gameProfile);
            }
        });
    }
}
