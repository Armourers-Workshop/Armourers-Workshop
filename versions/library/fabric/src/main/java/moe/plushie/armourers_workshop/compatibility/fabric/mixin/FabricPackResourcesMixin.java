package moe.plushie.armourers_workshop.compatibility.fabric.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IPackResources;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.21, )")
@Mixin(PackResources.class)
public interface FabricPackResourcesMixin extends IPackResources {

    @Override
    default boolean isModBundled() {
        return this instanceof ModResourcePack;
    }
}
