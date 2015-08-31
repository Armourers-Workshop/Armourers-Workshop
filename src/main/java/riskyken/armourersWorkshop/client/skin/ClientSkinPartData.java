package riskyken.armourersWorkshop.client.skin;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GLAllocation;
import riskyken.armourersWorkshop.client.model.bake.ColouredVertexWithUV;

@SideOnly(Side.CLIENT)
public class ClientSkinPartData {

    public ArrayList<ColouredVertexWithUV>[] vertexLists;
    public boolean hasList[];
    public int[] totalCubesInPart;
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

    public void setVertexLists(ArrayList<ColouredVertexWithUV>[] vertexLists) {
        hasList = new boolean[vertexLists.length];
        displayListCompiled = new boolean[vertexLists.length];
        displayList = new int[vertexLists.length];
        this.vertexLists = vertexLists;
        for (int i = 0; i < vertexLists.length; i++) {
            hasList[i] = vertexLists[i].size() > 0;
        }
    }
}
