package moe.plushie.armourers_workshop.api.client.render.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Use to render skins on entities.
 * 
 * NOTE: This class will be constructed by the mod and must have a blank constructor.
 * 
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public interface ISkinnableEntityRenderer<ENTITY extends EntityLivingBase> {

    
    //public void render(ENTITY entity, RendererLivingEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment);
}
