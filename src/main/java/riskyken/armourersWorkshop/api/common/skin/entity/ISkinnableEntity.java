package riskyken.armourersWorkshop.api.common.skin.entity;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public interface ISkinnableEntity {
    
    public Class<? extends EntityLivingBase> getEntityClass();
    
    @SideOnly(Side.CLIENT)
    public Class<? extends ISkinnableEntityRenderer> getRendererClass();
    
    public boolean canUseWandOfStyle();
    
    public boolean canUseSkinsOnEntity();
    
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes);
}
