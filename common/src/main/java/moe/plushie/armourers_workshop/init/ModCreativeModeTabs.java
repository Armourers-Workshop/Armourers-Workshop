package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.ICreativeModeTabBuilder;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.item.CreativeModeTab;

public class ModCreativeModeTabs {

    public static final IRegistryHolder<CreativeModeTab> MAIN_GROUP = normal().icon(() -> SkinItemRenderer::getPlayerMannequinItem).build("main");
    public static final IRegistryHolder<CreativeModeTab> BUILDING_GROUP = normal().icon(() -> ModItems.ARMOURER.get()::getDefaultInstance).build("painting_tools");

    private static ICreativeModeTabBuilder<CreativeModeTab> normal() {
        return BuilderManager.getInstance().createItemGroupBuilder();
    }

    public static void init() {
    }
}
