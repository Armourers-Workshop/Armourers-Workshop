package moe.plushie.armourers_workshop.compatibility.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProviderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public abstract class AbstractBlockEntityRendererImpl<T extends BlockEntity> implements BlockEntityRenderer<T>, AbstractBlockEntityRendererProviderImpl {

    public AbstractBlockEntityRendererImpl(Context context) {
    }
}
