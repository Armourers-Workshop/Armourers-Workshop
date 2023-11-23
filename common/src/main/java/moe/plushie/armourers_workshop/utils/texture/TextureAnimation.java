package moe.plushie.armourers_workshop.utils.texture;

import moe.plushie.armourers_workshop.api.common.ITextureAnimation;

public class TextureAnimation implements ITextureAnimation {

//    public static final TextureAnimation NONE = new TextureAnimation();
//    public static final TextureAnimation DEFAULT = new TextureAnimation() {
//
//        @Override
//        protected float _getTextureOffset(int ticks) {
//            return Math.round((ticks % (255L * 25)) / 25f) / 256f;
//        }
//    };
//
//    private float lastTextureOffset = 0;
//    private OpenMatrix4f lastTextureMatrix = IDENTITY;


    public TextureAnimation() {
    }


    public static class Mode {

        public static final Mode LOOP = new Mode(1, null);
        public static final Mode BACKWARDS = new Mode(2, null);
        public static final Mode BACK_AND_FORTH = new Mode(3, null);

        private final int type;
        private final int[] frames;

        private Mode(int type, int[] frames) {
            this.type = type;
            this.frames = frames;
        }

        public Mode(int[] frames) {
            this(4, frames);
        }

        public int getType() {
            return type;
        }

        public int[] getFrames() {
            return frames;
        }
    }
}
