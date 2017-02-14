package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;


public abstract class ModAddon {
    
    private final String modId;
    private final String modName;
    private final boolean isModLoaded;
    private final ArrayList<String> overrrides;
    
    public ModAddon(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
        this.isModLoaded = Loader.isModLoaded(modId);
        if (isModLoaded) {
            String.format("Loading %s Compatibility Addon", getModName());
        }
        this.overrrides = new ArrayList<String>();
    }
    
    public void preInit() {}
    
    public void init() {}
    
    public void postInit() {}
    
    public String getModId() {
        return this.modId;
    }
    
    public String getModName() {
        return this.modName;
    }
    
    public boolean isModLoaded() {
        return isModLoaded;
    }
    
    public ArrayList<String> getItemOverrides() {
        return overrrides;
    }
    
    protected void addItemOverride(ItemOverrideType type, String itemName) {
        overrrides.add(type.toString().toLowerCase() + ":" + getModId() + ":" + itemName);
    }
    
    public void onWeaponRender(ItemRenderType type, EventState state) {};
    
    protected static enum ItemOverrideType {
        SWORD,
        ITEM,
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        BOW
    }
}
