package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

@SuppressWarnings("unused")
public abstract class StateGuiElement implements CGGraphicsElement {

    public static StateGuiElement flush() {
        return Flush.INSTANCE;
    }

    public static StateGuiElement beginComposeLayer(String name) {
        return new ComposeLayer.Begin(name);
    }

    public static StateGuiElement endComposeLayer(String name) {
        return new ComposeLayer.End(name);
    }

    public static StateGuiElement beginTransparencyLayer() {
        return TransparencyLayer.Begin.INSTANCE;
    }

    public static StateGuiElement endTransparencyLayer() {
        return TransparencyLayer.End.INSTANCE;
    }

    @Override
    public void render(IPoseStack poseStack, IBufferSource bufferSource) {
        // nop
    }

    public static class Flush extends StateGuiElement {
        private static final Flush INSTANCE = new Flush();
    }

    public static class ComposeLayer extends StateGuiElement {

        private final String name;

        protected ComposeLayer(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        public static class Begin extends ComposeLayer {

            protected Begin(String name) {
                super(name);
            }
        }

        public static class End extends ComposeLayer {

            protected End(String name) {
                super(name);
            }
        }
    }

    public static class TransparencyLayer extends StateGuiElement {

        public static class Begin extends TransparencyLayer {
            private static final Begin INSTANCE = new Begin();
        }

        public static class End extends TransparencyLayer {
            private static final End INSTANCE = new End();
        }
    }
}
