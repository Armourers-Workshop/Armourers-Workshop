package moe.plushie.armourers_workshop.compat.fabric.mixin.core;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.Util;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Available("[16, 26)")
@Mixin(EntityType.Builder.class)
public class FabricEntityTypeBuilderMixin implements IAssociatedContainer {

    private boolean aw2$allowsFetchChoiceType = true;

    @Redirect(method = "build", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;fetchChoiceType(Lcom/mojang/datafixers/DSL$TypeReference;Ljava/lang/String;)Lcom/mojang/datafixers/types/Type;"))
    private Type<?> aw2$fetchChoiceType(DSL.TypeReference type, String name) {
        if (aw2$allowsFetchChoiceType) {
            return Util.fetchChoiceType(type, name);
        }
        return null;
    }

    @Override
    public <T> T getAssociatedObject(Key<T> key) {
        return Objects.unsafeCast(aw2$allowsFetchChoiceType);
    }

    @Override
    public <T> void setAssociatedObject(Key<T> key, T value) {
        aw2$allowsFetchChoiceType = Objects.unsafeCast(value);
    }
}
