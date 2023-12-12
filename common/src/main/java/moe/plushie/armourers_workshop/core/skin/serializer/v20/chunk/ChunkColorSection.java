package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.api.common.ITextureProvider;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import moe.plushie.armourers_workshop.utils.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.texture.TextureData;
import moe.plushie.armourers_workshop.utils.texture.TextureProperties;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;

public abstract class ChunkColorSection {

    private final static byte[] BUFFER = new byte[8];

    protected int index = 0;
    protected int total = 0;
    protected int colorIndexBytes = 1;
    protected int textureIndexBytes = 4;
    protected boolean resolved = false;

    protected final int usedBytes; // 0 is texture
    protected final ISkinPaintType paintType;

    public ChunkColorSection(int count, int colorBytes, ISkinPaintType paintType) {
        this.total = count;
        this.usedBytes = colorBytes;
        this.paintType = paintType;
    }

    public abstract void writeToStream(IOutputStream stream) throws IOException;

    public void freeze(int index) {
        this.index = index;
        this.resolved = true;
    }

    public void freezeIndex(int colorUsedIndex, int textureUsedIndex) {
        this.colorIndexBytes = colorUsedIndex;
        this.textureIndexBytes = textureUsedIndex;
    }

    public abstract IPaintColor getColor(int index);

    public TextureRef getTexture(Vector2f pos) {
        TextureList list = getTextureList(pos);
        if (list != null) {
            return list.get(pos, this);
        }
        return null;
    }

    protected abstract TextureList getTextureList(Vector2f pos);

    public boolean isResolved() {
        return resolved;
    }

    public int getStartIndex() {
        return index;
    }

    public int getEndIndex() {
        return index + total;
    }

    public int getTotal() {
        return total;
    }

    public int getUsedBytes() {
        return usedBytes;
    }

    public ISkinPaintType getPaintType() {
        return paintType;
    }

    private static int _readFixedInt(int usedBytes, IInputStream stream) throws IOException {
        stream.read(BUFFER, 0, usedBytes);
        return _readFixedInt(usedBytes, 0, BUFFER);
    }

    private static int _readFixedInt(int usedBytes, int offset, byte[] bytes) {
        int value = 0;
        for (int i = 0; i < usedBytes; ++i) {
            value = (value << 8) | (bytes[offset + i] & 0xff);
        }
        return value;
    }

    private static void _writeFixedInt(int value, int usedBytes, IOutputStream stream) throws IOException {
        if (usedBytes == 4) {
            stream.writeInt(value);
            return;
        }
        BUFFER[0] = (byte) (value >>> 24);
        BUFFER[1] = (byte) (value >>> 16);
        BUFFER[2] = (byte) (value >>> 8);
        BUFFER[3] = (byte) (value >>> 0);
        stream.write(BUFFER, 4 - usedBytes, usedBytes);
    }

    private static float _readFixedFloat(int usedBytes, int offset, byte[] bytes) {
        return Float.intBitsToFloat(_readFixedInt(usedBytes, offset, bytes));
    }

    private static void _writeFixedFloat(float value, int usedBytes, IOutputStream stream) throws IOException {
        _writeFixedInt(Float.floatToIntBits(value), usedBytes, stream);
    }

    public static class Immutable extends ChunkColorSection {

        private byte[] buffers;
        private TextureList[] textureLists;

        public Immutable(int total, int usedBytes, ISkinPaintType paintType) {
            super(total, usedBytes, paintType);
        }

        public void readFromStream(IInputStream stream) throws IOException {
            if (usedBytes != 0) {
                buffers = new byte[usedBytes * total];
                stream.read(buffers);
            }
            if (usedBytes == 0) {
                textureLists = new TextureList[total];
                for (int i = 0; i < total; ++i) {
                    TextureList list = new TextureList();
                    list.readFromStream(stream);
                    textureLists[i] = list;
                }
                // restore the parent -> child.
                for (TextureList parent : textureLists) {
                    for (TextureList child : textureLists) {
                        if (parent.id == child.parentId) {
                            Collection<ITextureProvider> variants = parent.provider.getVariants();
                            if (variants == null) {
                                variants = new ArrayList<>();
                                if (parent.provider instanceof TextureData) {
                                    ((TextureData) parent.provider).setVariants(variants);
                                }
                            }
                            variants.add(child.provider);
                        }
                    }
                }
            }
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            if (buffers != null) {
                stream.write(buffers);
            }
            if (textureLists != null) {
                for (TextureList list : textureLists) {
                    list.writeToStream(stream);
                }
            }
        }

