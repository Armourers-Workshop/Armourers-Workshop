package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements ISkinDataProvider {

    public Object aw$skinData;

    @Override
    public <T> T getSkinData() {
        return ObjectUtils.unsafeCast(aw$skinData);
    }

    @Override
    public <T> void setSkinData(T data) {
        this.aw$skinData = data;
    }
}