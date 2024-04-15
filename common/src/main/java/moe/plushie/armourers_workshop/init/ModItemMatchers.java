package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.utils.ItemMatcher;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ModItemMatchers {

    public static final ItemMatcher SWORDS = builder()
            .match("sword")
            .match("machete")
            .match("gladius")
//            .match("shortsword") // contains in sword
            .match("falchion")
//            .match("broodsword") // contains in sword
            .match("saber")
            .match("dagger")
            .match("cullass")
            .match("rapier")
//            .match("longsword") // contains in sword
            .match("claymore")
            .match("flamberge")
            .match("zweihander")
            .match("dao")
            .match("jian")
//            .match("dadao") // contains in dao
//            .match("hooksword") // contains in sword
//            .match("kodachi") // contains in odachi
            .match("odachi")
            .match("tachi")
            .match("wakizashi")
            .match("katana")
            .match("chokuto")
            .match("ninjato")
            .match("scimitar")
            .match("shamshir")
            .required(Attributes.ATTACK_DAMAGE)
            .build();

    public static final ItemMatcher TRIDENTS = builder()
            .match("trident")
            .match("lance")
            .match("halbred")
            .match("spear")
            .required(Attributes.ATTACK_DAMAGE)
            .build();

    public static final ItemMatcher SHIELDS = simple("shield");
    public static final ItemMatcher BOWS = simple("bow");

    public static final ItemMatcher PICKAXES = simple("pickaxe");
    public static final ItemMatcher AXES = simple("(?<!pick)axe");
    public static final ItemMatcher SHOVELS = simple("shovel");
    public static final ItemMatcher HOES = simple("hoe");


    private static ItemMatcher simple(String name) {
        return builder().match(name).build();
    }

    private static MatcherBuilder builder() {
        return new MatcherBuilder();
    }

    private static class MatcherBuilder {

        private final StringBuffer buffer;
        private final ArrayList<String> whitelist = new ArrayList<>();
        private final ArrayList<String> blacklist = new ArrayList<>();
        private Predicate<ItemStack> requirements;

        private MatcherBuilder() {
            buffer = new StringBuffer();
        }

        private MatcherBuilder required(Attribute attr) {
            requirements = item -> item.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(attr);
            return this;
        }

        private MatcherBuilder match(String tag) {
            if (buffer.length() != 0) {
                buffer.append("|");
            }
            buffer.append(tag);
            return this;
        }

        private MatcherBuilder add(String tag) {
            whitelist.add(tag);
            return this;
        }

        private MatcherBuilder remove(String tag) {
            blacklist.add(tag);
            return this;
        }

        private ItemMatcher build() {
            return new ItemMatcher(buffer.toString(), whitelist, blacklist, requirements);
        }
    }
}