        @Override
        public IPaintColor getColor(int offset) {
            int value = 0;
            for (int i = 0; i < usedBytes; ++i) {
                value = (value << 8) | (buffers[offset * usedBytes + i]) & 0xff;
            }
            return PaintColor.of(value, getPaintType());
        }

        @Override
        public TextureList getTextureList(Vector2f pos) {
            if (textureLists == null) {
                return null;
            }
            for (TextureList list : textureLists) {
                if (list.contains(pos)) {
                    return list;
                }
            }
            return null;
        }
    }

    public static class Mutable extends ChunkColorSection {

        private final ArrayList<Integer> colorLists = new ArrayList<>();

        private final LinkedHashMap<Integer, ColorRef> indexes = new LinkedHashMap<>();
        private final LinkedHashMap<ITextureProvider, TextureList> textureLists = new LinkedHashMap<>();

        public Mutable(int colorBytes, ISkinPaintType paintType) {
            super(0, colorBytes, paintType);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            for (int color : colorLists) {
                _writeFixedInt(color, usedBytes, stream);
            }
            for (TextureList list : textureLists.values()) {
                list.writeToStream(stream);
            }
        }

        @Override
        public void freeze(int index) {
            float x = 0;
            float y = 0;
            for (TextureList list : textureLists.values()) {
                list.freeze(x, y, textureLists::get);
                x += list.rect.getWidth() * 2;
            }
            total = colorLists.size() + textureLists.size();
            super.freeze(index);
        }

        @Override
        public IPaintColor getColor(int offset) {
            int value = colorLists.get(offset);
            return PaintColor.of(value, getPaintType());
        }

        public ColorRef putColor(int value) {
            // if the transparent channel not used, clear it.
            if (usedBytes == 3) {
                value |= 0xff000000;
            }
            return indexes.computeIfAbsent(value, k -> {
                ColorRef ref = new ColorRef(this, colorLists.size());
                colorLists.add(k);
                return ref;
            });
        }

        public TextureRef putTexture(Vector2f uv, ITextureProvider provider) {
            // we're also adding all variant textures.
            TextureList textureList = getOrCreateTextureList(provider);
            if (provider.getVariants() != null) {
                provider.getVariants().forEach(this::getOrCreateTextureList);
            }
            return textureList.add(uv, this);
        }

        @Override
        protected TextureList getTextureList(Vector2f pos) {
            for (TextureList list : textureLists.values()) {
                if (list.contains(pos)) {
                    return list;
                }
            }
            return null;
        }

        protected TextureList getOrCreateTextureList(ITextureProvider provider) {
            // ..
            return textureLists.computeIfAbsent(provider, it -> {
                TextureList list = new TextureList(it);
                list.id = textureLists.size() + 1;
                return list;
            });
        }
    }

    public static class ColorRef implements ChunkVariable {

        private final int value;
        private final ChunkColorSection section;

        public ColorRef(ChunkColorSection section, int value) {
            this.value = value;
            this.section = section;
        }

        public static int readFromStream(int usedIndexBytes, IInputStream stream) throws IOException {
            return _readFixedInt(usedIndexBytes, stream);
        }

        public static int readFromStream(int usedIndexBytes, int offset, byte[] bytes) {
            return _readFixedInt(usedIndexBytes, offset, bytes);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            _writeFixedInt(section.getStartIndex() + value, section.colorIndexBytes, stream);
        }

        @Override
        public boolean freeze() {
            return section.isResolved();
        }
    }

    public static class TextureRef implements ChunkVariable {

        private final Vector2f uv;
        private final TextureList list;
        private final ChunkColorSection section;

