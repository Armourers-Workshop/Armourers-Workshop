package riskyken.plushieWrapper.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.plushieWrapper.common.entity.EntityLivingBasePointer;
import riskyken.plushieWrapper.common.entity.EntityPlayerPointer;
import riskyken.plushieWrapper.common.world.BlockLocation;
import riskyken.plushieWrapper.common.world.WorldPointer;

public class ModItemWrapper extends Item {

    private final PlushieItem item;
    private ArrayList<IIcon> itemIcons;
    
    public ModItemWrapper(PlushieItem item) {
        this.item = item;
        this.setUnlocalizedName(item.getName());
    }
    
    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return item.getColorFromItemStack(new ItemStackPointer(stack), pass);
    }
    
    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }
    
    @Override
    public CreativeTabs getCreativeTab() {
        if (item.getCreativeTab() != null) {
            return item.getCreativeTab().getMinecraftCreativeTab();
        }
        return null;
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return item.onItemUse(new ItemStackPointer(stack), new EntityPlayerPointer(player),
                new WorldPointer(world), new BlockLocation(x, y, z), side, hitX, hitY, hitZ);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        return item.onItemRightClick(new ItemStackPointer(stack), new WorldPointer(world), new EntityPlayerPointer(player)).getMinecraftStack();
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
        item.addInformation(new ItemStackPointer(stack), new EntityPlayerPointer(player), list, advancedTooltips);
        super.addInformation(stack, player, list, advancedTooltips);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getModdedUnlocalizedName(super.getUnlocalizedName(stack), new ItemStackPointer(stack));
    }
    
    private String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + item.getModId().toLowerCase() + ":" + name + ".0";
        } else {
            return "item." + item.getModId().toLowerCase() + ":" + name;
        }
    }
    
    private String getModdedUnlocalizedName(String unlocalizedName, ItemStackPointer stack) {
        String name = unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
        if (hasSubtypes) {
            return "item." + item.getModId().toLowerCase() + ":" + name + "." + stack.getItemDamage();
        } else {
            return "item." + item.getModId().toLowerCase() + ":" + name;
        }
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        return item.itemInteractionForEntity(new ItemStackPointer(stack), new EntityPlayerPointer(player),
                new EntityLivingBasePointer(entity));
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        return getIcon(stack, renderPass);
    }
    
    @Override
    public IIcon getIconFromDamage(int damage) {
        return getIconFromDamageForRenderPass(damage, 0);
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return item.requiresMultipleRenderPasses();
    }
    
    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int renderPass) {
        if (itemIcons.size() > 0) {
            return itemIcons.get(0);
        }
        return null;
    }
    
    @Override
    public IIcon getIconIndex(ItemStack stack) {
        return getIcon(stack, 0);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass) {
        int iconIndex = item.getIconIndex(new ItemStackPointer(stack), renderPass);
        if (itemIcons.size() > iconIndex) {
            return itemIcons.get(iconIndex);
        }
        return null;
    }
    
    @Override
    public void registerIcons(IIconRegister register) {
        ArrayList<String> iconList = new ArrayList<String>();
        itemIcons = new ArrayList<IIcon>();
        item.registerIcons(iconList);
        for (int i = 0; i < iconList.size(); i++) {
            itemIcons.add(register.registerIcon(iconList.get(i)));
        }
    }
}
