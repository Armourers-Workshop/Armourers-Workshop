package moe.plushie.armourers_workshop.core.api.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.core.api.ISkinType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface ISkinnableEntity {
    
    /** Return the class of the entity to be skinned. */
    public Class<? extends Entity> getEntityClass();

    // TODO: Refactor
//    /** Return the render class for the entity. */
//    public void addRenderLayer(RenderManager renderManager);
    
    /** Should the wand of style be usable on this entity? */
    public boolean canUseWandOfStyle(PlayerEntity user);
    
    /** Should skins be right click-able on this entity? */
    public boolean canUseSkinsOnEntity();
    
    /** Return a list of skins that are valid for this entity. */
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes);
    
    public int getSlotsForSkinType(ISkinType skinType);
}