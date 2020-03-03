package moe.plushie.armourers_workshop.common.network;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.client.gui.GuiAdminPanel;
import moe.plushie.armourers_workshop.client.gui.GuiDebugTool;
import moe.plushie.armourers_workshop.client.gui.GuiGuideBook;
import moe.plushie.armourers_workshop.client.gui.GuiToolOptions;
import moe.plushie.armourers_workshop.client.gui.miniarmourer.GuiMiniArmourer;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinWardrobe;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.inventory.ModContainer;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
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
        NetworkRegistry.INSTANCE.registerGuiHandler(ArmourersWorkshop.getInstance(), this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID < 0 & ID > EnumGuiId.values().length) {
            return null;
        }
        EnumGuiId guiId = EnumGuiId.values()[ID];
        if (guiId.isTile()) {
            BlockPos pos = new BlockPos(x, y, z);
            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (te != null && te instanceof IGuiFactory) {
                    return ((IGuiFactory) te).getServerGuiElement(player, world, pos);
                }
            }
            return null;
        }

        switch (guiId) {
        case WARDROBE_PLAYER:
            EntitySkinCapability skinCapabilityPlayer = (EntitySkinCapability) player.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
            IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(player);
            if (skinCapabilityPlayer != null & wardrobeCapability != null) {
                return new ContainerSkinWardrobe(player.inventory, skinCapabilityPlayer, wardrobeCapability);
            } else {
                ModLogger.log(Level.WARN, "Error entity not found " + player.getClass());
            }
            break;
        case WARDROBE_ENTITY:
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
        case ADMIN_PANEL:
            return new ModContainer(player.inventory);
        default:
            break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID < 0 & ID > EnumGuiId.values().length) {
            return null;
        }
        EnumGuiId guiId = EnumGuiId.values()[ID];
        if (guiId.isTile()) {
            BlockPos pos = new BlockPos(x, y, z);
            if (world.isBlockLoaded(pos)) {
                TileEntity te = world.getTileEntity(pos);
                if (te != null && te instanceof IGuiFactory) {
                    return ((IGuiFactory) te).getClientGuiElement(player, world, pos);
                }
            }
            return null;
        }

        switch (guiId) {
        case GUIDE_BOOK:
            if (player.getHeldItemMainhand().getItem() == ModItems.GUIDE_BOOK) {
                return new GuiGuideBook(player.getHeldItemMainhand());
            }
            break;
        case WARDROBE_PLAYER:
            EntitySkinCapability skinCapabilityPlayer = (EntitySkinCapability) player.getCapability(EntitySkinCapability.ENTITY_SKIN_CAP, null);
            IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get(player);
            if (skinCapabilityPlayer != null & wardrobeCapability != null) {
                return new GuiWardrobe(player.inventory, skinCapabilityPlayer, wardrobeCapability);
            } else {
                ModLogger.log(Level.WARN, "Error entity not found " + player.getClass());
            }
            break;
        case TOOL_OPTIONS:
            if (player.getHeldItemMainhand().getItem() instanceof IConfigurableTool) {
                return new GuiToolOptions(player.getHeldItemMainhand());
            }
            break;
        case WARDROBE_ENTITY:
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
        case DEBUG_TOOL:
            return new GuiDebugTool();
        case ADMIN_PANEL:
            return new GuiAdminPanel(player);
        case MINI_ARMOURER:
            return new GuiMiniArmourer(player);
        default:
            break;
        }
        return null;
    }
}
