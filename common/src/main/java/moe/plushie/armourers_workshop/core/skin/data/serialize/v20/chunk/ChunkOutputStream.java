package moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk;

import com.google.common.collect.Iterators;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.data.base.IDataOutputStream;
import moe.plushie.armourers_workshop.core.skin.data.serialize.v20.chunk.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.IntConsumer;

public class ChunkOutputStream implements IDataOutputStream {

    private final ByteBuf buffer;
    private final DataOutputStream outputStream;
    private final DataOutputStream finalStream;

    private final ChunkContext context;

    private ChunkNode headNode;
    private ChunkNode tailNode;

    public ChunkOutputStream(DataOutputStream stream, ChunkContext context) {
        this.buffer = Unpooled.buffer(1024);
        this.outputStream = new DataOutputStream(new ByteBufOutputStream(buffer));
        this.context = context;
        this.headNode = new ChunkNode(0);
        this.tailNode = headNode;
        this.finalStream = stream;
    }

    public void writeChunk(ChunkConsumer<ChunkWriter> consumer) throws IOException {
        Total total = new Total();
        writeVariable(total);
        consumer.accept(new ChunkWriter(this, total));
    }

    public void writeVariable(ChunkVariable variable) throws IOException {
        appendVariable(variable);
    }

    public void flush() throws IOException {
        appendVariable(null);
        // load all variable nodes.
        ChunkNode node = headNode;
        LinkedList<ChunkNode> pending = new LinkedList<>();
        while (node != null) {
            if (!node.freeze()) {
                pending.add(node);
            }
            node = node.next;
        }
        // freeze all pending variable node.
        Iterator<ChunkNode> iterator = Iterators.cycle(pending);
        while (iterator.hasNext()) {
            if (iterator.next().freeze()) {
                iterator.remove();
            }
        }
        // write to final stream and destroy the links.
        byte[] bytes = buffer.array();
        node = headNode;
        while (node != null) {
            node.write(bytes, finalStream);
            ChunkNode next = node.next;
            node.next = null;
            node = next;
        }
        headNode = null;
        tailNode = null;
    }

    protected void sumTask(IntConsumer callback, ChunkRunnable runnable) throws IOException {
        ChunkNode start = appendVariable(null);
        runnable.run();
        appendNode(new ChunkNode.Sum(buffer.writerIndex(), start, callback));
    }

    protected void compressTask(int flags, ChunkRunnable runnable) throws IOException {
        if (flags == 0) {
            runnable.run();
            return;
        }
        ChunkNode start = appendVariable(null);
        appendNode(start);
        runnable.run();
        appendNode(new ChunkNode.Compressed(buffer().writerIndex(), start, flags, this));
    }

    @Override
    public DataOutputStream stream() {
        return outputStream;
    }

    protected ChunkContext context() {
        return context;
    }

    protected ByteBuf buffer() {
        return buffer;
    }

    protected ChunkNode appendVariable(ChunkVariable var) {
        ChunkNode node = ChunkNode.of(buffer.writerIndex(), var, this);
        return appendNode(node);
    }

    protected ChunkNode appendNode(ChunkNode node) {
        tailNode.next = node;
        tailNode = node;
        return node;
    }

    protected static class Total implements ChunkVariable, IntConsumer {

        private int value = 0;

        @Override
        public void accept(int value) {
            this.value += value;
        }

        @Override
        public void writeToStream(IDataOutputStream stream) throws IOException {
            stream.writeInt(value);
        }

        @Override
        public boolean freeze() {
            return true;
        }
    }
}

