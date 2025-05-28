package moe.plushie.armourers_workshop.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;

import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public interface IRenderType extends Supplier<RenderType> {

    boolean isGrowing();

    boolean isTranslucent();

    boolean isOutline();

    int bufferSize();

    IVertexFormat.Mode mode();

    IVertexFormat format();

    Optional<IRenderType> outline();

    enum Target {
        MAIN, OUTLINE, TRANSLUCENT, CLOUDS, WEATHER, PARTICLES, ITEM_ENTITY
    }

    enum Transparency {
        NONE, DEFAULT, TRANSLUCENT
    }

    enum Texturing {
    }

    enum WriteMask {
        NONE, COLOR_DEPTH_WRITE, COLOR_WRITE, DEPTH_WRITE
    }

    enum DepthTest {
        NONE, EQUAL, LESS_EQUAL
    }

    enum ColorLogic {
        NONE, OR_REVERSE
    }
}
