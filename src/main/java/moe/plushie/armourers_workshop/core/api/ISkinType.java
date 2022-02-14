package moe.plushie.armourers_workshop.core.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
     *
     * @return registryName
     */
    String getRegistryName();


    List<? extends ISkinPartType> getParts();
}
