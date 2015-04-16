package riskyken.armourersWorkshop.common.items;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import org.lwjgl.input.Keyboard;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.client.equipment.ClientEquipmentModelCache;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.equipment.cubes.Cube;
import riskyken.armourersWorkshop.common.equipment.cubes.CubeGlass;
import riskyken.armourersWorkshop.common.equipment.cubes.CubeGlassGlowing;
import riskyken.armourersWorkshop.common.equipment.cubes.CubeGlowing;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEquipmentSkin extends AbstractModItem {

    public ItemEquipmentSkin() {
        super(LibItemNames.EQUIPMENT_SKIN, false);
        setHasSubtypes(true);
    }
    
    public EnumEquipmentType getEquipmentType(ItemStack stack) {
        int damage = stack.getItemDamage() + 1;
        if (damage >= 0 & damage < EnumEquipmentType.values().length) {
            return EnumEquipmentType.values()[damage];
        } else {
            return EnumEquipmentType.NONE;
        }
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 7; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        String cGreen = EnumChatFormatting.GREEN.toString();
        String cGray = EnumChatFormatting.GRAY.toString();
        String cRed = EnumChatFormatting.RED.toString();
        String cGold = EnumChatFormatting.GOLD.toString();
        String cYellow = EnumChatFormatting.YELLOW.toString();
        if (stack.hasTagCompound()) {
            NBTTagCompound itemData = stack.getTagCompound();
            if (itemData.hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
                NBTTagCompound armourData = itemData.getCompoundTag(LibCommonTags.TAG_ARMOUR_DATA);
                if (armourData.hasKey(LibCommonTags.TAG_EQUIPMENT_ID)) {
                    int equipmentId = armourData.getInteger(LibCommonTags.TAG_EQUIPMENT_ID);
                    if (ClientEquipmentModelCache.INSTANCE.isEquipmentInCache(equipmentId)) {
                        CustomEquipmentItemData data = ClientEquipmentModelCache.INSTANCE.getEquipmentItemData(equipmentId);
                        if (!data.getCustomName().trim().isEmpty()) {
                            list.add(cGold + "Name: " + cGray + data.getCustomName());
                        }
                        if (!data.getAuthorName().trim().isEmpty()) {
                            list.add(cGold + "Author: " + cGray + data.getAuthorName());
                        }
                        
                        if (GuiScreen.isShiftKeyDown()) {
                            list.add(cYellow + "Equipment Id: " + cGray + equipmentId);
                            list.add(cYellow + "Total Cubes: " + cGray + data.getTotalCubes());
                            list.add(cYellow + "Cubes: " + cGray + data.getTotalOfCubeType(Cube.class));
                            list.add(cYellow + "Cubes Glowing: " + cGray + data.getTotalOfCubeType(CubeGlowing.class));
                            list.add(cYellow + "Cubes Glass: " + cGray + data.getTotalOfCubeType(CubeGlass.class));
                            list.add(cYellow + "Cubes Glass Glowing: " + cGray + data.getTotalOfCubeType(CubeGlassGlowing.class));
                            
                        } else {
                            list.add("Hold " + cGreen + "shift" + cGray + " for debug info.");
                        }
                    }
                }
            }
            String keyName = Keyboard.getKeyName(Keybindings.openCustomArmourGui.getKeyCode());

            keyName = cGreen + keyName + cGray;
            list.add("Press the " + keyName + " key to open the " + cGreen + "Equipment Wardrobe");
        } else {
            list.add(cRed + "ERROR: Invalid equipment skin.");
            list.add(cRed + "Please delete.");
        }
        

        super.addInformation(stack, player, list, p_77624_4_);
    }
    
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        icons = new IIcon[8];
        icons[0] = register.registerIcon(LibItemResources.TEMPLATE_HEAD);
        icons[1] = register.registerIcon(LibItemResources.TEMPLATE_CHEST);
        icons[2] = register.registerIcon(LibItemResources.TEMPLATE_LEGS);
        icons[3] = register.registerIcon(LibItemResources.TEMPLATE_SKIRT);
        icons[4] = register.registerIcon(LibItemResources.TEMPLATE_FEET);
        icons[5] = register.registerIcon(LibItemResources.TEMPLATE_WEAPON);
        icons[6] = register.registerIcon(LibItemResources.TEMPLATE_BOW);
        icons[7] = register.registerIcon(LibItemResources.TEMPLATE_LOADING);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        int damage = stack.getItemDamage();
        
        if (pass == 1) {
            return icons[7];
        }
        
        if (damage < 7 & damage >= 0) {
            return icons[damage];
        }
        return super.getIcon(stack, pass);
    }
}
