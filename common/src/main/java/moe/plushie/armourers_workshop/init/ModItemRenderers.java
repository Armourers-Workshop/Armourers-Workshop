package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRenderer;
import moe.plushie.armourers_workshop.api.client.ISpecialModelRendererType;
import moe.plushie.armourers_workshop.api.core.IDataMapCodec;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ISpecialModelRendererBuilder;
import moe.plushie.armourers_workshop.builder.client.render.SkinCubeItemRenderer;
import moe.plushie.armourers_workshop.core.client.render.MannequinItemRenderer;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.init.platform.ClientBuilderManager;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ModItemRenderers {

    public static final IRegistryHolder<ISpecialModelRendererType<SkinItemRenderer>> SKIN = create(SkinItemRenderer.MAP_CODEC).build("skin");
    public static final IRegistryHolder<ISpecialModelRendererType<SkinCubeItemRenderer>> SKIN_CUBE = create(SkinCubeItemRenderer.MAP_CODEC).build("skin-cube");
    public static final IRegistryHolder<ISpecialModelRendererType<MannequinItemRenderer>> MANNEQUIN = create(MannequinItemRenderer.MAP_CODEC).build("mannequin");

    private static <T extends ISpecialModelRenderer<?>> ISpecialModelRendererBuilder<T> create(IDataMapCodec<T> codec) {
        return ClientBuilderManager.getInstance().createSpecialModelRendererBuilder(codec);
    }

    public static void init() {
    }
}
