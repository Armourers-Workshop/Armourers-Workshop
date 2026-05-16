package moe.plushie.armourers_workshop.compat.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.renderer.rendertype.AbstractRenderType;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.LazyValue;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

@Available("[16, 26)")
@OnlyIn(Dist.CLIENT)
public class AbstractRenderAttachment {

    private static final Map<SkinRenderType.Group, LazyValue<IRenderType>> ATTACH_POINTS = Collections.immutableMap(it -> {
        it.put(SkinRenderType.Group.SOLID_BLOCKS, normal(Sheets::solidBlockSheet));
        it.put(SkinRenderType.Group.CUTOUT_BLOCKS, normal(Sheets::cutoutBlockSheet));
        it.put(SkinRenderType.Group.TRANSLUCENT_BLOCKS, normal(Sheets::translucentCullBlockSheet));
        it.put(SkinRenderType.Group.OUTLINE_BLOCKS, outline(Sheets::solidBlockSheet));

        it.put(SkinRenderType.Group.SOLID_ENTITIES, normal(RenderType::glint));
        it.put(SkinRenderType.Group.CUTOUT_ENTITIES, normal(RenderType::glintTranslucent));
        it.put(SkinRenderType.Group.TRANSLUCENT_ENTITIES, normal(RenderType::glintTranslucent));
        it.put(SkinRenderType.Group.OUTLINE_ENTITIES, outline(Sheets::solidBlockSheet));

        it.put(SkinRenderType.Group.CLOUDS, normal(RenderType::waterMask));
        it.put(SkinRenderType.Group.WEATHER, normal(RenderType::waterMask));
    });

    @Nullable
    public static IRenderType find(SkinRenderType.Group group) {
        var provider = ATTACH_POINTS.get(group);
        if (provider != null) {
            return provider.get();
        }
        return null;
    }

    private static LazyValue<IRenderType> outline(Supplier<RenderType> supplier) {
        return normal(() -> supplier.get().outline().orElse(null));
    }

    private static LazyValue<IRenderType> normal(Supplier<RenderType> supplier) {
        return LazyValue.of(() -> Objects.flatMap(supplier.get(), AbstractRenderType::wrap));
    }
}
