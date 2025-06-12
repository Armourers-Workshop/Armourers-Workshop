package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenVector2f;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureProperties;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ChunkTextureData {

    private static final SkinProperty<List<Float>> USED_RECT_KEY = SkinProperty.normal("usedRect", null);

    protected OpenRectangle2f rect = OpenRectangle2f.ZERO;
    protected OpenRectangle2f usedRect = OpenRectangle2f.ZERO;
    protected SkinTextureData provider;
    protected boolean isResolved = false;

    protected int id = 0;
    protected int parentId = 0;

    private final ArrayList<TextureRef> uvs = new ArrayList<>();

    public ChunkTextureData() {
    }

    public ChunkTextureData(SkinTextureData provider) {
        this.rect = new OpenRectangle2f(0, 0, provider.width(), provider.height());
        this.usedRect = rect;
        this.provider = provider;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        this.id = stream.readVarInt();
        this.parentId = stream.readVarInt();
        if (id == 0) {
            // TODO: when id is 0, this is a builtin texture.
        }
        var x = stream.readFloat();
        var y = stream.readFloat();
        var width = stream.readFloat();
        var height = stream.readFloat();
        this.rect = new OpenRectangle2f(x, y, width, height);
        this.usedRect = rect;
        var animation = stream.readTextureAnimation();
        var properties = readAdditionalData(stream.readTextureProperties());
        var file = stream.readFile();
        var provider = new SkinTextureData(file.name(), width, height, animation, properties);
        provider.load(file.bytes());
        this.provider = provider;
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        stream.writeVarInt(id);
        stream.writeVarInt(parentId);
        if (id == 0) {
            // TODO: when id is 0, this is a builtin texture.

        }
        stream.writeFloat(rect.x());
        stream.writeFloat(rect.y());
        stream.writeFloat(rect.width());
        stream.writeFloat(rect.height());
        stream.writeTextureAnimation(provider.animation());
        stream.writeTextureProperties(writeAdditionalData(provider.properties()));
        stream.writeFile(ChunkFile.image(provider.name(), provider.buffer()));
    }

    public void freeze(float x, float y, Function<SkinTextureData, ChunkTextureData> childProvider) {
        // bind the child -> parent
        Collections.compactMap(provider.variants(), childProvider).forEach(it -> it.parentId = this.id);

        // alignment the coordinate 16x16.
        float minX = OpenMath.floori((usedRect.minX() - rect.minX()) / 16f) * 16f;
        float minY = OpenMath.floori((usedRect.minY() - rect.minY()) / 16f) * 16f;
        float maxX = OpenMath.ceili((usedRect.maxX() - rect.minX()) / 16f) * 16f;
        float maxY = OpenMath.ceili((usedRect.maxY() - rect.minY()) / 16f) * 16f;

        this.rect = new OpenRectangle2f(x - minX, y - minY, rect.width(), rect.height());
        this.usedRect = new OpenRectangle2f(x, y, maxX - minX, maxY - minY);
        this.isResolved = true;
    }

    public boolean contains(OpenVector2f uv) {
        return usedRect.contains(uv);
    }

    public TextureRef get(OpenVector2f uv, ChunkColorSection section) {
        return new TextureRef(section, this, new OpenVector2f(uv.x() - rect.x(), uv.y() - rect.y()));
    }

    public TextureRef add(OpenVector2f uv, ChunkColorSection section) {
        var ref = new TextureRef(section, this, uv);
        // the texture coordinates maybe exceed the texture itself.
        if (!usedRect.contains(uv)) {
            float x0 = Math.min(usedRect.minX(), uv.x());
            float y0 = Math.min(usedRect.minY(), uv.y());
            float x1 = Math.max(usedRect.maxX(), uv.x());
            float y1 = Math.max(usedRect.maxY(), uv.y());
            usedRect = new OpenRectangle2f(x0, y0, x1 - x0, y1 - y0);
        }
        uvs.add(ref);
        return ref;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    public OpenRectangle2f rect() {
        return rect;
    }

    public OpenRectangle2f usedRect() {
        return usedRect;
    }

    public SkinTextureData texture() {
        return provider;
    }

    public boolean isResolved() {
        return isResolved;
    }

    private SkinTextureProperties readAdditionalData(SkinTextureProperties properties) {
        // have custom used rect?
        var rectValues = properties.get(USED_RECT_KEY);
        if (rectValues != null) {
            float x = rectValues.get(0);
            float y = rectValues.get(1);
            float w = rectValues.get(2);
            float h = rectValues.get(3);
            this.usedRect = new OpenRectangle2f(x, y, w, h);
        }
        return properties;
    }

    private SkinTextureProperties writeAdditionalData(SkinTextureProperties properties) {
        // have custom used rect?
        if (!rect.equals(usedRect)) {
            float x = usedRect.x();
            float y = usedRect.y();
            float w = usedRect.width();
            float h = usedRect.height();
            var newProperties = properties.copy();
            newProperties.set(USED_RECT_KEY, Collections.newList(x, y, w, h));
            properties = newProperties;
        }
        return properties;
    }


    public static class TextureRef implements ChunkVariable {

        private final OpenVector2f uv;
        private final ChunkTextureData list;
        private final ChunkColorSection section;

        public TextureRef(ChunkColorSection section, ChunkTextureData list, OpenVector2f uv) {
            this.section = section;
            this.list = list;
            this.uv = uv;
        }

        @Override
        public void writeToStream(ChunkOutputStream stream) throws IOException {
            var rect = list.rect();
            stream.writeFixedFloat(rect.x() + uv.x(), section.textureIndexBytes);
            stream.writeFixedFloat(rect.y() + uv.y(), section.textureIndexBytes);
        }

        @Override
        public boolean freeze() {
            return section.isResolved() && list.isResolved();
        }

        public float u() {
            return uv.x();
        }

        public float v() {
            return uv.y();
        }

        public OpenVector2f uv() {
            return uv;
        }

        public SkinTextureData provider() {
            return list.provider;
        }
    }

    public static class OptionsRef implements ChunkVariable {

        private final long value;
        private final ChunkColorSection section;

        public OptionsRef(ChunkColorSection section, long value) {
            this.value = value;
            this.section = section;
        }

        @Override
        public void writeToStream(ChunkOutputStream stream) throws IOException {
            stream.writeFixedInt((int) (value), section.textureIndexBytes);
            stream.writeFixedInt((int) (value >> 32), section.textureIndexBytes);
        }

        @Override
        public boolean freeze() {
            return section.isResolved();
        }
    }
}
