package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IPackResources;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.21, )")
@Mixin(PackResources.class)
public interface ForgePackResourcesMixin extends IPackResources {

    @Shadow
    String packId();

    @Override
    default boolean isModBundled() {
        return packId().startsWith("mod/");
    }
}
