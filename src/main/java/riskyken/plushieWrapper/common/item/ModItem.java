package riskyken.plushieWrapper.common.item;

import java.util.ArrayList;
import java.util.List;

import riskyken.plushieWrapper.common.creativetab.ModCreativeTab;
import riskyken.plushieWrapper.common.entity.EntityLivingBasePointer;
import riskyken.plushieWrapper.common.entity.EntityPlayerPointer;
import riskyken.plushieWrapper.common.world.BlockLocation;
import riskyken.plushieWrapper.common.world.WorldPointer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModItem {
    
    private int maxStackSize = 64; 
    private boolean hasSubtypes;
    private final String name;
    private final String modId;
    private ModCreativeTab creativeTab;
    
    public ModItem(String name, String modId) {
        this.name = name;
        this.modId = modId;
    }
    
    public ModCreativeTab getCreativeTab() {
        return creativeTab;
    }
    
    public void setCreativeTab(ModCreativeTab creativeTab) {
        this.creativeTab = creativeTab;
    }
    
    public String getName() {
        return name;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStackPointer stack, EntityPlayerPointer player, List list, boolean advancedTooltips) {
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
    
    public int getColorFromItemStack(ItemStackPointer stack, int pass) {
        return 0xFFFFFFFF;
    }
    
    public boolean requiresMultipleRenderPasses() {
        return false;
    }
    
    public boolean onItemUse(ItemStackPointer stack, EntityPlayerPointer player, WorldPointer world,
            BlockLocation blockLocation, int side, float hitX, float hitY, float hitZ) {
        return false;
    }
    
    public ItemStackPointer onItemRightClick(ItemStackPointer stack, WorldPointer world, EntityPlayerPointer player) {
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
    }
    
    @SideOnly(Side.CLIENT)
    public int getIconIndex(ItemStackPointer stack, int pass) {
        return 0;
    }

    public boolean itemInteractionForEntity(ItemStackPointer itemStackPointer,
            EntityPlayerPointer entityPlayerPointer,
            EntityLivingBasePointer entityLivingBasePointer) {
        // TODO Auto-generated method stub
        return false;
    }
}
