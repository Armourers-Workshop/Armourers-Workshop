package riskyken.plushieWrapper.common.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.plushieWrapper.common.creativetab.ModCreativeTab;
import riskyken.plushieWrapper.common.entity.PlushieEntityLivingBase;
import riskyken.plushieWrapper.common.entity.PlushieEntityPlayer;

public class PlushieItem {
    
    private int maxStackSize = 64; 
    private boolean hasSubtypes;
    private final String name;
    private final String modId;
    private ModCreativeTab creativeTab;
    
    public PlushieItem(String name, String modId) {
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
    public void addInformation(PlushieItemStack stack, PlushieEntityPlayer player, List list, boolean advancedTooltips) {
    }
    
    public String getModId() {
        return modId;
    }
    
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
    
    public int getColorFromItemStack(PlushieItemStack stack, int pass) {
        return 0xFFFFFFFF;
    }
    
    public boolean requiresMultipleRenderPasses() {
        return false;
    }
    
    public boolean onItemUse(PlushieItemStack stack, PlushieEntityPlayer player, World world,
            BlockPos loc, int side, float hitX, float hitY, float hitZ) {
        return false;
    }
    
    public PlushieItemStack onItemRightClick(PlushieItemStack stack, World world, PlushieEntityPlayer player) {
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
    }
    
    @SideOnly(Side.CLIENT)
    public int getIconIndex(PlushieItemStack stack, int pass) {
        return 0;
    }

    public boolean itemInteractionForEntity(PlushieItemStack itemStackPointer,
            PlushieEntityPlayer entityPlayerPointer,
            PlushieEntityLivingBase entityLivingBasePointer) {
        // TODO Auto-generated method stub
        return false;
    }
}
