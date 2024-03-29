package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.data.IAssociatedObjectProvider;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.18, )")
@Mixin(Item.class)
public abstract class ForgeItemRendererMixin implements IAssociatedObjectProvider {

    @Shadow(remap = false)
    private Object renderProperties;

    @Override
    public <T> T getAssociatedObject() {
        // noinspection unchecked
        return (T) renderProperties;
    }

    @Override
    public <T> void setAssociatedObject(T data) {
        renderProperties = data;
    }
}
