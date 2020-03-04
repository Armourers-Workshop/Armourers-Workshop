package moe.plushie.armourers_workshop.common.init.entities;

import java.util.Properties;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class EntityMannequin extends Entity {

    BipedRotations rotations;
    TextureData textureData;
    int rotation;
    boolean doll;
    boolean renderExtras;
    boolean flying;
    boolean visable;
    boolean noClip;

    boolean isChild;
    boolean hasCustomHead;

    private Properties properties = new Properties();

    public EntityMannequin(World worldIn) {
        super(worldIn);
        this.setSize(0.8F, 1.9F);
        setPosition(posX, posY, posZ);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return super.getEntityBoundingBox();
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            return true;
        }
        FMLNetworkHandler.openGui(player, ArmourersWorkshop.getInstance(), EnumGuiId.WARDROBE_ENTITY.ordinal(), getEntityWorld(), getEntityId(), 0, 0);
        return false;
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
        return EnumActionResult.PASS;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        // ModLogger.log("tick!");
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    public class TextureData {

        private TextureType textureType = TextureType.USER;
        private GameProfile profile = null;
        private String url = "";
    }
}
