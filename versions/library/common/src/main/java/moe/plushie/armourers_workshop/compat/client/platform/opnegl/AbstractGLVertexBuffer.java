package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IMeshData;
import moe.plushie.armourers_workshop.api.client.IVertexBuffer;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.core.client.buffer.MeshData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public class AbstractGLVertexBuffer extends ReferenceCounted implements IVertexBuffer {

    private static final AbstractGLIndexBuffer INDEXER = new AbstractGLIndexBuffer(4, 6, (builder, index) -> {
        builder.accept(index);
        builder.accept(index + 1);
        builder.accept(index + 2);
        builder.accept(index + 2);
        builder.accept(index + 3);
        builder.accept(index);
    });

    protected int id;
    protected int size = 0;

    public AbstractGLVertexBuffer(ByteBuffer bytes) {
        this.id = GL15.glGenBuffers();
        this.upload(bytes);
    }

    @Override
    public IMeshData slice(int offset, int vertexCount, IVertexFormat format) {
        var reference = new Reference(offset, vertexCount, format);

        // Use direct array drawing without an index buffer for non-quad primitives.
        if (format.mode() != IVertexFormat.Mode.QUADS) {
            reference.invoker = Invoker.DRAW_ARRAYS;
            reference.indexer = null;
        }

        // Fallback to non-VAO rendering if vertex array objects are not supported (OpenGL 2.0+ compatibility).
        if (!AbstractGLCapabilities.GL_ARB_vertex_array_object) {
            reference.binder = Binder.FALLBACK;
        }

        return reference;
    }

    public static void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public void bind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
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

    protected void upload(ByteBuffer byteBuffer) {
        if (id < 0) {
            return;
        }
        size = byteBuffer.limit();
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
        unbind();
    }

    private class Reference extends MeshData implements Runnable {

        private final int offset;

        private Invoker invoker = Invoker.DRAW_ELEMENTS;
        private Binder binder = Binder.VAO;

        private AbstractGLIndexBuffer indexer = INDEXER;

        private int vaoId = -1;

        private Reference(int offset, int vertexCount, IVertexFormat format) {
            super(null, vertexCount, format);
            this.offset = offset;
            // ensure the index capacity contains this slice.
            Objects.ifPresent(indexer, it -> it.ensureCapacity(vertexCount));
        }

        @Override
        protected void init() {
            binder.init(this);
        }

        @Override
        protected void dispose() {
            binder.dispose(this);
        }

        @Override
        public void run() {
            binder.bind(this);
            invoker.draw(0, vertexCount, indexer);
            binder.unbind(this);
        }

        public void bind() {
            AbstractGLVertexBuffer.this.bind();
        }

        public void unbind() {
            AbstractGLVertexBuffer.unbind();
        }

        public AbstractGLIndexBuffer indexer() {
            return indexer;
        }
    }


    private interface Invoker {
        /**
         * Draw object without indexes.
         */
        Invoker DRAW_ARRAYS = (offset, count, indexer) -> GL15.glDrawArrays(GL15.GL_TRIANGLES, 0, count);

        /**
         * Draw object with indexes.
         */
        Invoker DRAW_ELEMENTS = (offset, count, indexer) -> GL15.glDrawElements(GL15.GL_TRIANGLES, indexer.stride(count), indexer.type(), 0);

        /**
         * Renders graphical elements using either native drawing method.
         */
        void draw(int offset, int count, AbstractGLIndexBuffer indexer);
    }

    private interface Binder {
        /**
         * Bind object with VAO.
         * requires OpenGL 3.0+
         */
        Binder VAO = new Binder() {

            @Override
            public void bind(Reference reference) {
                // only bind array object, because the it bind in the init.
                GL30.glBindVertexArray(reference.vaoId);
            }

            @Override
            public void unbind(Reference reference) {
                // nop.
            }

            @Override
            public void init(Reference reference) {
                reference.vaoId = GL30.glGenVertexArrays();

                // in the newer version rendering system, we will use a shader.
                // and shader requires we to split the quad into two triangles,
                // so we need use index buffer to control size of the vertex data.
                GL30.glBindVertexArray(reference.vaoId);

                Objects.ifPresent(reference, Reference::bind);
                Objects.ifPresent(reference.indexer(), AbstractGLIndexBuffer::bind);

                // the vertex offset no longer supported in vanilla,
                // so we need a special version of the format setup.
                Binder.super.bind(reference);

                // unbind the VBO/VAO to prevent accidentally modify VAO.
                GL30.glBindVertexArray(0);
                Objects.ifPresent(reference, Reference::unbind);

                // because the setup state by each format maybe different,
                // so we need to clear state first.
                Binder.super.unbind(reference);
            }

            @Override
            public void dispose(Reference reference) {
                if (reference.vaoId < 0) {
                    return;
                }
                GL30.glDeleteVertexArrays(reference.vaoId);
                reference.vaoId = -1;
            }
        };

        /**
         * Bind object without VAO.
         * requires OpenGL 2.0+
         */
        Binder FALLBACK = new Binder() {

            @Override
            public void bind(Reference reference) {
                Objects.ifPresent(reference, Reference::bind);
                Binder.super.bind(reference);
                Objects.ifPresent(reference.indexer(), AbstractGLIndexBuffer::bind);
            }

            @Override
            public void unbind(Reference reference) {
                Objects.ifPresent(reference.indexer(), AbstractGLIndexBuffer::unbind);
                Binder.super.unbind(reference);
                Objects.ifPresent(reference, Reference::unbind);
            }
        };

        default void bind(Reference reference) {
            AbstractGLVertexBufferImpl.setupBufferState(reference.format(), reference.offset);
        }

        default void unbind(Reference reference) {
            AbstractGLVertexBufferImpl.clearBufferState(reference.format());
        }

        default void init(Reference reference) {
        }

        default void dispose(Reference reference) {
        }
    }
}

