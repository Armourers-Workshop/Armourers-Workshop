package moe.plushie.armourers_workshop.common.addons;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.common.init.items.ItemArmourersHammer;
import moe.plushie.armourers_workshop.common.init.items.ItemSkin;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class AddonBuildCraft extends ModAddon {
    
    public AddonBuildCraft() {
        super("BuildCraft|Core", "BuildCraft");
    }
    
    @Override
    public void init() {
        if (!isModLoaded()) {
            return;
        }
        ModLogger.log("Buildcraft detected! - Enabling knishes support.");
    }
    
    public boolean isSkinCompatibleVersion() {
        if (isModLoaded()) {
            ModContainer mc = Loader.instance().getIndexedModList().get(getModId());
            if (mc != null) {
                String version = mc.getVersion();
                String[] versionSplit = version.split("\\.");
                try {
                    int majorVersion = Integer.parseInt(versionSplit[0]);
                    if (majorVersion > 6) {
                        ModLogger.log("BuildCraft robot skin support active.");
                        return true;
                    } else {
                        ModLogger.log("BuildCraft is out of date. Unable to active robot skin support.");
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }
    
    private static Item setEquipmentSkinType() {
        if (ModAddonManager.addonBuildCraft.isSkinCompatibleVersion()) {
            try {
                Class<?> c = Class.forName("riskyken.armourers_workshop.common.items.ItemSkinRobotOverlay");
                Object classObject = c.newInstance();
                if (classObject instanceof ItemSkin) {
                    return (ItemSkin)classObject;
                }
            } catch (Exception e) {
                ModLogger.log(Level.WARN, "Failed to load BuildCraft skinned item.");
                e.printStackTrace();
            }
        }
        return new ItemSkin();
    }
    
    /*@Optional.Interface(iface = "buildcraft.api.robots.IRobotOverlayItem", modid = "BuildCraft|Core")*/
    public class ItemSkinRobotOverlay extends ItemSkin /*implements IRobotOverlayItem*/ {
        /*
        @Optional.Method(modid = "BuildCraft|Core")
        @Override
        public boolean isValidRobotOverlay(ItemStack stack) {
            if (!SkinNBTHelper.stackHasSkinData(stack)) {
                return false;
            }
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (skinPointer.getIdentifier().getSkinType() != SkinTypeRegistry.skinHead) {
                return false;
            }
            return true;
        }
        
        @Optional.Method(modid = "BuildCraft|Core")
        @SideOnly(Side.CLIENT)
        @Override
        public void renderRobotOverlay(ItemStack stack, TextureManager textureManager) {
            GL11.glPushMatrix();
            GL11.glScalef(1.0125F, 1.0125F, 1.0125F);
            GL11.glTranslatef(0.0F, -0.25F, 0.0F);
            GL11.glRotatef(180F, 0F, 0F, 1F);
            GL11.glRotatef(-90F, 0F, 1F, 0F);
            EquipmentRenderHandler.INSTANCE.renderSkinWithHelper(stack);
            GL11.glPopMatrix();
        }
        */
    }
    
    /*@Optional.Interface(iface = "buildcraft.api.tools.IToolWrench", modid = "BuildCraft|Core")*/
    public class ItemArmourersHammerToolWrench extends ItemArmourersHammer /*implements IToolWrench*/ {
        /*
        @Optional.Method(modid = "BuildCraft|Core")
        @Override
        public boolean canWrench(EntityPlayer player, int x, int y, int z) {
            return true;
        }
        
        @Optional.Method(modid = "BuildCraft|Core")
        @Override
        public void wrenchUsed(EntityPlayer player, int x, int y, int z) {
            player.swingItem();
        }
        */
    }
}
