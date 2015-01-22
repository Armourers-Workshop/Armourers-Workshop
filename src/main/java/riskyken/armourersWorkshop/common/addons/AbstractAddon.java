package riskyken.armourersWorkshop.common.addons;

import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.client.render.item.RenderItemBowSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemSwordSkin;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.common.registry.GameRegistry;


public abstract class AbstractAddon {
    
    public abstract void init();
    
    public abstract void initRenderers();
    
    public abstract String getModName();
    
    public abstract void onWeaponRender(ItemRenderType type, EventState state);
    
    protected void overrideItemRenderer(String itemName, RenderType renderType) {
        Item item = GameRegistry.findItem(getModName(), itemName);
        if (item != null) {
            switch (renderType) {
            case SWORD:
                MinecraftForgeClient.registerItemRenderer(item, new RenderItemSwordSkin());
                break;
            case BOW:
                MinecraftForgeClient.registerItemRenderer(item, new RenderItemBowSkin());
                break;
            }
            
        } else {
            ModLogger.log(Level.WARN, "Unable to override item renderer for: " + getModName() + " - " + itemName);
        }
    }
    
    protected void addRenderClass(String className, RenderType renderType) {
        switch (renderType) {
        case SWORD:
            EntityEquipmentDataManager.INSTANCE.addSwordRenderClass(className);
            break;
        case BOW:
            EntityEquipmentDataManager.INSTANCE.addBowRenderClass(className);
            break;
        }
    }
    
    protected enum RenderType {
        SWORD,
        BOW;
    }
}
