package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IItemGroup;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemGroupBuilder;
import moe.plushie.armourers_workshop.core.client.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {

    public static final IRegistryKey<IItemGroup> MAIN_GROUP = create().icon(() -> SkinItemRenderer.getInstance()::getPlayerMannequinItem).build("main");
    public static final IRegistryKey<IItemGroup> BUILDING_GROUP = create().icon(() -> () -> new ItemStack(ModItems.ARMOURER.get())).build("painting_tools");

    private static IItemGroupBuilder<IItemGroup> create() {
        return BuilderManager.getInstance().createItemGroupBuilder();
    }

    public static void init() {
    }
}
