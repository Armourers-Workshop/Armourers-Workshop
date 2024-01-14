package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.NotNull;

public interface SkinRenderBufferSource {

    static SkinRenderBufferSource immediate(MultiBufferSource buffers) {
        return skin -> {
            SkinVertexBufferBuilder bufferBuilder = SkinVertexBufferBuilder.getBuffer(buffers);
            return bufferBuilder.getBuffer(skin);
        };
    }

    ObjectBuilder getBuffer(@NotNull BakedSkin skin);

    interface ObjectBuilder {

        int addPart(BakedSkinPart bakedPart, BakedSkin bakedSkin, ColorScheme scheme, boolean shouldRender, SkinRenderContext context);

        default void addShape(Vector3f origin, SkinRenderContext context) {
        }

        default void addShape(OpenVoxelShape shape, UIColor color, SkinRenderContext context) {
        }

        default void addShape(BakedArmature armature, SkinRenderContext context) {
        }
    }
}
