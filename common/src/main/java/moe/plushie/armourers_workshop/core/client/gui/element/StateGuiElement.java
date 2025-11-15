package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;

@SuppressWarnings("unused")
public abstract class StateGuiElement implements CGGraphicsElement {

    public static StateGuiElement beginTransparencyLayer() {
        return BeginTransparencyLayer.INSTANCE;
    }

    public static StateGuiElement endTransparencyLayer() {
        return EndTransparencyLayer.INSTANCE;
    }

    public static StateGuiElement beginComposeLayer(String name) {
        return new BeginComposeLayer(name);
    }

    public static StateGuiElement endComposeLayer(String name) {
        return new EndComposeLayer(name);
    }

    public static StateGuiElement flush() {
        return Flush.INSTANCE;
    }

    @Override
    public void render(IPoseStack poseStack, IBufferSource bufferSource) {
        // nop
    }

    public static class Flush extends StateGuiElement {
        private static final Flush INSTANCE = new Flush();
    }

    public static class BeginTransparencyLayer extends StateGuiElement {
        private static final BeginTransparencyLayer INSTANCE = new BeginTransparencyLayer();
    }

    public static class EndTransparencyLayer extends StateGuiElement {
        private static final EndTransparencyLayer INSTANCE = new EndTransparencyLayer();
    }

    public static class BeginComposeLayer extends StateGuiElement {

        private final String name;

        public BeginComposeLayer(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }
    }

    public static class EndComposeLayer extends StateGuiElement {

        private final String name;

        public EndComposeLayer(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }
    }
}
