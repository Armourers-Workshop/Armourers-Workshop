package moe.plushie.armourers_workshop.common.init.items;

import moe.plushie.armourers_workshop.client.render.item.RenderItemMannequin;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMannequin extends AbstractModItem {

    public ItemMannequin() {
        super(LibItemNames.MANNEQUIN);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            if (!ConfigHandler.hideDollFromCreativeTabs) {
                items.add(create((TextureData) null, 0.5F));
            }
            items.add(create((TextureData) null, 0.5F));
            items.add(create((TextureData) null, 2F));
        }
    }

    public static ItemStack create(TextureData textureData, float scale) {
        ItemStack itemStack = new ItemStack(ModItems.MANNEQUIN);
        itemStack.setTagCompound(new NBTTagCompound());
        if (textureData != null) {
            NBTTagCompound compoundTextureData = new NBTTagCompound();
            textureData.writeToNBT(compoundTextureData);
            itemStack.getTagCompound().setTag("texture_data", compoundTextureData);
        }
        itemStack.getTagCompound().setFloat("scale", scale);
        return itemStack;
    }

    public static ItemStack create(EntityPlayer player, float scale) {
        if (player != null) {
            return create(new TextureData(player.getGameProfile()), scale);
        }
        return new ItemStack(ModItems.MANNEQUIN);
    }

    public static TextureData getTextureData(ItemStack itemStack) {
        TextureData textureData = new TextureData();
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().hasKey("texture_data", NBT.TAG_COMPOUND)) {
                textureData.readFromNBT(itemStack.getTagCompound().getCompoundTag("texture_data"));
            }
        }
        return textureData;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == EnumFacing.UP) {
            ItemStack itemStack = player.getHeldItem(hand);
            if (!worldIn.isRemote) {
                pos = pos.offset(facing);
                int l = MathHelper.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
                EntityMannequin entityMannequin = new EntityMannequin(worldIn);
                entityMannequin.setPosition(pos.getX() + hitX, pos.getY(), pos.getZ() + hitZ);
                double angle = TrigUtils.getAngleDegrees(player.posX, player.posZ, pos.getX() + 0.5F, pos.getZ() + 0.5F) + 90D;
                entityMannequin.setRotation((float) angle);
                if (itemStack.hasTagCompound()) {
                    NBTTagCompound compound = itemStack.getTagCompound();
                    if (compound.hasKey("scale", NBT.TAG_FLOAT)) {
                        entityMannequin.setScale(compound.getFloat("scale"));
                    }
                    if (compound.hasKey("texture_data", NBT.TAG_COMPOUND)) {
                        TextureData textureData = new TextureData();
                        textureData.readFromNBT(compound.getCompoundTag("texture_data"));
                        entityMannequin.setTextureData(textureData, true);
                    }
                }
                // entityMannequin.setScale(0.25F + worldIn.rand.nextFloat() * 4.45F);
                // entityMannequin.setTextureData(new TextureData(player.getGameProfile()));
                worldIn.spawnEntity(entityMannequin);
                itemStack.shrink(1);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        super.registerModels();
        setTileEntityItemStackRenderer(new RenderItemMannequin());
    }
}
