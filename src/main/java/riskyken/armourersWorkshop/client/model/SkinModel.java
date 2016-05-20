package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import riskyken.armourersWorkshop.client.model.bake.ColouredFace;

@SideOnly(Side.CLIENT)
public class SkinModel {
    
    public boolean hasList[];
    public boolean[] displayListCompiled;
    public int[] displayList;
    
    public SkinModel(ArrayList<ColouredFace>[] vertexLists) {
        hasList = new boolean[vertexLists.length];
        displayListCompiled = new boolean[vertexLists.length];
        displayList = new int[vertexLists.length];
        for (int i = 0; i < vertexLists.length; i++) {
            hasList[i] = vertexLists[i].size() > 0;
        }
    }
    
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
