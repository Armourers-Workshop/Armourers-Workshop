package moe.plushie.armourers_workshop.core.render;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.render.other.BakedSkinDye;
import moe.plushie.armourers_workshop.core.render.other.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SkinVertexBufferBuilder {

    protected final Skin skin;
    protected final Cache<SkinCache.Key, ArrayList<CompiledTask>> cachingBuffers = CacheBuilder.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build();

    protected final ArrayList<Pair<SkinRenderTask.Parameter, CompiledTask>> pendingTasks = new ArrayList<>();
    protected final ArrayList<CompiledTask> compilingBuffers = new ArrayList<>();

    public SkinVertexBufferBuilder(Skin skin) {
        this.skin = skin;
    }

    public void addPartData(BakedSkinPart part, BakedSkinDye dye, int light, int partialTicks, MatrixStack matrixStack) {
        // ignore part when part is disable
        if (SkinConfig.isEnableSkinPart(part.getPart())) {
            return;
        }
        SkinCache.Key key = new SkinCache.Key(part.getId(), dye.getRequirements(part.getColorInfo()));
        SkinRenderTask.Parameter parameter = new SkinRenderTask.Parameter(matrixStack, light, partialTicks);
        ArrayList<CompiledTask> compiledTasks = cachingBuffers.getIfPresent(key);
        if (compiledTasks != null) {
            compiledTasks.forEach(compiledTask -> pendingTasks.add(new Pair<>(parameter, compiledTask)));
            return;
        }
        MatrixStack matrixStack1 = new MatrixStack();
        ArrayList<CompiledTask> compiledTasks1 = new ArrayList<>();
        part.forEach((renderType, quads) -> {
            BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
            builder.begin(renderType.mode(), renderType.format());
            for (ColouredFace quad : quads) {
                quad.renderVertex(part, dye, matrixStack1, builder);
            }
            builder.end();
            CompiledTask compiledTask = new CompiledTask(renderType, builder);
            compiledTasks1.add(compiledTask);
            compilingBuffers.add(compiledTask);
            pendingTasks.add(new Pair<>(parameter, compiledTask));
        });
        cachingBuffers.put(key, compiledTasks1);
    }

    public void addShapeData(Rectangle3f box, Color color, MatrixStack matrixStack) {
        RenderUtils.drawBoundingBox(matrixStack, box, color);
    }

    public void endBatch(SkinRenderTask.Group group) {
        if (compilingBuffers.size() != 0) {
            combineAndUpload();
            compilingBuffers.clear();
        }
        if (pendingTasks.size() != 0) {
            pendingTasks.forEach(pair -> group.add(pair.getSecond().build(pair.getFirst())));
            pendingTasks.clear();
        }
    }

    private void combineAndUpload() {
        int totalRenderedBytes = 0;
        SkinVertexBuffer vertexBuffer = new SkinVertexBuffer();
        ArrayList<ByteBuffer> byteBuffers = new ArrayList<>();

        for (CompiledTask compiledTask : compilingBuffers) {
            VertexFormat format = compiledTask.renderType.format();
            Pair<BufferBuilder.DrawState, ByteBuffer> pair = compiledTask.bufferBuilder.popNextBuffer();
            ByteBuffer byteBuffer = pair.getSecond();
            compiledTask.vertexBuffer = vertexBuffer;
            compiledTask.vertexCount = byteBuffer.remaining() / format.getVertexSize();
            compiledTask.vertexOffset = totalRenderedBytes;
            compiledTask.bufferBuilder = null;
            byteBuffers.add(byteBuffer);
            totalRenderedBytes += byteBuffer.remaining();
        }

        ByteBuffer mergedByteBuffer = ByteBuffer.allocateDirect(totalRenderedBytes);
        for (ByteBuffer byteBuffer : byteBuffers) {
            mergedByteBuffer.put(byteBuffer);
        }
        mergedByteBuffer.rewind();
        vertexBuffer.upload(mergedByteBuffer);
    }

    static class CompiledTask {

        final RenderType renderType;
        int vertexCount;
        int vertexOffset;
        BufferBuilder bufferBuilder;
        SkinVertexBuffer vertexBuffer;

        CompiledTask(RenderType renderType, BufferBuilder bufferBuilder) {
            this.renderType = renderType;
            this.bufferBuilder = bufferBuilder;
        }

        SkinRenderTask build(SkinRenderTask.Parameter parameter) {
            return new SkinRenderTask(renderType, vertexBuffer, vertexOffset, vertexCount, parameter);
        }

        void close() {
            vertexBuffer.close();
        }
    }
}
