package moe.plushie.armourers_workshop.api.skin;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * @author RiskyKen
 */
public interface ISkinType {

    int getId();

    /**
     * Gets the name this skin will be registered with.
     * Armourer's Workshop uses the format armourers:skinName.
     * Example armourers:head is the registry name of
     * Armourer's Workshop head armour skin.
     *
     * @return registryName
     */
    ResourceLocation getRegistryName();


    List<? extends ISkinPartType> getParts();
}
