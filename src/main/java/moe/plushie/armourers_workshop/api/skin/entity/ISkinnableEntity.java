package moe.plushie.armourers_workshop.api.skin.entity;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;

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
