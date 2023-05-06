package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.18, )")
@Mixin(Item.class)
public abstract class ForgeItemRendererMixin implements ISkinDataProvider {

    @Shadow(remap = false)
    private Object renderProperties;

    @Override
    public <T> T getSkinData() {
        return ObjectUtils.unsafeCast(renderProperties);
    }

    @Override
    public <T> void setSkinData(T data) {
        renderProperties = data;
    }
}
