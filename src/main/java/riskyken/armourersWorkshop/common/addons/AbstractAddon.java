package riskyken.armourersWorkshop.common.addons;

import java.util.IdentityHashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.client.render.item.RenderItemBowSkin;
import riskyken.armourersWorkshop.client.render.item.RenderItemSwordSkin;
import riskyken.armourersWorkshop.common.skin.EntityEquipmentDataManager;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;


public abstract class AbstractAddon {
    
    public abstract void init();
    
    public abstract void initRenderers();
    
    public abstract String getModName();
    
    public abstract void onWeaponRender(ItemRenderType type, EventState state);
    
    private static IdentityHashMap<Item, IItemRenderer> customItemRenderers = Maps.newIdentityHashMap();
    
    public static IItemRenderer getItemRenderer(ItemStack item, ItemRenderType type) {
        IItemRenderer renderer = customItemRenderers.get(item.getItem());
        if (renderer != null && renderer.handleRenderType(item, type)) {
            return renderer;
        }
        return null;
    }
    
    private static IItemRenderer getItemRenderer(ItemStack stack) {
        try {
            IdentityHashMap<Item, IItemRenderer> customItemRenderers = null;
            customItemRenderers = ReflectionHelper.getPrivateValue(MinecraftForgeClient.class, null, "customItemRenderers");
            IItemRenderer renderer = customItemRenderers.get(stack.getItem());
            if (renderer != null) {
                return renderer;
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    protected void overrideItemRenderer(String itemName, RenderType renderType) {
        Item item = GameRegistry.findItem(getModName(), itemName);
        if (item != null) {
            ItemStack stack = new ItemStack(item);
            IItemRenderer renderer = getItemRenderer(stack);
            ModLogger.log("got " + renderer + " for item " + itemName);
            if (renderer != null) {
                ModLogger.log("Storing custom item renderer for: " + itemName);
                customItemRenderers.put(item, renderer);
            }
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
