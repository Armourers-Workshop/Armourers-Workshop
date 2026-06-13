package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.builder.client.render.SkinCubeItemRenderer;
import moe.plushie.armourers_workshop.core.client.render.MannequinItemRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRenderer;
import moe.plushie.armourers_workshop.core.client.special.SpecialModelRendererType;
import moe.plushie.armourers_workshop.init.platform.Platform;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ModItemRenderers {

    public static final IRegistryHolder<SpecialModelRendererType<SkinItemRenderer>> SKIN = create(SkinItemRenderer.MAP_CODEC).build("skin");
    public static final IRegistryHolder<SpecialModelRendererType<SkinCubeItemRenderer>> SKIN_CUBE = create(SkinCubeItemRenderer.MAP_CODEC).build("skin-cube");
    public static final IRegistryHolder<SpecialModelRendererType<MannequinItemRenderer>> MANNEQUIN = create(MannequinItemRenderer.MAP_CODEC).build("mannequin");

    private static <T extends SpecialModelRenderer<?>> IRegistryBuilder<SpecialModelRendererType<T>> create(IDataMapCodec<T> codec) {
        return Platform.get().client().builder().specialModelRenderer(codec);
    }

    public static void init() {
    }
}
