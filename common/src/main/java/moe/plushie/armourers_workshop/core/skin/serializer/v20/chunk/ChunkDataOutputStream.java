package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOExecutor;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.function.IntConsumer;

public class ChunkDataOutputStream implements ChunkOutputStream {

    private final ByteBuf buffer;
    private final DataOutputStream outputStream;

    private final ChunkContext context;

    private ChunkNode headNode;
    private ChunkNode tailNode;

    public ChunkDataOutputStream(ChunkContext context) {
        this.buffer = Unpooled.buffer(1024);
        this.outputStream = new DataOutputStream(new ByteBufOutputStream(buffer));
        this.context = context;
        this.headNode = new ChunkNode(0);
        this.tailNode = headNode;
    }

    public void writeChunk(IOConsumer<ChunkWriter> consumer) throws IOException {
        writeChunk(ChunkWriter::new, consumer);
    }

    public <C extends ChunkWriter> void writeChunk(Function<ChunkDataOutputStream, C> provider, IOConsumer<C> consumer) throws IOException {
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
        var iterator = Collections.cycle(pending);
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
            var next = node.next;
            node.next = null;
            node = next;
        }
        headNode = null;
        tailNode = null;
    }

    protected void ifTask(ChunkCondition condition, IOExecutor executor) throws IOException {
        switch (condition.result()) {
            case PASS -> executor.run();
            case PENDING -> {
                var start = appendVariable(null);
                executor.run();
                appendNode(new ChunkNode.If(buffer.writerIndex(), start, condition));
            }
        }
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
        executor.run();
        appendNode(new ChunkNode.Compressed(buffer.writerIndex(), start, flags, this));
    }

    @Override
    public DataOutputStream outputStream() {
        return outputStream;
    }

    @Override
    public ChunkContext context() {
        return context;
    }

    protected ByteBuf buffer() {
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

