package moe.plushie.armourers_workshop.init.mixin;


import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements ISkinDataProvider {

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
