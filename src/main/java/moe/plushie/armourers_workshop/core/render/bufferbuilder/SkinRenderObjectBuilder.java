package moe.plushie.armourers_workshop.core.render.bufferbuilder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.core.cache.SkinCache;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.utils.Rectangle3f;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.color.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class SkinRenderObjectBuilder {

    protected final Skin skin;
    protected final Cache<Object, ArrayList<CompiledTask>> cachingTasks = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.SECONDS)
            .build();

    protected final ArrayList<CompiledPass> pendingTasks = new ArrayList<>();
    protected final ArrayList<CompiledTask> buildingTasks = new ArrayList<>();

    public SkinRenderObjectBuilder(Skin skin) {
        this.skin = skin;
    }

    public void addPartData(BakedSkinPart part, ColorScheme scheme, int light, float partialTicks, MatrixStack matrixStack, boolean shouldRender) {
        Object key = SkinCache.borrowKey(part.getId(), part.requirements(scheme));
        ArrayList<CompiledTask> compiledTasks = cachingTasks.getIfPresent(key);
        if (compiledTasks != null) {
            SkinCache.returnKey(key);
            if (shouldRender) {
                compiledTasks.forEach(compiledTask -> pendingTasks.add(new CompiledPass(compiledTask, matrixStack, light, partialTicks)));
            }
            return;
        }
        MatrixStack matrixStack1 = new MatrixStack();
        ArrayList<CompiledTask> mergedTasks = new ArrayList<>();
        part.forEach((renderType, quads) -> {
            BufferBuilder builder = new BufferBuilder(quads.size() * 8 * renderType.format().getVertexSize());
            builder.begin(renderType.mode(), renderType.format());
            quads.forEach(quad -> quad.render(part, scheme, matrixStack1, builder));
            builder.end();
            CompiledTask compiledTask = new CompiledTask(renderType, builder, part.getRenderPolygonOffset());
            mergedTasks.add(compiledTask);
            buildingTasks.add(compiledTask);
            if (shouldRender) {
                pendingTasks.add(new CompiledPass(compiledTask, matrixStack, light, partialTicks));
            }
        });
        cachingTasks.put(key, mergedTasks);
    }

    public void addShapeData(Vector3f origin, MatrixStack matrixStack) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderUtils.drawBoundingBox(matrixStack, box, color, SkinRenderBuffer.getInstance());
        RenderUtils.drawPoint(matrixStack, origin, 2, buffer);
    }

    public void addShapeData(Rectangle3f box, Color color, MatrixStack matrixStack) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderUtils.drawPoint(matrixStack, null, box.getWidth(), box.getHeight(), box.getDepth(), buffer);
        RenderUtils.drawBoundingBox(matrixStack, box, color, buffer);
    }

    public void endBatch(SkinVertexBufferBuilder.Pipeline pipeline) {
        if (buildingTasks.size() != 0) {
            combineAndUpload();
            buildingTasks.clear();
        }
        if (pendingTasks.size() != 0) {
            pendingTasks.forEach(pipeline::add);
            pendingTasks.clear();
        }
    }

    private void combineAndUpload() {
        int totalRenderedBytes = 0;
        SkinRenderObject vertexBuffer = new SkinRenderObject();
        ArrayList<ByteBuffer> byteBuffers = new ArrayList<>();

        for (CompiledTask compiledTask : buildingTasks) {
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

        final float polygonOffset;
        final RenderType renderType;
        int vertexCount;
        int vertexOffset;
        BufferBuilder bufferBuilder;
        SkinRenderObject vertexBuffer;

        CompiledTask(RenderType renderType, BufferBuilder bufferBuilder, float polygonOffset) {
            this.renderType = renderType;
            this.bufferBuilder = bufferBuilder;
            this.polygonOffset = polygonOffset;
        }

        void close() {
            vertexBuffer.close();
        }
    }

    static class CompiledPass extends SkinVertexBufferBuilder.Pass {

        int lightmap;
        float partialTicks;

        Matrix4f matrix;
        CompiledTask compiledTask;

        CompiledPass(CompiledTask compiledTask, MatrixStack matrixStack, int lightmap, float partialTicks) {
            super();
            this.compiledTask = compiledTask;
            this.matrix = matrixStack.last().pose().copy();
            this.lightmap = lightmap;
            this.partialTicks = partialTicks;
        }

        @Override
        public RenderType getRenderType() {
            return compiledTask.renderType;
        }

        @Override
        public int getVertexOffset() {
            return compiledTask.vertexOffset;
        }

        @Override
        public int getVertexCount() {
            return compiledTask.vertexCount;
        }

        @Override
        public SkinRenderObject getVertexBuffer() {
            return compiledTask.vertexBuffer;
        }

        @Override
        public float getPolygonOffset() {
            return compiledTask.polygonOffset;
        }

        @Override
        public Matrix4f getMatrix() {
            return matrix;
        }

        @Override
        public int getLightmap() {
            return lightmap;
        }
    }
}
