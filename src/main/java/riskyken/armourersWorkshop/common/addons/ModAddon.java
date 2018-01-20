package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import riskyken.armourersWorkshop.utils.EventState;
import riskyken.armourersWorkshop.utils.ModLogger;


public abstract class ModAddon {
    
    private final String modId;
    private final String modName;
    private final boolean isModLoaded;
    private final ArrayList<String> overrrides;
    
    public ModAddon(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
        this.isModLoaded = setIsModLoaded();
        if (isModLoaded) {
            ModLogger.log(String.format("Loading %s Compatibility Addon", getModName()));
        }
        this.overrrides = new ArrayList<String>();
    }
    
    protected boolean setIsModLoaded() {
        return Loader.isModLoaded(modId);
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
    
    public static enum ItemOverrideType {
        SWORD,
        ITEM,
        PICKAXE,
        AXE,
        SHOVEL,
        HOE,
        BOW
    }
}
