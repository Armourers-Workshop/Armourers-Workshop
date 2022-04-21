package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin implements ISkinDataProvider {

    public Object aw2_skinData;

    @Override
    public <T> void setSkinData(T data) {
        this.aw2_skinData = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSkinData() {
        return (T)aw2_skinData;
    }
}
