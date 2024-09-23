package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.utils.ItemMatcher;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ModItemMatchers {

    public static final ItemMatcher SWORDS = MatcherBuilder.of()
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
            .build();

    public static final ItemMatcher TRIDENTS = MatcherBuilder.of()
            .match("trident")
            .match("lance")
            .match("halbred")
            .match("spear")
            .build();

    public static final ItemMatcher SHIELDS = MatcherBuilder.of()
            .match("shield")
            .build();

    public static final ItemMatcher BOWS = MatcherBuilder.of()
            .match("bow")
            .nonMatch("bowl")
            .build();

    public static final ItemMatcher PICKAXES = MatcherBuilder.of()
            .match("pickaxe")
            .build();

    public static final ItemMatcher AXES = MatcherBuilder.of()
            .match("axe")
            .nonMatch("pickaxe")
            .nonMatch("waxed")
            .build();

    public static final ItemMatcher SHOVELS = MatcherBuilder.of()
            .match("shovel")
            .build();

    public static final ItemMatcher HOES = MatcherBuilder.of()
            .match("hoe")
            .build();


    private static class MatcherBuilder {

        private final StringBuffer mathBuffer = new StringBuffer();
        private final StringBuffer nonMathBuffer = new StringBuffer();

        private final ArrayList<String> whitelist = new ArrayList<>();
        private final ArrayList<String> blacklist = new ArrayList<>();

        private Predicate<ItemStack> requirements;

        private static MatcherBuilder of() {
            return new MatcherBuilder();
        }

        private MatcherBuilder match(String tag) {
            if (!mathBuffer.isEmpty()) {
                mathBuffer.append("|");
            }
            mathBuffer.append(tag);
            return this;
        }

        private MatcherBuilder nonMatch(String tag) {
            if (!nonMathBuffer.isEmpty()) {
                nonMathBuffer.append("|");
            }
            nonMathBuffer.append(tag);
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
            return new ItemMatcher(mathBuffer.toString(), nonMathBuffer.toString(), whitelist, blacklist, requirements);
        }
    }
}
