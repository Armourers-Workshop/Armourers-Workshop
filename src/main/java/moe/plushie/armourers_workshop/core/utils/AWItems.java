package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.render.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.item.BottleItem;
import moe.plushie.armourers_workshop.core.item.SkinItem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
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
public class AWItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AWCore.getModId());

    public static final ItemGroup TAB_SKIN = (new ItemGroup("skin") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.APPLE);
        }
    });

    public static final RegistryObject<Item> SKIN = ITEMS.register("skin", () -> new SkinItem(new Item.Properties().stacksTo(1).setISTER(() -> SkinItemRenderer.ItemStackRenderer::new)));
    public static final RegistryObject<Item> BOTTLE = ITEMS.register("dye-bottle", () -> new BottleItem(new Item.Properties().stacksTo(1).tab(TAB_SKIN)));


    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, AWCore.getModId());


    public static final RegistryObject<EntityType<MannequinEntity>> MANNEQUIN2 = ENTITIES.register("mannequin", () -> EntityType.Builder
            .of(MannequinEntity::new, EntityClassification.CREATURE)
            .build("mannequin"));
            //register("mannequin", () -> MANNEQUIN));

    //.create(SlimePetEntity::new, EntityClassification.CREATURE).build(MOD_ID+":slime_pet").setRegistryName("slime_pet")

}
