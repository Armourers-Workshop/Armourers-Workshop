package moe.plushie.armourers_workshop.api.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.client.render.entity.ISkinnableEntityRenderer;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;

public interface ISkinnableEntity {
    
    /** Return the class of the entity to be skinned. */
    public Class<? extends EntityLivingBase> getEntityClass();
    
    /** Return the render class for the entity. */
    @SideOnly(Side.CLIENT)
    public Class<? extends ISkinnableEntityRenderer> getRendererClass();
    
    /** Should the wand of style be usable on this entity? */
    public boolean canUseWandOfStyle();
    
    /** Should skins be right click-able on this entity? */
    public boolean canUseSkinsOnEntity();
    
    /** Return a list of skins that are valid for this entity. */
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes);
}
