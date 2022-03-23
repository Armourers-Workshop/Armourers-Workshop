package moe.plushie.armourers_workshop.core.render.bufferbuilder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.skin.Skin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SkinVertexBufferBuilder2 extends BufferBuilder implements IRenderTypeBuffer {

    public static final RenderType MERGER = new Merger("skin_merger", DefaultVertexFormats.POSITION, GL11.GL_QUADS, 256);

    protected final HashMap<Skin, SkinVertexBufferBuilder> cachingBuilders = new HashMap<>();

    protected final HashMap<Skin, SkinVertexBufferBuilder> pendingBuilders = new HashMap<>();
    protected final HashMap<RenderType, BufferBuilder> pendingBuilders2 = new HashMap<>();

    public SkinVertexBufferBuilder2() {
        super(256);
    }

    public static SkinVertexBufferBuilder2 by(IRenderTypeBuffer buffers) {
        IVertexBuilder builder = buffers.getBuffer(MERGER);
        if (builder instanceof SkinVertexBufferBuilder2) {
            return (SkinVertexBufferBuilder2) builder;
        }
        return new SkinVertexBufferBuilder2();
    }

    public static void clearAllCache() {
        SkinVertexBufferBuilder2 builder = by(Minecraft.getInstance().renderBuffers().bufferSource());
        builder.cachingBuilders.clear();
    }

    @Nonnull
    @Override
    public IVertexBuilder getBuffer(@Nonnull RenderType renderType) {
        BufferBuilder buffer = pendingBuilders2.get(renderType);
        if (buffer != null) {
            return buffer;
        }
        buffer = new BufferBuilder(renderType.bufferSize());
        buffer.begin(renderType.mode(), renderType.format());
        pendingBuilders2.put(renderType, buffer);
        return buffer;
    }

    public SkinVertexBufferBuilder getBuffer(@Nonnull Skin skin) {
        SkinVertexBufferBuilder bufferBuilder = pendingBuilders.get(skin);
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        bufferBuilder = cachingBuilders.computeIfAbsent(skin, SkinVertexBufferBuilder::new);
        pendingBuilders.put(skin, bufferBuilder);
        return bufferBuilder;
    }

    @Override
    public void end() {
        super.end();
        if (pendingBuilders.isEmpty()) {
            return;
        }
        Batch batch = new Batch();
        for (SkinVertexBufferBuilder builder : pendingBuilders.values()) {
            builder.endBatch(batch);
        }
        pendingBuilders.clear();
        batch.end();

        pendingBuilders2.forEach((key, value) -> {
            key.setupRenderState();
            value.end();
            WorldVertexBufferUploader.end(value);
            key.clearRenderState();
        });
        pendingBuilders2.clear();
    }

    public static class Batch {

        private final Map<RenderType, ArrayList<SkinRenderTask>> tasks = new HashMap<>();
        private int maxVertexCount = 0;

        public void add(SkinRenderTask task) {
            tasks.computeIfAbsent(task.getRenderType(), k -> new ArrayList<>()).add(task);
            maxVertexCount = Math.max(maxVertexCount, task.getVertexCount());
        }

        public void end() {
            if (tasks.isEmpty()) {
                return;
            }
            setupRenderState();

            int index = 0;
            for (SkinRenderType renderType : SkinRenderType.RENDER_ORDERING_FACES) {
                ArrayList<SkinRenderTask> pendingTasks = tasks.get(renderType);
                if (pendingTasks == null || pendingTasks.isEmpty()) {
                    continue;
                }
                renderType.setupRenderState();
                for (SkinRenderTask task : pendingTasks) {
                    task.render(renderType, index++, maxVertexCount);
                }
                renderType.clearRenderState();
            }

            clearRenderState();
        }

        private void setupRenderState() {
            RenderSystem.enableRescaleNormal();

            if (AWConfig.enableWireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            }
        }

        private void clearRenderState() {
            if (AWConfig.enableWireframeRender) {
                RenderSystem.polygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }

            RenderSystem.disableRescaleNormal();
            SkinVertexBufferObject.unbind();
        }
    }

    protected static class Merger extends RenderType {

        protected Merger(String name, VertexFormat format, int mode, int bufferSize) {
            super(name, format, mode, bufferSize, false, false, Merger::noop, Merger::noop);
        }

        protected static void noop() {
        }

        @Override
        public void end(BufferBuilder builder, int p_228631_2_, int p_228631_3_, int p_228631_4_) {
            if (builder.building()) {
                builder.end();
            }
        }

        @Override
        public String toString() {
            return "RenderType[Merger[Skin]]";
        }
    }
}
