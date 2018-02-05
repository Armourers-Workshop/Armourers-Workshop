package riskyken.armourersWorkshop.api.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;


/**
 * Use to render skins on entities.
 * 
 * NOTE: This class will be constructed by the mod and must have a blank constructor.
 * 
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public interface ISkinnableEntityRenderer {

    
    public void render(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment);
}
