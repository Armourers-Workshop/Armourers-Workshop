package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.utils.texture.TextureAnimation;
import moe.plushie.armourers_workshop.utils.texture.TextureProperties;

import java.util.ArrayList;

public class BlockBenchTexture extends BlockBenchObject {

    private final String renderMode;
    private final String source;

    private final int frameTime;
    private final String frameOrderType;
    private final String frameOrder;
    private final boolean frameInterpolate;

    public BlockBenchTexture(String uuid, String name, String renderMode, String source, int frameTime, String frameOrderType, String frameOrder, boolean frameInterpolate) {
        super(uuid, name);
        this.renderMode = renderMode;
        this.source = source;
        this.frameTime = frameTime;
        this.frameOrderType = frameOrderType;
        this.frameOrder = frameOrder;
        this.frameInterpolate = frameInterpolate;
    }

    public String getSource() {
        return source;
    }


    public int getFrameTime() {
        return frameTime;
    }

    public boolean getFrameInterpolate() {
        return frameInterpolate;
    }

    public TextureAnimation.Mode getFrameMode() {
        if (frameOrderType.equals("loop")) {
            return TextureAnimation.Mode.LOOP;
        }
        if (frameOrderType.equals("backwards")) {
            return TextureAnimation.Mode.BACKWARDS;
        }
        if (frameOrderType.equals("back_and_forth")) {
            return TextureAnimation.Mode.BACK_AND_FORTH;
        }
        if (frameOrderType.equals("custom")) {
            int[] frames = _parseFrameSeq(frameOrder);
            if (frames.length >= 1) {
                return new TextureAnimation.Mode(frames);
            }
        }
        return TextureAnimation.Mode.LOOP;
    }

    public TextureProperties getProperties() {
        TextureProperties properties = new TextureProperties();
        properties.setEmissive(renderMode.equals("emissive"));
        properties.setAdditive(renderMode.equals("additive"));
        return properties;
    }

    private int[] _parseFrameSeq(String input) {
        String[] parts = input.split("\\s+");
        ArrayList<Integer> values = new ArrayList<>(parts.length);
        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                values.add(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int[] frames = new int[values.size()];
        for (int i = 0; i < frames.length; ++i) {
            frames[i] = values.get(i);
        }
        return frames;
    }

    public static class Builder extends BlockBenchObject.Builder {

        private String renderMode = "default";
        private String source;

        private int frameTime = 1;
        private String frameOrderType = "loop";
        private String frameOrder = "";
        private boolean frameInterpolate = false;

        public void renderMode(String renderMode) {
            this.renderMode = renderMode;
        }

        public void source(String source) {
            this.source = source;
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
            return new BlockBenchTexture(uuid, name, renderMode, source, frameTime, frameOrderType, frameOrder, frameInterpolate);
        }
    }
}
