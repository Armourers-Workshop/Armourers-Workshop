package moe.plushie.armourers_workshop.compatibility;

import com.mojang.blaze3d.vertex.BufferBuilder;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

public abstract class AbstractClientNativeImpl implements ClientNativeProvider, ClientNativeFactory {

    @Override
    public <T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> HumanoidArmorLayer<T, M, A> createHumanoidArmorLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context, A innerModel, A outerModel) {
        return new HumanoidArmorLayer<>(renderer, innerModel, outerModel);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> ItemInHandLayer<T, M> createItemInHandLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context) {
        return new ItemInHandLayer<>(renderer, context.getItemInHandRenderer());
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> ElytraLayer<T, M> createElytraLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context) {
        return new ElytraLayer<>(renderer, context.getModelSet());
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & HeadedModel> CustomHeadLayer<T, M> createCustomHeadLayer(LivingEntityRenderer<T, M> renderer, AbstractEntityRendererContext context) {
        return new CustomHeadLayer<>(renderer, context.getModelSet(), context.getItemInHandRenderer());
    }

    @Override
    public IBufferBuilder createBuilderBuffer(int size) {
        BufferBuilder bufferBuilder = new BufferBuilder(size);
        return new IBufferBuilder() {

            @Override
            public BufferBuilder asBufferBuilder() {
                return bufferBuilder;
            }

            @Override
            public IRenderedBuffer end() {
                BufferBuilder.RenderedBuffer buffer = bufferBuilder.end();
                return new IRenderedBuffer() {
                    @Override
                    public ByteBuffer vertexBuffer() {
                        return buffer.vertexBuffer();
                    }

                    @Override
                    public BufferBuilder.DrawState drawState() {
                        return buffer.drawState();
                    }

                    @Override
                    public void release() {
                        buffer.release();
                    }
                };
            }
        };
    }

    @Override
    public IResourceManager getResourceManager() {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        return new IResourceManager() {
            @Override
            public boolean hasResource(ResourceLocation resourceLocation) {
                return resourceManager.getResource(resourceLocation).isPresent();
            }

            @Override
            public InputStream readResource(ResourceLocation resourceLocation) throws IOException {
                Optional<Resource> resource = resourceManager.getResource(resourceLocation);
                if (resource.isPresent()) {
                    return resource.get().open();
                }
                throw new FileNotFoundException(resourceLocation.toString());
            }
        };
    }
}
