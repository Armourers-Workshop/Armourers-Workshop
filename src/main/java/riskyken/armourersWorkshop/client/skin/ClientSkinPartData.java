package riskyken.armourersWorkshop.client.skin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.model.SkinModel;
import riskyken.armourersWorkshop.client.model.bake.ColouredVertexWithUV;
import riskyken.armourersWorkshop.common.skin.data.SkinDye;

@SideOnly(Side.CLIENT)
public class ClientSkinPartData {

    /** Blank dye that is used if no dye is applied. */
    public static final SkinDye blankDye = new SkinDye();
    public ArrayList<ColouredVertexWithUV>[] vertexLists;
    public HashMap<ISkinDye, SkinModel> dyeModels;
    public int[] totalCubesInPart;
    
    public ClientSkinPartData() {
        dyeModels = new HashMap<ISkinDye, SkinModel>();
    }
    
    public SkinModel getModelForDye(ISkinDye skinDye) {
        if (skinDye == null) {
            skinDye = blankDye;
        }
        SkinModel skinModel = dyeModels.get(skinDye);
        if (skinModel == null) {
            skinModel = new SkinModel(vertexLists);
            dyeModels.put(skinDye, skinModel);
        }
        return skinModel;
    }
    
    public void cleanUpDisplayLists() {
        Set keys = dyeModels.keySet();
        Iterator<ISkinDye> i = dyeModels.keySet().iterator();
        while (i.hasNext()) {
            ISkinDye skinDye = i.next();
            dyeModels.get(skinDye).cleanUpDisplayLists();
        }
    }

    public void setVertexLists(ArrayList<ColouredVertexWithUV>[] vertexLists) {
        this.vertexLists = vertexLists;
    }
}
