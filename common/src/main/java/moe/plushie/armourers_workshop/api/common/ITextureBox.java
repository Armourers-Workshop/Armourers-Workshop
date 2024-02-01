package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface ITextureBox {

    @Nullable
    ITextureKey getTexture(Direction dir);
}
