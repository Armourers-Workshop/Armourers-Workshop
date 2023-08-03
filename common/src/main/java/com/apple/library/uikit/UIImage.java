package com.apple.library.uikit;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGSize;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntFunction;

@SuppressWarnings("unused")
public class UIImage {

    protected final ResourceLocation rl;
    protected final CGPoint uv;
    protected final CGSize size;
    protected final CGSize source;
    protected final CGSize limit;
    protected final ClipData clipData;
    protected final AnimationData animationData;
    protected final IntFunction<CGPoint> mapping;

    private UIImage(ResourceLocation rl, CGPoint uv, CGSize size, CGSize source, CGSize limit, ClipData clipData, AnimationData animationData, IntFunction<CGPoint> mapping) {
        this.rl = rl;
        this.uv = uv;
        this.size = size;
        this.source = source;
        this.limit = limit;
        this.clipData = clipData;
        this.animationData = animationData;
        this.mapping = mapping;
    }

    public static Builder of(ResourceLocation rl) {
        return Builder.of(rl);
    }

    public UIImage imageAtIndex(int index) {
        if (mapping == null) {
            return this;
        }
        if (size != null) {
            CGPoint point = mapping.apply(index);
            if (point == null) {
                return this;
            }
            float u = point.x * size.width;
            float v = point.y * size.height;
            if (uv != null) {
                u += uv.x;
                v += uv.y;
            }
            return Builder.of(this).uv((int) u, (int) v).unzip(null).build();
        }
        ModLog.warn("Unable generate status image, because missing the image size.");
        return this;
    }

    public UIImage snapshot() {
        Builder builder = Builder.of(this);
        builder.animationData = null;
        return builder.build();
    }

    public ResourceLocation rl() {
        return rl;
    }

    public CGPoint uv() {
        return uv;
    }

    public CGSize size() {
        return size;
    }

    public CGSize source() {
        return source;
    }

    public CGSize limit() {
        return limit;
    }

    public ClipData clipData() {
        return clipData;
    }

    public AnimationData animationData() {
        return animationData;
    }

    public boolean isPacked() {
        return mapping != null;
    }

    public static class AnimationData {

        public final int frames;
        public final int speed;

        public AnimationData(int frames, int speed) {
            this.frames = frames;
            this.speed = speed;
        }
    }

    public static class ClipData {

        public final UIEdgeInsets contentInsets;

        public ClipData(UIEdgeInsets contentInsets) {
            this.contentInsets = contentInsets;
        }
    }

    public static class Builder {

        private ResourceLocation rl;
        private CGPoint uv;
        private CGSize size;
        private CGSize source;
        private CGSize limit;
        private IntFunction<CGPoint> mapping;
        private ClipData clipData;
        private AnimationData animationData;

        public static Builder of(UIImage img) {
            Builder builder = new Builder();
            builder.rl = img.rl;
            builder.uv = img.uv;
            builder.size = img.size;
            builder.limit = img.limit;
            builder.clipData = img.clipData;
            builder.animationData = img.animationData;
            builder.mapping = img.mapping;
            return builder;
        }

        public static Builder of(ResourceLocation rl) {
            Builder builder = new Builder();
            builder.rl = rl;
            return builder;
        }

        public Builder uv(float u, float v) {
            this.uv = new CGPoint(u, v);
            return this;
        }

        public Builder fixed(float width, float height) {
            this.size = new CGSize(width, height);
            return this;
        }

        public Builder resizable(float sourceWidth, float sourceHeight) {
            this.source = new CGSize(sourceWidth, sourceHeight);
            return this;
        }

        public Builder resize(float targetWidth, float targetHeight, float sourceWidth, float sourceHeight) {
            this.source = new CGSize(sourceWidth, sourceHeight);
            this.size = new CGSize(targetWidth, targetHeight);
            return this;
        }

        public Builder limit(float width, float height) {
            this.limit = new CGSize(width, height);
            return this;
        }

        public Builder clip(float top, float left, float bottom, float right) {
            this.clipData = new ClipData(new UIEdgeInsets(top, left, bottom, right));
            return this;
        }

        public Builder unzip(IntFunction<CGPoint> mapping) {
            this.mapping = mapping;
            return this;
        }

        public Builder animation(int frame, int speed) {
            this.animationData = new AnimationData(frame, speed);
            return this;
        }

        public UIImage build() {
            return new UIImage(rl, uv, size, source, limit, clipData, animationData, mapping);
        }
    }
}
