package moe.plushie.armourers_workshop.common.network;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.client.gui.GuiAdminPanel;
import moe.plushie.armourers_workshop.client.gui.GuiColourMixer;
import moe.plushie.armourers_workshop.client.gui.GuiDebugTool;
import moe.plushie.armourers_workshop.client.gui.GuiDyeTable;
import moe.plushie.armourers_workshop.client.gui.GuiGuideBook;
import moe.plushie.armourers_workshop.client.gui.GuiOutfit;
import moe.plushie.armourers_workshop.client.gui.GuiSkinnable;
import moe.plushie.armourers_workshop.client.gui.GuiSkinningTable;
import moe.plushie.armourers_workshop.client.gui.GuiToolOptions;
import moe.plushie.armourers_workshop.client.gui.armourer.GuiArmourer;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.hologramprojector.GuiHologramProjector;
import moe.plushie.armourers_workshop.client.gui.mannequin.GuiMannequin;
import moe.plushie.armourers_workshop.client.gui.miniarmourer.GuiMiniArmourer;
import moe.plushie.armourers_workshop.client.gui.skinlibrary.GuiSkinLibrary;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinLibrary;
import moe.plushie.armourers_workshop.common.inventory.ContainerArmourer;
import moe.plushie.armourers_workshop.common.inventory.ContainerColourMixer;
import moe.plushie.armourers_workshop.common.inventory.ContainerDyeTable;
import moe.plushie.armourers_workshop.common.inventory.ContainerGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.inventory.ContainerHologramProjector;
import moe.plushie.armourers_workshop.common.inventory.ContainerMannequin;
import moe.plushie.armourers_workshop.common.inventory.ContainerOutfit;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinWardrobe;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinnable;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinningTable;
import moe.plushie.armourers_workshop.common.inventory.ModContainer;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourMixer;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityDyeTable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityHologramProjector;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinningTable;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GuiHandler implements IGuiHandler {

    public GuiHandler() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ArmourersWorkshop.instance, this);
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = null;
        
        if (ID != LibGuiIds.WARDROBE_ENTITY | ID != LibGuiIds.WARDROBE_PLAYER) {
            BlockPos pos = new BlockPos(x, y, z);
            te = world.getTileEntity(pos);
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
                    return new ContainerSkinLibrary(player.inventory, (TileEntitySkinLibrary)te);
                }
                break;
            case LibGuiIds.WARDROBE_PLAYER:
                EntitySkinCapability skinCapabilityPlayer = (EntitySkinCapability) player.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
                IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(player);
                if (skinCapabilityPlayer != null & wardrobeCapability != null) {
                    return new ContainerSkinWardrobe(player.inventory, skinCapabilityPlayer, wardrobeCapability);
                } else {
                    ModLogger.log(Level.WARN, "Error entity not found " + player.getClass());
                }
                break;
            case LibGuiIds.MANNEQUIN:
                if (te instanceof TileEntityMannequin) {
                    return new ContainerMannequin(player.inventory, (TileEntityMannequin)te);
                }
                break;
            case LibGuiIds.WARDROBE_ENTITY:
                Entity entity = player.getEntityWorld().getEntityByID(x);
                if (entity != null) {
                    EntitySkinCapability skinCapability = (EntitySkinCapability) entity.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
                    IWardrobeCap wardrobeCap = WardrobeCap.get(entity);
                    if (skinCapability == null) {
                        wardrobeCap = PlayerWardrobeCap.get(player);
                    }
                    if (skinCapability == null) {
                        break;
                    }
                    if (wardrobeCap == null) {
                        break;
                    }
                    return new ContainerSkinWardrobe(player.inventory, skinCapability, wardrobeCap);
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
                    return new ContainerGlobalSkinLibrary(player.inventory, (TileEntityGlobalSkinLibrary)te);
                }
                break;
            case LibGuiIds.ADMIN_PANEL:
                return new ModContainer(player.inventory);
            case LibGuiIds.SKINNABLE:
                if (te instanceof TileEntitySkinnable) {
                    Skin skin = ((TileEntitySkinnable)te).getSkin(((TileEntitySkinnable)te).getSkinPointer());
                    if (skin != null) {
                        return new ContainerSkinnable(player.inventory, (TileEntitySkinnable)te, skin);
                    }
                }
                break;
            case LibGuiIds.HOLOGRAM_PROJECTOR:
                if (te instanceof TileEntityHologramProjector) {
                    return new ContainerHologramProjector(player.inventory, (TileEntityHologramProjector)te);
                }
                break;
            case LibGuiIds.OUTFIT:
                if (player.getHeldItemMainhand().getItem() == ModItems.outfit) {
                    return new ContainerOutfit(player.inventory, player.getHeldItemMainhand());
                }
                break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = null;
        
        if (ID != LibGuiIds.WARDROBE_ENTITY | ID != LibGuiIds.WARDROBE_PLAYER) {
            BlockPos pos = new BlockPos(x, y, z);
            te = world.getTileEntity(pos);
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
                if (player.getHeldItemMainhand().getItem() == ModItems.guideBook) {
                    return new GuiGuideBook(player.getHeldItemMainhand());
                }
                break;
            case LibGuiIds.ARMOUR_LIBRARY:
                if (te instanceof TileEntitySkinLibrary) {
                    return new GuiSkinLibrary(player.inventory, (TileEntitySkinLibrary)te);
                }
                break;
            case LibGuiIds.WARDROBE_PLAYER:
                EntitySkinCapability skinCapabilityPlayer = (EntitySkinCapability) player.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
                IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(player);
                if (skinCapabilityPlayer != null & wardrobeCapability != null) {
                    return new GuiWardrobe(player.inventory, skinCapabilityPlayer, wardrobeCapability);
                } else {
                    ModLogger.log(Level.WARN, "Error entity not found " + player.getClass());
                }
                break;
            case LibGuiIds.TOOL_OPTIONS:
                if (player.getHeldItemMainhand().getItem() instanceof IConfigurableTool) {
                    return new GuiToolOptions(player.getHeldItemMainhand());
                }
                break;
            case LibGuiIds.MANNEQUIN:
                if (te instanceof TileEntityMannequin) {
                    return new GuiMannequin(player.inventory, (TileEntityMannequin)te);
                }
                break;
            case LibGuiIds.MINI_ARMOURER:
                return new GuiMiniArmourer(player);
            case LibGuiIds.WARDROBE_ENTITY:
                Entity entity = player.getEntityWorld().getEntityByID(x);
                if (entity != null) {
                    EntitySkinCapability skinCapability = (EntitySkinCapability) entity.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
                    IWardrobeCap wardrobeCap = WardrobeCap.get(entity);
                    if (skinCapability == null) {
                        wardrobeCap = PlayerWardrobeCap.get(player);
                    }
                    if (skinCapability == null) {
                        break;
                    }
                    if (wardrobeCap == null) {
                        break;
                    }
                    return new GuiWardrobe(player.inventory, skinCapability, wardrobeCap);
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
                    return new GuiGlobalLibrary((TileEntityGlobalSkinLibrary)te, player.inventory);
                }
                break;
            case LibGuiIds.DEBUG_TOOL:
                return new GuiDebugTool();
            case LibGuiIds.ADMIN_PANEL:
                return new GuiAdminPanel(player);
            case LibGuiIds.SKINNABLE:
                if (te instanceof TileEntitySkinnable) {
                    Skin skin = ((TileEntitySkinnable)te).getSkin(((TileEntitySkinnable)te).getSkinPointer());
                    if (skin != null) {
                        return new GuiSkinnable(player.inventory, (TileEntitySkinnable)te, skin);
                    }
                }
                break;
            case LibGuiIds.HOLOGRAM_PROJECTOR:
                if (te instanceof TileEntityHologramProjector) {
                    return new GuiHologramProjector(player.inventory, (TileEntityHologramProjector)te);
                }
                break;
            case LibGuiIds.OUTFIT:
                if (player.getHeldItemMainhand().getItem() == ModItems.outfit) {
                    return new GuiOutfit(player, player.getHeldItemMainhand());
                }
                break;
        }
        return null;
    }
}
