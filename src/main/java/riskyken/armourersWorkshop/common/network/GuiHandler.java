package riskyken.armourersWorkshop.common.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.GuiArmourLibrary;
import riskyken.armourersWorkshop.client.gui.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.GuiColourMixer;
import riskyken.armourersWorkshop.client.gui.GuiEntityEquipment;
import riskyken.armourersWorkshop.client.gui.GuiEquipmentWardrobe;
import riskyken.armourersWorkshop.client.gui.GuiGuideBook;
import riskyken.armourersWorkshop.client.gui.GuiMannequin;
import riskyken.armourersWorkshop.client.gui.GuiMiniArmourer;
import riskyken.armourersWorkshop.client.gui.GuiMiniArmourerBuilding;
import riskyken.armourersWorkshop.client.gui.GuiToolOptions;
import riskyken.armourersWorkshop.common.equipment.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.equipment.npc.ExPropsEntityEquipmentData;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.inventory.ContainerEntityEquipment;
import riskyken.armourersWorkshop.common.inventory.ContainerEquipmentWardrobe;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.items.ItemGuideBook;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ArmourersWorkshop.instance, this);
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = null;
        if (ID != LibGuiIds.ENTITY_SKIN_INVENTORY) {
            te = world.getTileEntity(x, y, z);
        }
        
        switch (ID)
        {
            case LibGuiIds.COLOUR_MIXER:
                if (te instanceof TileEntityColourMixer){
                    return new ContainerColourMixer(player.inventory, (TileEntityColourMixer)te);
                }
                break;
            case LibGuiIds.ARMOURER:
                if (te instanceof TileEntityArmourerBrain){
                    return new ContainerArmourer(player.inventory, (TileEntityArmourerBrain)te);
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntityArmourLibrary){
                    return new ContainerArmourLibrary(player.inventory, (TileEntityArmourLibrary)te);
                }
                break;
            case LibGuiIds.CUSTOM_ARMOUR_INVENTORY:
                ExPropsPlayerEquipmentData customEquipmentData = ExPropsPlayerEquipmentData.get(player);
                return new ContainerEquipmentWardrobe(player.inventory, customEquipmentData);
            case LibGuiIds.MANNEQUIN:
                if (te instanceof TileEntityMannequin){
                    return new ContainerMannequin(player.inventory, (TileEntityMannequin)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER:
                if (te instanceof TileEntityMiniArmourer){
                    return new ContainerMiniArmourer(player.inventory, (TileEntityMiniArmourer)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER_BUILDING:
                if (te instanceof TileEntityMiniArmourer) {
                    return new ContainerMiniArmourerBuilding((TileEntityMiniArmourer)te);
                }
            case LibGuiIds.ENTITY_SKIN_INVENTORY:
                Entity entity = player.worldObj.getEntityByID(x);
                if (entity != null) {
                    ExPropsEntityEquipmentData entityProps = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
                    if (entityProps == null) {
                        break;
                    }
                    return new ContainerEntityEquipment(player.inventory, entityProps.getSkinInventory());
                }
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = null;
        if (ID != LibGuiIds.ENTITY_SKIN_INVENTORY) {
            te = world.getTileEntity(x, y, z);
        }
        
        switch (ID)
        {
            case LibGuiIds.COLOUR_MIXER:
                if (te instanceof TileEntityColourMixer){
                    return new GuiColourMixer(player.inventory, (TileEntityColourMixer)te);
                }
                break;
            case LibGuiIds.ARMOURER:
                if (te instanceof TileEntityArmourerBrain){
                    return new GuiArmourer(player.inventory, (TileEntityArmourerBrain)te);
                }
                break;
            case LibGuiIds.GUIDE_BOOK:
                if (player.getCurrentEquippedItem().getItem() instanceof ItemGuideBook) {
                    return new GuiGuideBook(player.getCurrentEquippedItem());
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntityArmourLibrary) {
                    return new GuiArmourLibrary(player.inventory, (TileEntityArmourLibrary)te);
                }
                break;
            case LibGuiIds.CUSTOM_ARMOUR_INVENTORY:
                ExPropsPlayerEquipmentData customEquipmentData = ExPropsPlayerEquipmentData.get(player);
                return new GuiEquipmentWardrobe(player.inventory, customEquipmentData);
            case LibGuiIds.TOOL_OPTIONS:
                if (player.getCurrentEquippedItem().getItem() instanceof AbstractModItem) {
                    return new GuiToolOptions(player.getCurrentEquippedItem());
                }
                break;
            case LibGuiIds.MANNEQUIN:
                if (te instanceof TileEntityMannequin) {
                    return new GuiMannequin(player.inventory, (TileEntityMannequin)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER:
                if (te instanceof TileEntityMiniArmourer){
                    return new GuiMiniArmourer(player.inventory, (TileEntityMiniArmourer)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER_BUILDING:
                if (te instanceof TileEntityMiniArmourer) {
                    return new GuiMiniArmourerBuilding((TileEntityMiniArmourer)te);
                }
            case LibGuiIds.ENTITY_SKIN_INVENTORY:
                Entity entity = player.worldObj.getEntityByID(x);
                if (entity != null) {
                    ExPropsEntityEquipmentData entityProps = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
                    if (entityProps == null) {
                        break;
                    }
                    return new GuiEntityEquipment(player.inventory, entityProps.getSkinInventory());
                }
                break;
        }
        return null;
    }
}
