package riskyken.armourersWorkshop.common.addons;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.client.render.item.RenderItemSwordSkin;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.registry.GameRegistry;


public abstract class AbstractAddon {
    
    public abstract void init();
    
    public abstract void initRenderers();
    
    public abstract String getModName();
    
    protected void overrideItemRenderer(String itemName) {
        Item item = GameRegistry.findItem(getModName(), itemName);
        if (item != null) {
            MinecraftForgeClient.registerItemRenderer(item, new RenderItemSwordSkin());
        } else {
            ModLogger.log(Level.WARN, "Unable to override item renderer for: " + getModName() + " - " + itemName);
        }
    }
}
