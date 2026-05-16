package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexFormat;
import moe.plushie.armourers_workshop.api.core.IResourceKey;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainer;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModConstants;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public abstract class SkinRenderType implements IRenderType, IAssociatedContainer {

    protected boolean isEmissive = false;
    protected boolean isOutline = false;

    protected int ordinal = 0;
    protected int bufferSize = 0;

    protected Group group = Group.MAIN;
    protected BlendMode blendMode = BlendMode.NONE;

    protected IResourceKey texture;

    protected IVertexFormat format;

    protected String name;
    protected Optional<IRenderType> outline;

    @Override
    public <T> T getAssociatedObject(IAssociatedContainer.Key<T> key) {
        return ((IAssociatedContainer) get()).getAssociatedObject(key);
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainer.Key<T> key, T value) {
        ((IAssociatedContainer) get()).setAssociatedObject(key, value);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Override
    public boolean isEmissive() {
        return isEmissive;
    }

    @Override
    public boolean isTranslucent() {
        return blendMode == BlendMode.TRANSLUCENT;
    }

    @Override
    public boolean isOutline() {
        return isOutline;
    }

    @Override
    public IVertexFormat format() {
        return format;
    }

    @Override
    public Optional<IRenderType> outline() {
        return outline;
    }

    public Group group() {
        return group;
    }

    public int bufferSize() {
        return bufferSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinRenderType that)) return false;
        return get().equals(that.get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name, "texture", texture);
    }

    public enum Group {
        MAIN,

        SOLID_BLOCKS,
        CUTOUT_BLOCKS,
        TRANSLUCENT_BLOCKS,
        OUTLINE_BLOCKS,

        SOLID_ENTITIES,
        CUTOUT_ENTITIES,
        TRANSLUCENT_ENTITIES,
        OUTLINE_ENTITIES,

        CLOUDS,
        WEATHER,
        PARTICLES;

        /// Returns the group outline version, if the group not support outline will return main.
        public Group outline() {
            return switch (this) {
                case OUTLINE_BLOCKS, OUTLINE_ENTITIES -> this;
                case SOLID_BLOCKS, CUTOUT_BLOCKS, TRANSLUCENT_BLOCKS -> OUTLINE_BLOCKS;
                case SOLID_ENTITIES, CUTOUT_ENTITIES, TRANSLUCENT_ENTITIES -> OUTLINE_ENTITIES;
                default -> MAIN;
            };
        }
    }

    public enum Target {
        MAIN,
        OUTLINE,
        TRANSLUCENT,
        CLOUDS,
        WEATHER,
        PARTICLES,
        ITEM_ENTITY,
    }

    public enum DepthTestMode {
        NO_DEPTH_TEST,
        EQUAL_DEPTH_TEST,
        LEQUAL_DEPTH_TEST,
        LESS_DEPTH_TEST,
        GREATER_DEPTH_TEST,
    }

    public enum BlendMode {
        NONE,
        NORMAL,
        LIGHTNING,
        GLINT,
        OVERLAY,
        TRANSLUCENT,
        TRANSLUCENT_PREMULTIPLIED_ALPHA,
        ADDITIVE,
        INVERT,
    }

    public enum PolygonMode {
        FILL,
        WIREFRAME,
    }

    public abstract static class Builder {

        private final ArrayList<Consumer<SkinRenderType>> updater = new ArrayList<>();

        public Builder texture(IResourceKey texture) {
            return texture(texture, false, false);
        }

        public Builder texture(IResourceKey texture, boolean blur, boolean mipmap) {
            updater.add(t -> t.texture = texture);
            return this;
        }

        public Builder group(Group group) {
            updater.add(t -> t.group = group);
            return this;
        }

        public Builder target(Target target) {
            return this;
        }


        public Builder polygonMode(PolygonMode mode) {
            return this;
        }

        public Builder polygonOffset(float factor, float units) {
            return this;
        }


        public Builder stroke(float width) {
            return this;
        }

        public Builder lineWidth(float width) {
            return this;
        }


        public Builder cull() {
            return this;
        }

        public Builder blend(BlendMode mode) {
            updater.add(t -> t.blendMode = mode);
            return this;
        }

        public Builder colorWrite(boolean bl) {
            return colorWrite(bl, bl);
        }

        public Builder colorWrite(boolean writeColor, boolean writeAlpha) {
            return this;
        }


        public Builder depthWrite(boolean writeDepth) {
            return this;
        }

        public Builder depthTest(DepthTestMode depthTestFunction) {
            return this;
        }


        public Builder lightmap() {
            return this;
        }

        public Builder overlay() {
            return this;
        }

        public Builder emissive() {
            updater.add(t -> t.isEmissive = true);
            return this;
        }

        public Builder outline() {
            return this;
        }

        public Builder crumbling() {
            return this;
        }

        public Builder sortOnUpload() {
            return this;
        }

        public Builder ordinal(int ordinal) {
            updater.add(t -> t.ordinal = ordinal);
            return this;
        }

        public SkinRenderType build(String name) {
            var registryName = ModConstants.key("rendertype/" + name);
            var renderType = create(registryName.toString());
            updater.forEach(it -> it.accept(renderType));
            renderType.outline().ifPresent(type -> {
                if (type instanceof SkinRenderType type1) {
                    type1.group = renderType.group().outline();
                    type1.ordinal = renderType.ordinal();
                }
            });
            return renderType;
        }

        protected abstract SkinRenderType create(String registryName);
    }
}
