package moe.plushie.armourers_workshop.core.api.client.render.entity;

import net.minecraft.entity.LivingEntity;


/**
 * Use to render skins on entities.
 * 
 * NOTE: This class will be constructed by the mod and must have a blank constructor.
 * 
 * @author RiskyKen
 *
 */
public interface ISkinnableEntityRenderer<E extends LivingEntity> {

    
    //public void render(ENTITY entity, RendererLivingEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment);
}
