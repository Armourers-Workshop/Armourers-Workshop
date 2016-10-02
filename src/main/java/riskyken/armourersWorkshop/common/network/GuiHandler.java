package riskyken.armourersWorkshop.common.network;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.GuiArmourLibrary;
import riskyken.armourersWorkshop.client.gui.GuiArmourer;
import riskyken.armourersWorkshop.client.gui.GuiColourMixer;
import riskyken.armourersWorkshop.client.gui.GuiDebugTool;
import riskyken.armourersWorkshop.client.gui.GuiDyeTable;
import riskyken.armourersWorkshop.client.gui.GuiEntityEquipment;
import riskyken.armourersWorkshop.client.gui.GuiGuideBook;
import riskyken.armourersWorkshop.client.gui.GuiSkinWardrobe;
import riskyken.armourersWorkshop.client.gui.GuiSkinningTable;
import riskyken.armourersWorkshop.client.gui.GuiToolOptions;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.mannequin.GuiMannequin;
import riskyken.armourersWorkshop.client.gui.miniarmourer.GuiMiniArmourer;
import riskyken.armourersWorkshop.client.gui.miniarmourer.GuiMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.inventory.ContainerDyeTable;
import riskyken.armourersWorkshop.common.inventory.ContainerEntityEquipment;
import riskyken.armourersWorkshop.common.inventory.ContainerGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.inventory.ContainerMannequin;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourer;
import riskyken.armourersWorkshop.common.inventory.ContainerMiniArmourerBuilding;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinWardrobe;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinningTable;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.common.skin.entity.ExPropsEntityEquipmentData;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityDyeTable;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinningTable;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.plushieWrapper.common.registry.ModRegistry;

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
                if (te instanceof TileEntityColourMixer) {
                    return new ContainerColourMixer(player.inventory, (TileEntityColourMixer)te);
                }
                break;
            case LibGuiIds.ARMOURER:
                if (te instanceof TileEntityArmourer) {
                    return new ContainerArmourer(player.inventory, (TileEntityArmourer)te);
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntitySkinLibrary) {
                    return new ContainerArmourLibrary(player.inventory, (TileEntitySkinLibrary)te);
                }
                break;
            case LibGuiIds.CUSTOM_ARMOUR_INVENTORY:
                ExPropsPlayerEquipmentData customEquipmentData = ExPropsPlayerEquipmentData.get(player);
                return new ContainerSkinWardrobe(player.inventory, customEquipmentData);
            case LibGuiIds.MANNEQUIN:
                if (te instanceof TileEntityMannequin) {
                    return new ContainerMannequin(player.inventory, (TileEntityMannequin)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER:
                if (te instanceof TileEntityMiniArmourer) {
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
                } else {
                    ModLogger.log(Level.WARN, "Error entity not found");
                }
                break;
            case LibGuiIds.SKNNING_TABLE:
                if (te instanceof TileEntitySkinningTable) {
                    return new ContainerSkinningTable(player.inventory, (TileEntitySkinningTable)te);
                }
                break;
            case LibGuiIds.DYE_TABLE:
                if (te instanceof TileEntityDyeTable) {
                    return new ContainerDyeTable(player.inventory, (TileEntityDyeTable)te);
                }
                break;
            case LibGuiIds.GLOBAL_SKIN_LIBRARY:
                if (te instanceof TileEntityGlobalSkinLibrary) {
                    return new ContainerGlobalSkinLibrary((TileEntityGlobalSkinLibrary)te);
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
                if (te instanceof TileEntityColourMixer) {
                    return new GuiColourMixer(player.inventory, (TileEntityColourMixer)te);
                }
                break;
            case LibGuiIds.ARMOURER:
                if (te instanceof TileEntityArmourer) {
                    return new GuiArmourer(player.inventory, (TileEntityArmourer)te);
                }
                break;
            case LibGuiIds.GUIDE_BOOK:
                if (player.getCurrentEquippedItem().getItem() == ModRegistry.getMinecraftItem(ModItems.guideBook)) {
                    return new GuiGuideBook(player.getCurrentEquippedItem());
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntitySkinLibrary) {
                    return new GuiArmourLibrary(player.inventory, (TileEntitySkinLibrary)te);
                }
                break;
            case LibGuiIds.CUSTOM_ARMOUR_INVENTORY:
                ExPropsPlayerEquipmentData customEquipmentData = ExPropsPlayerEquipmentData.get(player);
                return new GuiSkinWardrobe(player.inventory, customEquipmentData);
            case LibGuiIds.TOOL_OPTIONS:
                if (player.getCurrentEquippedItem().getItem() instanceof IConfigurableTool) {
                    return new GuiToolOptions(player.getCurrentEquippedItem());
                }
                break;
            case LibGuiIds.MANNEQUIN:
                if (te instanceof TileEntityMannequin) {
                    return new GuiMannequin(player.inventory, (TileEntityMannequin)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER:
                if (te instanceof TileEntityMiniArmourer) {
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
                } else {
                    ModLogger.log(Level.WARN, "Error entity not found");
                }
                break;
            case LibGuiIds.SKNNING_TABLE:
                if (te instanceof TileEntitySkinningTable) {
                    return new GuiSkinningTable(player.inventory, (TileEntitySkinningTable)te);
                }
                break;
            case LibGuiIds.DYE_TABLE:
                if (te instanceof TileEntityDyeTable) {
                    return new GuiDyeTable(player.inventory, (TileEntityDyeTable)te);
                }
                break;
            case LibGuiIds.GLOBAL_SKIN_LIBRARY:
                if (te instanceof TileEntityGlobalSkinLibrary) {
                    return new GuiGlobalLibrary((TileEntityGlobalSkinLibrary)te);
                }
                break;
            case LibGuiIds.DEBUG_TOOL:
                return new GuiDebugTool();
        }
        return null;
    }
}
