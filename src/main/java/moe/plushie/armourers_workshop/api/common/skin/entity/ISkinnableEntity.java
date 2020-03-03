package moe.plushie.armourers_workshop.api.common.skin.entity;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISkinnableEntity {
    
    /** Return the class of the entity to be skinned. */
    public Class<? extends Entity> getEntityClass();
    
    /** Return the render class for the entity. */
    @SideOnly(Side.CLIENT)
    public void addRenderLayer(RenderManager renderManager);
    
    /** Should the wand of style be usable on this entity? */
    public boolean canUseWandOfStyle(EntityPlayer user);
    
    /** Should skins be right click-able on this entity? */
    public boolean canUseSkinsOnEntity();
    
    /** Return a list of skins that are valid for this entity. */
    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes);
    
    public int getSlotsForSkinType(ISkinType skinType);
}
