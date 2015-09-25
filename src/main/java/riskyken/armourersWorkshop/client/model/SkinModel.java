package riskyken.armourersWorkshop.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;

@SideOnly(Side.CLIENT)
public class SkinModel {
    
    public boolean hasList[];
    public boolean[] displayListCompiled;
    public int[] displayList;
    
    public void cleanUpDisplayLists() {
        if (hasList != null) {
            for (int i = 0; i < displayList.length; i++) {
                if (hasList[i]) {
                    if (displayListCompiled[i]) {
                        GLAllocation.deleteDisplayLists(displayList[i]);
                    }
                }
            }
        }
    }
}
