package moe.plushie.armourers_workshop.common.init.items;

import java.util.List;

import moe.plushie.armourers_workshop.client.render.item.RenderItemMannequin;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.TrigUtils;
import net.minecraft.client.util.ITooltipFlag;
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

    private static final String TAG_TEXTURE_DATA = "texture_data";
    private static final String TAG_SCALE = "scale";

    public ItemMannequin() {
        super(LibItemNames.MANNEQUIN);
        setSortPriority(199);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            if (!ConfigHandler.hideDollFromCreativeTabs) {
                items.add(create((TextureData) null, 0.5F));
            }
            if (!ConfigHandler.hideGiantFromCreativeTabs) {
                items.add(create((TextureData) null, 2F));
            }
        }
    }

    @Override
    protected String getModdedUnlocalizedName(String unlocalizedName, ItemStack stack) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        String moddedUnlocalizedName = "item." + LibModInfo.ID.toLowerCase() + ":" + name;
        float scale = getScale(stack);
        if (scale <= 0.5F) {
            moddedUnlocalizedName += ".small";
        }
        if (scale >= 2F) {
            moddedUnlocalizedName += ".big";
        }
        return moddedUnlocalizedName;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()) {
            TextureData textureData = getTextureData(stack);
            if (textureData.getTextureType() == TextureType.USER) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.user", textureData.getProfile().getName()));
            }
            if (textureData.getTextureType() == TextureType.URL) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.url", textureData.getUrl()));
            }
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static ItemStack create(TextureData textureData, float scale) {
        ItemStack itemStack = new ItemStack(ModItems.MANNEQUIN);
        itemStack.setTagCompound(new NBTTagCompound());
        if (textureData != null) {
            setTextureData(itemStack, textureData);
        }
        setScale(itemStack, scale);
        return itemStack;
    }

    public static ItemStack create(EntityPlayer player, float scale) {
        if (player != null) {
            return create(new TextureData(player.getGameProfile()), scale);
        }
        return new ItemStack(ModItems.MANNEQUIN);
    }

    public static void setTextureData(ItemStack itemStack, TextureData textureData) {
        if (itemStack.isEmpty()) {
            return;
        }
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound compoundTextureData = new NBTTagCompound();
        textureData.writeToNBT(compoundTextureData);
        itemStack.getTagCompound().setTag(TAG_TEXTURE_DATA, compoundTextureData);
    }

    public static TextureData getTextureData(ItemStack itemStack) {
        TextureData textureData = new TextureData();
        if (itemStack.hasTagCompound()) {
            if (itemStack.getTagCompound().hasKey(TAG_TEXTURE_DATA, NBT.TAG_COMPOUND)) {
                textureData.readFromNBT(itemStack.getTagCompound().getCompoundTag(TAG_TEXTURE_DATA));
            }
        }
        return textureData;
    }

    public static void setScale(ItemStack itemStack, float scale) {
        if (itemStack.isEmpty()) {
            return;
        }
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        itemStack.getTagCompound().setFloat(TAG_SCALE, scale);
    }

    public static float getScale(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (itemStack.hasTagCompound()) {
                if (itemStack.getTagCompound().hasKey(TAG_SCALE, NBT.TAG_FLOAT)) {
                    return itemStack.getTagCompound().getFloat(TAG_SCALE);
                }
            }
        }
        return 1F;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == EnumFacing.UP) {
            ItemStack itemStack = player.getHeldItem(hand);
            if (!worldIn.isRemote) {
                pos = pos.offset(facing);
                EntityMannequin entityMannequin = new EntityMannequin(worldIn);
                if (player.isSneaking()) {
                    int l = MathHelper.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
                    entityMannequin.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                    entityMannequin.setRotation(l * 22.5F + 180F);
                } else {
                    entityMannequin.setPosition(pos.getX() + hitX, pos.getY(), pos.getZ() + hitZ);
                    double angle = TrigUtils.getAngleDegrees(player.posX, player.posZ, pos.getX() + hitX, pos.getZ() + hitZ) + 90D;
                    entityMannequin.setRotation((float) angle);
                }
                if (itemStack.hasTagCompound()) {
                    TextureData textureData = getTextureData(itemStack);
                    float scale = getScale(itemStack);
                    entityMannequin.setTextureData(textureData, true);
                    entityMannequin.setScale(scale);
                }
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
