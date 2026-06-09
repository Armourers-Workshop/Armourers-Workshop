package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.coregraphics.CGGraphicsParameters;
import com.apple.library.coregraphics.CGGraphicsRenderer;
import com.apple.library.coregraphics.CGGraphicsState;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.gui.element.StateGuiElement;
import moe.plushie.armourers_workshop.core.utils.LazyValue;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

@Available("[20, 26)")
@OnlyIn(Dist.CLIENT)
public class AbstractGuiGraphicsRenderer implements CGGraphicsRenderer {

    private final CGGraphicsState state;
    private final IBufferSource bufferSource;
    private final GuiGraphics graphics;
    private final LazyValue<IGraphicsContext> context;

    private AbstractGuiGraphicsRenderer(GuiGraphics graphics, float width, float height) {
        this.state = new CGGraphicsState(AbstractPoseStack.wrap(graphics.pose()));
        this.bufferSource = AbstractBufferSource.wrap(graphics.bufferSource());
        this.graphics = graphics;
        this.context = new LazyValue<>(() -> AbstractGraphicsRenderer.wrap(graphics.pose(), graphics.bufferSource()));
    }

    public static void wrap(GuiGraphics graphics, float width, float height, float mouseX, float mouseY, float partialTick, Consumer<CGGraphicsContext> action) {
        var param = new CGGraphicsParameters(width, height, mouseX, mouseY, partialTick, graphics);
        var renderer = new AbstractGuiGraphicsRenderer(graphics, width, height);
        var context = new CGGraphicsContext(renderer.state, param, renderer);
        context.draw(StateGuiElement.beginComposeLayer("main"));
        action.accept(context);
        context.draw(StateGuiElement.endComposeLayer("main"));
    }

    public static void unwrap(CGGraphicsContext context, String name, Consumer<GuiGraphics> action) {
        var graphics = (GuiGraphics) context.param().context();
        context.draw(StateGuiElement.beginComposeLayer(name));
        action.accept(graphics);
        context.draw(StateGuiElement.endComposeLayer(name));
    }

    @Override
    public void render(CGGraphicsElement element) {
        // apply the flush into gui graphics.
        if (element instanceof StateGuiElement.Flush || element instanceof StateGuiElement.ComposeLayer) {
            graphics.flush();
            return;
        }
        // forwarding the game element into game graphics.
        if (element instanceof IGraphicsElement element1) {
            context.get().draw(element1);
            return;
        }
        element.render(state.ctm(), bufferSource);
    }
}
