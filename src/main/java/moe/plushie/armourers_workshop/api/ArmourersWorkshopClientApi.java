package moe.plushie.armourers_workshop.api;

import moe.plushie.armourers_workshop.api.client.render.ISkinRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmourersWorkshopClientApi {
    
    public static ISkinRenderHandler skinRenderHandler;
    
    private ArmourersWorkshopClientApi() {
        throw new IllegalAccessError();
    }
    
    public static ISkinRenderHandler getSkinRenderHandler() {
        return skinRenderHandler;
    }
}
