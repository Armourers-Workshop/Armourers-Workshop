package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.function.IntConsumer;

@Environment(EnvType.CLIENT)
public class VertexIndexObject {

    private final int id;
    private final int vertexStride;
    private final int indexStride;
    private final IndexGenerator generator;

    private int size;
    private int capacity;
    private IndexType type = IndexType.BYTE;

    public VertexIndexObject(int i, int j, IndexGenerator generator) {
        this.vertexStride = i;
        this.indexStride = j;
        this.generator = generator;
        this.id = GL15.glGenBuffers();
    }

    public void ensureCapacity(int i) {
        if (capacity < i) {
            capacity = i;
        }
    }

    public void bind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
        uploadStorageIfNeeded(capacity);
    }

    public static void unbind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void uploadStorageIfNeeded(int total) {
        if (total <= size) {
            return;
        }
        total = MathUtils.roundToward(total * 2, indexStride);
        ModLog.debug("growing index buffer {} => {}.", size, total);
        var indexType = IndexType.least(total);
        var bufferSize = MathUtils.roundToward(total * indexType.bytes, 4);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferSize, GL15.GL_DYNAMIC_DRAW);
        var buffer = GL15.glMapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, GL15.GL_WRITE_ONLY);
        if (buffer == null) {
            throw new RuntimeException("Failed to map GL buffer");
        }
        type = indexType;
        var builder = createBuilder(buffer);
        for (int k = 0; k < total; k += indexStride) {
            generator.accept(builder, k * vertexStride / indexStride);
        }
        GL15.glUnmapBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER);
        size = total;
    }

    private IntConsumer createBuilder(ByteBuffer buffer) {
        return switch (type) {
            case BYTE -> i -> buffer.put((byte) i);
            case SHORT -> i -> buffer.putShort((short) i);
            default -> buffer::putInt;
        };
    }

    public IndexType type() {
        return this.type;
    }

    public enum IndexType {
        BYTE(GL15.GL_UNSIGNED_BYTE, 1), SHORT(GL15.GL_UNSIGNED_SHORT, 2), INT(GL15.GL_UNSIGNED_INT, 4);

        public final int asGLType;
        public final int bytes;

        IndexType(int type, int bytes) {
            this.asGLType = type;
            this.bytes = bytes;
        }

        public static IndexType least(int i) {
            if ((i & 0xFFFF0000) != 0) {
                return INT;
            }
            if ((i & 0xFF00) != 0) {
                return SHORT;
            }
            return BYTE;
        }
    }

    public interface IndexGenerator {
        void accept(IntConsumer var1, int var2);
    }
}
