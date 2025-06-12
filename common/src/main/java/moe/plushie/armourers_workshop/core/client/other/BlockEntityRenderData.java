package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.client.skinrender.patch.BlockEntityRenderPatch;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderData extends EntitySlotsHandler<BlockEntity> {

    private Object customTextureProvider;
    private BlockEntityRenderPatch<? super BlockEntity> renderPatch;

    public BlockEntityRenderData(BlockEntity blockEntity) {
        super(blockEntity, new BlockEntityProvider(), new WardrobeProvider());
        this.renderPatch = new BlockEntityRenderPatch<>(blockEntity);
        this.renderPatch.renderingContext().setAnimationManager(animationManager());
    }

    @Nullable
    public static BlockEntityRenderData of(@Nullable BlockEntity entity) {
        if (entity != null) {
            return EntityDataStorage.of(entity).renderData().orElse(null);
        }
        return null;
    }

    public void tick(BlockEntity blockEntity) {
        tick(blockEntity, null);
    }


    public BlockEntityRenderPatch<? super BlockEntity> renderPatch() {
        return renderPatch;
    }

    public void setCustomTextureProvider(Object customTextureProvider) {
        this.customTextureProvider = customTextureProvider;
    }

    public Object customTextureProvider() {
        return customTextureProvider;
    }
}
