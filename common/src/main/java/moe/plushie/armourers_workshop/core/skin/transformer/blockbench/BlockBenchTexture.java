package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.core.texture.TextureAnimation;
import moe.plushie.armourers_workshop.core.texture.TextureProperties;
import moe.plushie.armourers_workshop.utils.math.Size2f;

import java.util.ArrayList;

public class BlockBenchTexture extends BlockBenchObject {

    private final boolean particle;
    private final String renderMode;
    private final String source;

    private final Size2f imageSize;
    private final Size2f textureSize;

    private final int frameTime;
    private final String frameOrderType;
    private final String frameOrder;
    private final boolean frameInterpolate;

    public BlockBenchTexture(String uuid, String name, boolean particle, String renderMode, String source, Size2f imageSize, Size2f textureSize, int frameTime, String frameOrderType, String frameOrder, boolean frameInterpolate) {
        super(uuid, name);
        this.particle = particle;
        this.renderMode = renderMode;
        this.source = source;
        this.imageSize = imageSize;
        this.textureSize = textureSize;
        this.frameTime = frameTime;
        this.frameOrderType = frameOrderType;
        this.frameOrder = frameOrder;
        this.frameInterpolate = frameInterpolate;
    }

    public boolean isParticle() {
        return particle;
    }

    public String getSource() {
        return source;
    }

    public Size2f getImageSize() {
        return imageSize;
    }

    public Size2f getTextureSize() {
        return textureSize;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public boolean getFrameInterpolate() {
        return frameInterpolate;
    }

    public TextureAnimation.Mode getFrameMode() {
        return switch (frameOrderType) {
            case "loop" -> TextureAnimation.Mode.LOOP;
            case "backwards" -> TextureAnimation.Mode.BACKWARDS;
            case "back_and_forth" -> TextureAnimation.Mode.BACK_AND_FORTH;
            case "custom" -> {
                var frames = _parseFrameSeq(frameOrder);
                if (frames.length >= 1) {
                    yield new TextureAnimation.Mode(frames);
                }
                yield TextureAnimation.Mode.LOOP;
            }
            default -> TextureAnimation.Mode.LOOP;
        };
    }

    public TextureProperties getProperties() {
        var properties = new TextureProperties();
        properties.setEmissive(renderMode.equals("emissive"));
        //properties.setAdditive(renderMode.equals("additive"));
        properties.setParticle(particle);
        return properties;
    }

    private int[] _parseFrameSeq(String input) {
        var parts = input.split("\\s+");
        var values = new ArrayList<Integer>(parts.length);
        for (var part : parts) {
            try {
                var value = Integer.parseInt(part);
                values.add(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        var frames = new int[values.size()];
        for (var i = 0; i < frames.length; ++i) {
            frames[i] = values.get(i);
        }
        return frames;
    }

    public static class Builder extends BlockBenchObject.Builder {

        private String renderMode = "default";
        private String source;
        private boolean particle = false;

        private int frameTime = 1;
        private String frameOrderType = "loop";
        private String frameOrder = "";
        private boolean frameInterpolate = false;

        private Size2f imageSize;
        private Size2f textureSize;

        public void renderMode(String renderMode) {
            this.renderMode = renderMode;
        }

        public void particle(boolean particle) {
            this.particle = particle;
        }

        public void source(String source) {
            this.source = source;
        }

        public void imageSize(Size2f size) {
            this.imageSize = size;
        }

        public void textureSize(Size2f size) {
            this.textureSize = size;
        }

        public void frameTime(int frameTime) {
            this.frameTime = frameTime;
        }

        public void frameOrderType(String frameOrderType) {
            this.frameOrderType = frameOrderType;
        }

        public void frameOrder(String frameOrder) {
            this.frameOrder = frameOrder;
        }

        public void frameInterpolate(boolean frameInterpolate) {
            this.frameInterpolate = frameInterpolate;
        }

        public BlockBenchTexture build() {
            return new BlockBenchTexture(uuid, name, particle, renderMode, source, imageSize, textureSize, frameTime, frameOrderType, frameOrder, frameInterpolate);
        }
    }
}
