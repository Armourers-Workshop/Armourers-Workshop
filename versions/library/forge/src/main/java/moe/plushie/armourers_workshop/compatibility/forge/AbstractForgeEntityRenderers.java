package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

@Available("[1.18, )")
public interface AbstractForgeEntityRenderers {

    static <T extends Entity> void registerEntity(IRegistryKey<EntityType<T>> entityType, IEntityRendererProvider<T> provider) {
        NotificationCenterImpl.observer(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(entityType.get(), context -> provider.getEntityRenderer(RendererManager.getEntityContext())));
    }

    static <T extends BlockEntity> void registerBlockEntity(IRegistryKey<BlockEntityType<T>> entityType, IBlockEntityRendererProvider<T> provider) {
        NotificationCenterImpl.observer(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerBlockEntityRenderer(entityType.get(), context -> provider.getBlockEntityRenderer(RendererManager.getBlockEntityContext())));
    }
}
