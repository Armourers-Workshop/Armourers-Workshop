package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.IntConsumer;

public class ChunkNode {

    public ChunkNode next;

    public int index;

    public ChunkNode(int index) {
        this.index = index;
    }

    public static ChunkNode of(int index, ChunkVariable var, ChunkDataOutputStream stream) {
        if (var != null) {
            return new Variable(index, var, stream);
        }
        return new ChunkNode(index);
    }

    public void write(byte[] bytes, OutputStream stream) throws IOException {
        if (next != null && next.index != index) {
            stream.write(bytes, index, next.index - index);
        }
    }

    public int length() throws IOException {
        int length = 0;
        if (next != null) {
            length += next.index - index;
        }
        return length;
    }

    public boolean freeze() throws IOException {
        return true;
    }

    public boolean resolved() {
        return true;
    }

    private static boolean freezeRange(ChunkNode start, ChunkNode end) throws IOException {
        var flag = true;
        var node = start;
        while (node != null && node != end) {
            if (!node.freeze()) {
                flag = false;
            }
            node = node.next;
        }
        return flag;
    }

    public static class Variable extends ChunkNode {

        private final ChunkVariable var;
        private ChunkDataOutputStream stream;

        private int varStart;
        private int varEnd;
        private boolean resolved;

        public Variable(int index, ChunkVariable var, ChunkDataOutputStream stream) {
            super(index);
            this.var = var;
            this.stream = stream;
        }

        @Override
        public void write(byte[] bytes, OutputStream stream) throws IOException {
            if (var != null && varStart != varEnd) {
                stream.write(bytes, varStart, varEnd - varStart);
            }
            super.write(bytes, stream);
        }

        @Override
        public int length() throws IOException {
            int length = super.length();
            if (var != null) {
                length += varEnd - varStart;
            }
            return length;
        }

        @Override
        public boolean freeze() throws IOException {
            if (var != null) {
                if (var.freeze()) {
                    resolved = true;
                    updateIfNeeded();
                    return true;
                }
                return false;
            }
            return super.freeze();
        }

        @Override
        public boolean resolved() {
            if (var != null) {
                return resolved;
            }
            return super.resolved();
        }

        private void updateIfNeeded() throws IOException {
            if (stream != null && var != null) {
                varStart = stream.buffer().writerIndex();
                var.writeToStream(stream);
                varEnd = stream.buffer().writerIndex();
                stream = null;
            }
        }
    }

    public static class If extends ChunkNode {

        private final ChunkNode start;
        private final ChunkCondition condition;

        public If(int index, ChunkNode start, ChunkCondition condition) {
            super(index);
            this.start = start;
            this.condition = condition;
        }

        @Override
        public void write(byte[] bytes, OutputStream stream) throws IOException {
            // nope
        }

        @Override
        public int length() throws IOException {
            return 0;
        }

        @Override
        public boolean freeze() throws IOException {
            if (freezeRange(start, this) && condition.result() != ChunkConditionResult.PENDING) {
                updateIfNeeded();
                return true;
            }
            return false;
        }

        private void updateIfNeeded() throws IOException {
            // check the condition result, the result failure we need ignore all node.
            if (condition.result() != ChunkConditionResult.FAILURE) {
                return;
            }
            // reset the node info.
            start.next = this;
            index = start.index; // yep, the start node can't write any contents.
        }
    }

    public static class Sum extends ChunkNode {

        private final ChunkNode start;
        private IntConsumer callback;

        public Sum(int index, ChunkNode start, IntConsumer callback) {
            super(index);
            this.start = start;
            this.callback = callback;
        }

        @Override
        public boolean freeze() throws IOException {
            if (freezeRange(start, this)) {
                updateIfNeeded();
                return true;
            }
            return false;
        }

        private void updateIfNeeded() throws IOException {
            if (callback != null) {
                callback.accept(estimatedLength());
                callback = null;
            }
        }

        private int estimatedLength() throws IOException {
            var length = 0;
            var node = start;
            while (node != null) {
                length += node.length();
                if (node == this) {
                    break;
                }
                node = node.next;
            }
            return length;
        }
    }

    public static class Compressed extends ChunkNode {

        private final ChunkFlags flags;
        private final ChunkNode start;

        private int length = 0;
        private ByteBuf buf;
        private ChunkDataOutputStream stream;

        public Compressed(int index, ChunkNode start, ChunkFlags flags, ChunkDataOutputStream stream) {
            super(index);
            this.start = start;
            this.flags = flags;
            this.stream = stream;
        }

        @Override
        public void write(byte[] bytes, OutputStream stream) throws IOException {
            stream.write(buf.array(), 0, length);
        }

        @Override
        public int length() throws IOException {
            return length;
        }

        @Override
        public boolean freeze() throws IOException {
            if (freezeRange(start, this)) {
                updateIfNeeded();
                return true;
            }
            return false;
        }

        private void updateIfNeeded() throws IOException {
            if (start == null || stream == null) {
                return;
            }
            buf = Unpooled.buffer(1024);
            var outputStream = stream.context().createOutputStream(buf, flags);
            var bytes = stream.buffer().array();
            var node = start;
            while (node != null && node != this) {
                node.write(bytes, outputStream);
                var next = node.next;
                node.next = null;
                node = next;
            }
            outputStream.close();
            stream = null;
            // reset the node info.
            length = buf.writerIndex();
            start.next = this;
            index = start.index; // yep, the start node can't write any contents.
        }
    }
}