        public TextureRef(ChunkColorSection section, TextureList list, Vector2f uv) {
            this.section = section;
            this.list = list;
            this.uv = uv;
        }

        public static Vector2f readFromStream(int usedIndexBytes, int offset, byte[] bytes) {
            float x = _readFixedFloat(usedIndexBytes, offset, bytes);
            float y = _readFixedFloat(usedIndexBytes, offset + usedIndexBytes, bytes);
            if (x == 0 && y == 0) {
                return Vector2f.ZERO;
            }
            return new Vector2f(x, y);
        }

        @Override
        public void writeToStream(IOutputStream stream) throws IOException {
            Rectangle2f rect = list.getRect();
            _writeFixedFloat(rect.getX() + uv.getX(), section.textureIndexBytes, stream);
            _writeFixedFloat(rect.getY() + uv.getY(), section.textureIndexBytes, stream);
        }

        @Override
        public boolean freeze() {
            return section.isResolved() && list.isResolved();
        }

        public float getU() {
            return uv.getX();
        }

        public float getV() {
            return uv.getY();
        }

        public Vector2f getPos() {
            return uv;
        }

        public ITextureProvider getProvider() {
            return list.provider;
        }
    }

    public static class TextureList {

        private Rectangle2f rect = Rectangle2f.ZERO;
        private ITextureProvider provider;
        private boolean isResolved = false;

        protected int id = 0;
        protected int parentId = 0;

        private final ArrayList<TextureRef> uvs = new ArrayList<>();

        public TextureList() {
        }

        public TextureList(ITextureProvider provider) {
            this.rect = new Rectangle2f(0, 0, provider.getWidth(), provider.getHeight());
            this.provider = provider;
        }

        public void readFromStream(IInputStream stream) throws IOException {
            this.id = stream.readVarInt();
            this.parentId = stream.readVarInt();
            float x = stream.readFloat();
            float y = stream.readFloat();
            float width = stream.readFloat();
            float height = stream.readFloat();
            this.rect = new Rectangle2f(x, y, width, height);
            TextureAnimation animation = stream.readTextureAnimation();
            TextureProperties properties = stream.readTextureProperties();
            int byteSize = stream.readInt();
            TextureData provider = new TextureData(String.valueOf(id), width, height, animation, properties);
            provider.load(stream.readBytes(byteSize));
            this.provider = provider;
        }

        public void writeToStream(IOutputStream stream) throws IOException {
            ByteBuffer buffer = provider.getBuffer();
            stream.writeVarInt(id);
            stream.writeVarInt(parentId);
            stream.writeFloat(rect.getX());
            stream.writeFloat(rect.getY());
            stream.writeFloat(rect.getWidth());
            stream.writeFloat(rect.getHeight());
            stream.writeTextureAnimation((TextureAnimation) provider.getAnimation());
            stream.writeTextureProperties((TextureProperties) provider.getProperties());
            stream.writeInt(buffer.remaining());
            stream.writeBytes(buffer);
        }

        public void freeze(float x, float y, Function<ITextureProvider, TextureList> childProvider) {
            this.rect = new Rectangle2f(x, y, rect.getWidth(), rect.getHeight());
            this.isResolved = true;
            // bind the child -> parent
            if (this.provider.getVariants() != null) {
                this.provider.getVariants().stream().map(childProvider).filter(Objects::nonNull).forEach(it -> it.parentId = this.id);
            }
        }

        public boolean contains(Vector2f uv) {
            float x0 = rect.getMinX();
            float x1 = uv.getX();
            float x2 = rect.getMaxX();
            return x0 <= x1 && x1 <= x2;
        }

        public TextureRef get(Vector2f uv, ChunkColorSection section) {
            return new TextureRef(section, this, new Vector2f(uv.getX() - rect.getX(), uv.getY()));
        }

        public TextureRef add(Vector2f uv, ChunkColorSection section) {
            TextureRef ref = new TextureRef(section, this, uv);
            uvs.add(ref);
            return ref;
        }

        public Rectangle2f getRect() {
            return rect;
        }

        public boolean isResolved() {
            return isResolved;
        }
    }
}

