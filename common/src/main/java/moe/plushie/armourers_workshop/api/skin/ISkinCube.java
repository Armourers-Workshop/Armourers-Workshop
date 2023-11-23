package moe.plushie.armourers_workshop.api.skin;

import moe.plushie.armourers_workshop.api.common.ITextureKey;
import moe.plushie.armourers_workshop.api.math.IRectangle3f;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.math.IVector3i;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface ISkinCube {

    ISkinCubeType getType();

    IVector3i getPosition();

    IRectangle3f getShape();

    ITransformf getTransform();

    IPaintColor getPaintColor(Direction dir);

    @Nullable
    ITextureKey getTexture(Direction dir);
}
