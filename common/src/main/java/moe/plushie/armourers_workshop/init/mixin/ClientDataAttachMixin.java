package moe.plushie.armourers_workshop.init.mixin;

import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityModel.class, EntityRenderer.class})
public abstract class ClientDataAttachMixin implements ISkinDataProvider {

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
