package moe.plushie.armourers_workshop.core.base;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.MannequinItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import moe.plushie.armourers_workshop.core.item.WandOfStyleItem;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class AWItems {

    @SuppressWarnings("NullableProblems")
    private static final ItemGroup MAIN_GROUP = new ItemGroup("armourers_workshop_main") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return SkinItemRenderer.getItemStackRenderer().getPlayerMannequinItem();
        }
    };

    public static final SkinItem SKIN = register("skin", SkinItem::new, p -> p.setISTER(() -> SkinItemRenderer::getItemStackRenderer));
    public static final BottleItem BOTTLE = register("dye-bottle", BottleItem::new, p -> p.tab(MAIN_GROUP));
    public static final MannequinItem MANNEQUIN = register("mannequin", MannequinItem::new, p -> p.tab(MAIN_GROUP).setISTER(() -> SkinItemRenderer::getItemStackRenderer));
    public static final WandOfStyleItem WAND_OF_STYLE = register("wand-of-style", WandOfStyleItem::new, p -> p.tab(MAIN_GROUP));


    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        return register(name, factory, null);
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Consumer<Item.Properties> customizer) {
        Item.Properties properties = new Item.Properties();
        properties.stacksTo(1);
        if (customizer != null) {
            customizer.accept(properties);
        }
        ResourceLocation registryName = AWCore.resource(name);
        T item = factory.apply(properties);
        item.setRegistryName(registryName);
        return item;
    }
}
