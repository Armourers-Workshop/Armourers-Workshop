package moe.plushie.armourers_workshop.common.entity;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.render.EntityTextureInfo;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityMannequin extends Entity {

    @SideOnly(Side.CLIENT)
    private EntityTextureInfo textureInfo;
    
    private int counter = 0;

    public EntityMannequin(World worldIn) {
        super(worldIn);
        this.setSize(0.8F, 1.9F);
        setPosition(posX, posY, posZ);
        if (worldIn.isRemote) {
            makeTexureInfo();
        }
    }
    
    @SideOnly(Side.CLIENT)
    private void makeTexureInfo() {
        textureInfo = new EntityTextureInfo();
    }
    
    @SideOnly(Side.CLIENT)
    public EntityTextureInfo getTextureInfo() {
        return textureInfo;
    }
    
    @Override
    public boolean canBeCollidedWith() {
        // TODO Auto-generated method stub
        return true;
    }
    
    
    @Override
    public boolean canBeAttackedWithItem() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    protected void entityInit() {
        //this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    public void onEntityUpdate() {
        if (world.isRemote) {
            //return;
        }
        
        //setDead();
        super.onEntityUpdate();
        //this.setPosition(this.posX, this.posY, this.posZ);
    }
    
    @Override
    public void setEntityBoundingBox(AxisAlignedBB bb) {
        // TODO Auto-generated method stub
        super.setEntityBoundingBox(bb);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        // TODO Auto-generated method stub
        return super.getEntityBoundingBox();
    }
    
    @Override
    public AxisAlignedBB getEntityBoundingBox() {

        return super.getEntityBoundingBox();
    }
    
    @Override
    public void applyEntityCollision(Entity entityIn) {
        //ModLogger.log("tick!");
        // TODO Auto-generated method stub
        super.applyEntityCollision(entityIn);
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
        //ModLogger.log("tick!");
        return super.attackEntityFrom(source, amount);
    }
    
    @Override
    public boolean equals(Object p_equals_1_) {
        // TODO Auto-generated method stub
        return super.equals(p_equals_1_);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }
}
