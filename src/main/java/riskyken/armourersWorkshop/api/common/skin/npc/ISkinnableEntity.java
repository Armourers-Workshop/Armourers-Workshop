package riskyken.armourersWorkshop.api.common.skin.npc;

import riskyken.armourersWorkshop.api.client.render.npc.ISkinnableEntityRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISkinnableEntity {

    public String getEntityFullClassPath();
    
    @SideOnly(Side.CLIENT)
    public abstract Class<? extends ISkinnableEntityRenderer> getRendererClass();
}
