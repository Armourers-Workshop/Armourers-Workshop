package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import com.google.common.collect.Iterators;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOExecutor;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.IntConsumer;

public class ChunkOutputStream implements IOutputStream {

    private final ByteBuf buffer;
    private final DataOutputStream outputStream;

    private final ChunkContext context;

    private ChunkNode headNode;
    private ChunkNode tailNode;

    public ChunkOutputStream(ChunkContext context) {
        this.buffer = Unpooled.buffer(1024);
        this.outputStream = new DataOutputStream(new ByteBufOutputStream(buffer));
        this.context = context;
        this.headNode = new ChunkNode(0);
        this.tailNode = headNode;
    }

    public void writeChunk(IOConsumer<ChunkWriter> consumer) throws IOException {
        writeChunk(ChunkWriter::new, consumer);
    }

    public <C extends ChunkWriter> void writeChunk(Function<ChunkOutputStream, C> provider, IOConsumer<C> consumer) throws IOException {
        consumer.accept(provider.apply(this));
        appendVariable(null);
        writeInt(0);
    }

    public void writeVariable(ChunkVariable variable) throws IOException {
        appendVariable(variable);
    }

    public void transferTo(OutputStream finalStream) throws IOException {
        appendVariable(null);
        // load all variable nodes.
        var node = headNode;
        var pending = new LinkedList<ChunkNode>();
        while (node != null) {
            if (!node.freeze()) {
                pending.add(node);
            }
            node = node.next;
        }
        // freeze all pending variable node.
        var iterator = Iterators.cycle(pending);
        while (iterator.hasNext()) {
            if (iterator.next().freeze()) {
                iterator.remove();
            }
        }
        // write to final stream and destroy the links.
        var bytes = buffer.array();
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

    protected void sumTask(IntConsumer callback, IOExecutor executor) throws IOException {
        var start = appendVariable(null);
        executor.run();
        appendNode(new ChunkNode.Sum(buffer.writerIndex(), start, callback));
    }

    protected void compressTask(ChunkFlags flags, IOExecutor executor) throws IOException {
        if (flags.isEmpty()) {
            executor.run();
            return;
        }
        var start = appendVariable(null);
        appendNode(start);
        executor.run();
        appendNode(new ChunkNode.Compressed(getBuffer().writerIndex(), start, flags, this));
    }

    @Override
    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public ChunkContext getContext() {
        return context;
    }

    protected ByteBuf getBuffer() {
        return buffer;
    }

    protected ChunkNode appendVariable(ChunkVariable var) {
        var node = ChunkNode.of(buffer.writerIndex(), var, this);
        return appendNode(node);
    }

    protected ChunkNode appendNode(ChunkNode node) {
        tailNode.next = node;
        tailNode = node;
        return node;
    }
}

