package riskyken.plushieWrapper.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;

public class ModCreativeTab {
    
    private final String tabName;
    private CreativeTabs minecraftCreativeTab;
    
    public ModCreativeTab(String tabName) {
        this.tabName = tabName;
    }
    
    public CreativeTabs getMinecraftCreativeTab() {
        return minecraftCreativeTab;
    }
    
    public void setMinecraftCreativeTab(CreativeTabs minecraftCreativeTab) {
        this.minecraftCreativeTab = minecraftCreativeTab;
    }
}
