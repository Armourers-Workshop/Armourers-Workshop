package moe.plushie.armourers_workshop.core.api.common.skin;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RiskyKen
 */
public interface ISkinType {

    /**
     * Gets the name this skin will be registered with.
     * Armourer's Workshop uses the format armourers:skinName.
     * Example armourers:head is the registry name of
     * Armourer's Workshop head armour skin.
     * @return registryName
     */
    String getRegistryName();


    List<? extends ISkinPartType> getParts();
    

    ResourceLocation getIcon();
    
    ResourceLocation getSlotIcon();
    
    /**
     * Should the show skin overlay check box be shown in the armourer and mini armourer.
     * @return
     */
    boolean showSkinOverlayCheckbox();
    
    /**
     * Should the helper check box be shown in the armourer and mini armourer.
     * @return
     */
    boolean showHelperCheckbox();
    
    /**
     * If this skin is for vanilla armour return the slot id here, otherwise return -1.
     * @return slotId
     */
    int getVanillaArmourSlotId();
    
    /**
     * Should this skin be hidden from the user?
     * @return Is hidden?
     */
    boolean isHidden();
    
    /**
     * Is this skin enabled?
     * @return Is enabled?
     */
    boolean enabled();
    
    ArrayList<ISkinProperty<?>> getProperties();

    boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew);
}
