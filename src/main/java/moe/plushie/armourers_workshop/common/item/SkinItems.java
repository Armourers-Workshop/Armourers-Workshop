package moe.plushie.armourers_workshop.common.item;

import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class SkinItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SkinCore.getModId());

    public static final ItemGroup TAB_SKIN = (new ItemGroup("skin") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.APPLE);
        }
    });

    public static final RegistryObject<Item> SKIN = ITEMS.register("skin", () -> new SkinItem(new Item.Properties().stacksTo(1).setISTER(() -> () -> SkinItemRenderer.INSTANCE).tab(TAB_SKIN)));
}
