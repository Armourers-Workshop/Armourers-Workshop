package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

@Available("[1.18,)")
public interface AbstractBlockEntityRendererProviderImpl {

    interface Provider<T extends BlockEntity> extends AbstractBlockEntityRendererProviderImpl {

        BlockEntityRenderer<T> create(Context context);

        @Environment(EnvType.CLIENT)
        default BlockEntityRenderer<T> create(BlockEntityRendererProvider.Context context) {
            return create(new Context());
        }
    }

    class Context {
    }
}
