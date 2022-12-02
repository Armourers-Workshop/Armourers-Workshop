package moe.plushie.armourers_workshop.compatibility.v19;

import com.mojang.blaze3d.vertex.BufferBuilder;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.init.provider.ClientNativeFactory;
import moe.plushie.armourers_workshop.init.provider.ClientNativeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

@Available("[1.19, )")
public interface ClientNativeProviderExt_V1920 extends ClientNativeProvider, ClientNativeFactory {

    @Override
    default IBufferBuilder createBuilderBuffer(int size) {
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
    default IResourceManager getResourceManager() {
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
