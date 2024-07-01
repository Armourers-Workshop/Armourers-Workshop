package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public interface ConcurrentBufferBuilder {

    void addPart(BakedSkinPart part, BakedSkin skin, ColorScheme scheme, ConcurrentRenderingContext context);

    void addShape(Vector3f origin, ConcurrentRenderingContext context);

    void addShape(OpenVoxelShape shape, UIColor color, ConcurrentRenderingContext context);

    void addShape(BakedArmature armature, ConcurrentRenderingContext context);
}
