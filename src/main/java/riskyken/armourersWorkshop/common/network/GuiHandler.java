package riskyken.armourersWorkshop.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.GuiArmourLibrary;
import riskyken.armourersWorkshop.client.gui.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.GuiColourMixer;
import riskyken.armourersWorkshop.client.gui.GuiEquipmentWardrobe;
import riskyken.armourersWorkshop.client.gui.GuiGuideBook;
import riskyken.armourersWorkshop.common.custom.equipment.PlayerCustomEquipmentData;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.inventory.ContainerEquipmentWardrobe;
import riskyken.armourersWorkshop.common.items.ItemGuideBook;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ArmourersWorkshop.instance, this);
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        switch (ID)
        {
            case LibGuiIds.COLOUR_MIXER:
                if (te instanceof TileEntityColourMixer){
                    return new ContainerColourMixer(player.inventory,(TileEntityColourMixer)te);
                }
                break;
            case LibGuiIds.ARMOURER:
                if (te instanceof TileEntityArmourerBrain){
                    return new ContainerArmourer(player.inventory,(TileEntityArmourerBrain)te);
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntityArmourLibrary){
                    return new ContainerArmourLibrary(player.inventory,(TileEntityArmourLibrary)te);
                }
                break;
            case LibGuiIds.CUSTOM_ARMOUR_INVENTORY:
                PlayerCustomEquipmentData customEquipmentData = PlayerCustomEquipmentData.get(player);
                return new ContainerEquipmentWardrobe(player.inventory, customEquipmentData);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        switch (ID)
        {
            case LibGuiIds.COLOUR_MIXER:
                if (te instanceof TileEntityColourMixer){
                    return new GuiColourMixer(player.inventory,(TileEntityColourMixer)te);
                }
                break;
            case LibGuiIds.ARMOURER:
                if (te instanceof TileEntityArmourerBrain){
                    return new GuiArmourer(player.inventory,(TileEntityArmourerBrain)te);
                }
                break;
            case LibGuiIds.GUIDE_BOOK:
                if (player.getCurrentEquippedItem().getItem() instanceof ItemGuideBook) {
                    return new GuiGuideBook(player.getCurrentEquippedItem());
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntityArmourLibrary) {
                    return new GuiArmourLibrary(player.inventory,(TileEntityArmourLibrary)te);
                }
                break;
            case LibGuiIds.CUSTOM_ARMOUR_INVENTORY:
                PlayerCustomEquipmentData customEquipmentData = PlayerCustomEquipmentData.get(player);
                return new GuiEquipmentWardrobe(player.inventory, customEquipmentData);
        }
        return null;
    }

}
