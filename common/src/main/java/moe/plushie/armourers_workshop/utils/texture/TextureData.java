package moe.plushie.armourers_workshop.utils.texture;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.utils.ObjectUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collection;

public class TextureData implements ITextureProvider {

    private final String name;

    private final float width;
    private final float height;

    private TextureAnimation animation;
    private TextureProperties properties;

    private ByteBuffer bytes;
    private Collection<ITextureProvider> variants;

    public TextureData(String name, float width, float height) {
        this(name, width, height, TextureAnimation.EMPTY, TextureProperties.EMPTY);
    }

    public TextureData(String name, float width, float height, TextureAnimation animation, TextureProperties properties) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.animation = animation;
        this.properties = properties;
    }

    public void load(ByteBuf buf) {
        bytes = ByteBuffer.allocateDirect(buf.readableBytes());
        buf.getBytes(0, bytes);
        bytes.rewind();
    }

    public void load(InputStream inputStream) throws IOException {
        // we have known the length of the file stream, fast copy.
        if (inputStream instanceof FileInputStream fileInputStream) {
            FileChannel fileChannel = fileInputStream.getChannel();
            bytes = ObjectUtils.createByteBuffer((int) fileChannel.size() + 1);
            while (fileChannel.read(bytes) != -1) {
                // ignored.
            }
            bytes.rewind();
            return;
        }

        // when we are lucky enough one load complete,
        // we don't need to merge buffers,
        // this give some a performance boost.
        int capacity = 16384; // 16k
        bytes = ObjectUtils.createByteBuffer(capacity);
        ArrayList<ByteBuffer> buffers = Lists.newArrayList(bytes);
        ReadableByteChannel byteChannel = Channels.newChannel(inputStream);

        // when the channel can't be load at one time,
        // we will split it into multiple buffs to load,
        // and final merge all buffers.
        while (byteChannel.read(bytes) != -1) {
            if (bytes.remaining() != 0) {
                continue;
            }
            // in this case, we required merge buffer, so no longer needs direct buffer.
            bytes = ByteBuffer.allocate(capacity);
            capacity += bytes.capacity();
            buffers.add(bytes);
        }

        // merge all buffers into one buffer
        if (buffers.size() != 1) {
            int total = capacity - bytes.remaining();
            bytes = ObjectUtils.createByteBuffer(total);
            for (ByteBuffer buffer : buffers) {
                buffer.flip();
                bytes.put(buffer);
            }
        }

        bytes.flip();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setAnimation(TextureAnimation animation) {
        this.animation = animation;
    }

    @Override
    public TextureAnimation getAnimation() {
        return animation;
    }

    public void setProperties(TextureProperties properties) {
        this.properties = properties;
    }

    @Override
    public TextureProperties getProperties() {
        return properties;
    }

    @Override
    public ByteBuffer getBuffer() {
        return bytes;
    }

    public void setVariants(Collection<ITextureProvider> variants) {
        this.variants = variants;
    }

    @Override
    public Collection<ITextureProvider> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return ObjectUtils.makeDescription(this, "name", name, "width", width, "height", height, "animation", animation, "properties", properties);
    }
}
