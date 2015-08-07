package riskyken.armourersWorkshop.common.painting;

import java.awt.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public final class PaintingHelper {
    
    public static boolean getToolHasColour(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey(LibCommonTags.TAG_COLOUR)) {
            return true;
        }
        return false;
    }
    
    public static int getToolColour(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey(LibCommonTags.TAG_COLOUR)) {
            return compound.getInteger(LibCommonTags.TAG_COLOUR);
        }
        return 0xFFFFFFFF;
    }
    
    public static void setToolColour(ItemStack stack, int colour) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound == null) {
            compound = new NBTTagCompound();
        }
        compound.setInteger(LibCommonTags.TAG_COLOUR, colour);
        stack.setTagCompound(compound);
    }
    
    public static PaintType getPaintTypeFromTool(ItemStack stack) {
        int colour = getToolColour(stack);
        return PaintType.getPaintTypeFromColour(colour);
    }
    
    public static void setPaintTypeOnTool(ItemStack stack, PaintType paintType) {
        int colour = getToolColour(stack);
        int newColour = PaintType.setPaintTypeOnColour(paintType, colour);
        setToolColour(stack, newColour);
    }
    
    @SideOnly(Side.CLIENT)
    public static int getLoadPlayersSkinColour() {
        PlayerPointer playerPointer = new PlayerPointer(Minecraft.getMinecraft().thePlayer);
        EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        if (ewd != null) {
            return ewd.skinColour;
        }
        return Color.decode("#F9DFD2").getRGB();
    }
    
    @SideOnly(Side.CLIENT)
    public static int getLocalPlayersHairColour() {
        PlayerPointer playerPointer = new PlayerPointer(Minecraft.getMinecraft().thePlayer);
        EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        if (ewd != null) {
            return ewd.hairColour;
        }
        return Color.decode("#804020").getRGB();
    }
}
