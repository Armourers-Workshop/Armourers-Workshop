package moe.plushie.armourers_workshop.init.function;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.api.common.ILootConditionalFunction;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkinRandomlyFunction implements ILootConditionalFunction {

    public final List<Provider> providers;

    SkinRandomlyFunction(Collection<Provider> providers) {
        this.providers = ImmutableList.copyOf(providers);
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
//        RandomSource randomSource = lootContext.getRandom(); // !!!!
        if (providers.isEmpty()) {
            return ItemStack.EMPTY;
        }
//            boolean bl = itemStack.is(Items.BOOK);
//            List list = Registry.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter(enchantment -> bl || enchantment.canEnchant(itemStack)).collect(Collectors.toList());
//            if (list.isEmpty()) {
//                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)itemStack);
//                return itemStack;
//            }
//            enchantment2 = (Enchantment)list.get(randomSource.nextInt(list.size()));
//        Provider provider = this.providers.get(randomSource.nextInt(this.providers.size()));
//        return EnchantRandomlyFunction.enchantItem(itemStack, enchantment2, randomSource);
//        EnchantRandomlyFunction
        return ItemStack.EMPTY;
    }

    // 1.18.2
//    @Override
//    public ItemStack run(ItemStack itemStack, LootContext lootContext) {
//        Enchantment enchantment2;
//        Random random = lootContext.getRandom();
//        if (this.enchantments.isEmpty()) {
//            boolean bl = itemStack.is(Items.BOOK);
//            List list = Registry.ENCHANTMENT.stream().filter(Enchantment::isDiscoverable).filter(enchantment -> bl || enchantment.canEnchant(itemStack)).collect(Collectors.toList());
//            if (list.isEmpty()) {
//                LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)itemStack);
//                return itemStack;
//            }
//            enchantment2 = (Enchantment)list.get(random.nextInt(list.size()));
//        } else {
//            enchantment2 = this.enchantments.get(random.nextInt(this.enchantments.size()));
//        }
//        return EnchantRandomlyFunction.enchantItem(itemStack, enchantment2, random);
//    }


//    private static ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment, RandomSource randomSource) {
//        int i = Mth.nextInt(randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel());
//        if (itemStack.is(Items.BOOK)) {
//            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
//            EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, i));
//        } else {
//            itemStack.enchant(enchantment, i);
//        }
//        return itemStack;
//    }

    public static class Provider {
    }

    public static class Serializer implements ILootConditionalFunction.Serializer<SkinRandomlyFunction> {

        @Override
        public void serialize(IDataPackObject object, SkinRandomlyFunction function) {
//            if (!enchantRandomlyFunction.enchantments.isEmpty()) {
//                JsonArray jsonArray = new JsonArray();
//                for (Enchantment enchantment : enchantRandomlyFunction.enchantments) {
//                    ResourceLocation resourceLocation = Registry.ENCHANTMENT.getKey(enchantment);
//                    if (resourceLocation == null) {
//                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
//                    }
//                    jsonArray.add(new JsonPrimitive(resourceLocation.toString()));
//                }
//                jsonObject.add("enchantments", jsonArray);
//            }
        }

        @Override
        public SkinRandomlyFunction deserialize(IDataPackObject object) {
            ArrayList<Provider> list = new ArrayList<>();
//            if (jsonObject.has("skins")) {
//                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "enchantments");
//                for (JsonElement jsonElement : jsonArray) {
//                    String string = GsonHelper.convertToString(jsonElement, "enchantment");
//                    Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
//                    list.add(enchantment);
//                }
//            }
            return new SkinRandomlyFunction(list);
        }
    }
}
