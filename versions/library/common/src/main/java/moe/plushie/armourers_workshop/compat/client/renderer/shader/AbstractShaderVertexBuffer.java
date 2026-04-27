package moe.plushie.armourers_workshop.compat.client.renderer.shader;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.shader.ShaderVertexBuffer;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModLog;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.function.IntConsumer;

@Available("[16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractShaderVertexBuffer extends ShaderVertexBuffer {

    private static final VertexIndexSlice INDEXER = new VertexIndexSlice(4, 6, (builder, index) -> {
        builder.accept(index);
        builder.accept(index + 1);
        builder.accept(index + 2);
        builder.accept(index + 2);
        builder.accept(index + 3);
        builder.accept(index);
    });

    protected int id;
    protected int size = 0;

    public AbstractShaderVertexBuffer() {
        this.id = GL15.glGenBuffers();
    }

    public static void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void bind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
    }

    @Override
    public void upload(ByteBuffer byteBuffer) {
        if (id < 0) {
            return;
        }
        size = byteBuffer.limit();
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
        unbind();
    }

    @Override
    public Slice slice(int offset, int count, IVertexFormat.Mode mode, IVertexFormat format) {
        var parent = create(offset, count, mode, format);
        return switch (mode) {
            case QUADS -> new DrawElementSlice(parent);
            default -> new DrawArraySlice(parent);
        };
    }

    @Override
    protected void init() {
        // nop
    }

    @Override
    protected void dispose() {
        if (id < 0) {
            return;
        }
        GL15.glDeleteBuffers(id);
        id = -1;
    }

    private ObjectSlice create(int offset, int count, IVertexFormat.Mode mode, IVertexFormat format) {
        if (AbstractShaderCapabilities.GL_ARB_vertex_array_object) {
            return new VertexArraySlice(offset, count, mode, format);
        }
        return new NormalSlice(offset, count, mode, format);
    }


    public static class ObjectSlice extends ReferenceCounted implements Slice {

        protected final int offset;
        protected final int count;
        protected final IVertexFormat.Mode mode;
        protected final IVertexFormat format;

        public ObjectSlice(int offset, int count, IVertexFormat.Mode mode, IVertexFormat format) {
            this.offset = offset;
            this.count = count;
            this.mode = mode;
            this.format = format;
        }

        public void setupBufferState(@Nullable VertexIndexSlice indexer) {
        }

        public void clearBufferState(@Nullable VertexIndexSlice indexer) {
        }

        @Override
        public int offset() {
            return offset;
        }

        @Override
        public int count() {
            return count;
        }

        @Override
        public IVertexFormat.Mode mode() {
            return mode;
        }

        @Override
        public IVertexFormat format() {
            return format;
        }
    }

    /**
     * Bind object without VAO.
     */
    public class NormalSlice extends ObjectSlice {

        public NormalSlice(int offset, int count, IVertexFormat.Mode mode, IVertexFormat format) {
            super(offset, count, mode, format);
        }

        @Override
        public void setupBufferState(@Nullable VertexIndexSlice indexer) {
            AbstractShaderVertexBuffer.this.bind();
            format.setupBufferState(offset);
            if (indexer != null) {
                indexer.bind();
            }
        }

        @Override
        public void clearBufferState(@Nullable VertexIndexSlice indexer) {
            format.clearBufferState();
        }
    }

    /**
     * Bind object with VAO.
     * requires OpenGL 3.0+
     */
    public class VertexArraySlice extends ObjectSlice {

        private int id = -1;

        public VertexArraySlice(int offset, int count, IVertexFormat.Mode mode, IVertexFormat format) {
            super(offset, count, mode, format);
        }

        public static void unbind() {
            GL30.glBindVertexArray(0);
        }

        @Override
        public void setupBufferState(@Nullable VertexIndexSlice indexer) {
            // create vertex array object if needed.
            if (id < 0) {
                init(indexer);
            }
            GL30.glBindVertexArray(id);
        }

        @Override
        public void clearBufferState(@Nullable VertexIndexSlice indexer) {
            // nop
        }

        protected void init(@Nullable VertexIndexSlice indexer) {
            id = GL30.glGenVertexArrays();

            // in the newer version rendering system, we will use a shader.
            // and shader requires we to split the quad into two triangles,
            // so we need use index buffer to control size of the vertex data.
            GL30.glBindVertexArray(id);
            AbstractShaderVertexBuffer.this.bind();
            if (indexer != null) {
                indexer.bind();
            }

            // the vertex offset no longer supported in vanilla,
            // so we need a special version of the format setup.
            format.setupBufferState(offset);

            // unbind the VBO/VAO to prevent accidentally modify VAO.
            VertexArraySlice.unbind();
            AbstractShaderVertexBuffer.unbind();

            // because the setup state by each format maybe different,
            // so we need to clear state first.
            format.clearBufferState();
        }

        @Override
        protected void dispose() {
            if (id < 0) {
                return;
            }
            GL30.glDeleteVertexArrays(id);
            id = -1;
        }
    }

    /**
     * Bind index object.
     * requires OpenGL 2.0+
     */
    public static class VertexIndexSlice {

        private final int id;
        private final int vertexStride;
        private final int indexStride;
        private final Generator generator;

        private int size;
        private int capacity;
        private Type type = Type.BYTE;

        public VertexIndexSlice(int vertexStride, int indexStride, Generator generator) {
            this.vertexStride = vertexStride;
            this.indexStride = indexStride;
            this.generator = generator;
            this.id = GL15.glGenBuffers();
        }

        public static void unbind() {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        public void bind() {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
            uploadStorageIfNeeded(capacity);
        }

        public void ensureCapacity(int i) {
            if (capacity < i) {
                capacity = i;
            }
        }

        private void uploadStorageIfNeeded(int total) {
            if (total <= size) {
                return;
            }
            total = OpenMath.roundToward(total * 2, indexStride);
            ModLog.debug("growing index buffer {} => {}.", size, total);
            var indexType = Type.least(total);
            var bufferSize = OpenMath.roundToward(total * indexType.bytes, 4);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferSize, GL15.GL_DYNAMIC_DRAW);
            var buffer = GL15.glMapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_WRITE_ONLY);
            if (buffer == null) {
                throw new RuntimeException("Failed to map GL buffer");
            }
            type = indexType;
            var builder = indexType.builder(buffer);
            for (int k = 0; k < total; k += indexStride) {
                generator.accept(builder, k * vertexStride / indexStride);
            }
            GL15.glUnmapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER);
            size = total;
        }

        public int stride(int count) {
            return (count / vertexStride) * indexStride;
        }

        public int type() {
            return type.asGLType;
        }

        public enum Type {
            BYTE(GL15.GL_UNSIGNED_BYTE, 1), SHORT(GL15.GL_UNSIGNED_SHORT, 2), INT(GL15.GL_UNSIGNED_INT, 4);

            public final int asGLType;
            public final int bytes;

            Type(int type, int bytes) {
                this.asGLType = type;
                this.bytes = bytes;
            }

            public static Type least(int i) {
                if ((i & 0xFFFF0000) != 0) {
                    return INT;
                }
                if ((i & 0xFF00) != 0) {
                    return SHORT;
                }
                return BYTE;
            }

            public IntConsumer builder(ByteBuffer buffer) {
                return switch (this) {
                    case BYTE -> i -> buffer.put((byte) i);
                    case SHORT -> i -> buffer.putShort((short) i);
                    default -> buffer::putInt;
                };
            }
        }

        public interface Generator {
            void accept(IntConsumer var1, int var2);
        }
    }

    /**
     * Draw object without indexes.
     */
    public static class DrawArraySlice implements Slice {

        private final ObjectSlice parent;

        public DrawArraySlice(ObjectSlice parent) {
            this.parent = parent;
        }

        @Override
        public void draw() {
            parent.setupBufferState(null);
            GL15.glDrawArrays(GL15.GL_TRIANGLES, 0, count());
            parent.clearBufferState(null);
        }

        @Override
        public int offset() {
            return parent.offset();
        }

        @Override
        public int count() {
            return parent.count();
        }

        @Override
        public IVertexFormat.Mode mode() {
            return parent.mode();
        }

        @Override
        public IVertexFormat format() {
            return parent.format();
        }

        @Override
        public void retain() {
            parent.retain();
        }

        @Override
        public void release() {
            parent.release();
        }
    }

    /**
     * Draw object with indexes.
     */
    public static class DrawElementSlice implements Slice {

        private final ObjectSlice parent;
        private final VertexIndexSlice indexer;

        public DrawElementSlice(ObjectSlice parent) {
            this.parent = parent;
            this.indexer = INDEXER;
            // ensure the index capacity contains this slice.
            this.indexer.ensureCapacity(parent.count());
        }

        @Override
        public void draw() {
            parent.setupBufferState(indexer);
            GL15.glDrawElements(GL15.GL_TRIANGLES, indexer.stride(count()), indexer.type(), 0);
            parent.clearBufferState(indexer);
        }

        @Override
        public int offset() {
            return parent.offset();
        }

        @Override
        public int count() {
            return parent.count();
        }

        @Override
        public IVertexFormat.Mode mode() {
            return parent.mode();
        }

        @Override
        public IVertexFormat format() {
            return parent.format();
        }

        @Override
        public void retain() {
            parent.retain();
        }

        @Override
        public void release() {
            parent.release();
        }
    }
}
