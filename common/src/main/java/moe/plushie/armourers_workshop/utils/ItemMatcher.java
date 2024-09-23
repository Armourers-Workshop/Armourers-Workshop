package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ItemMatcher {

    private final Pattern matchPattern;
    private final Pattern nonMatchPattern;

    private final Collection<String> whitelist;
    private final Collection<String> blacklist;
    private final Predicate<ItemStack> requirements;

    public ItemMatcher(String matchRegex, String nonMatchRegex, Collection<String> whitelist, Collection<String> blacklist, Predicate<ItemStack> requirements) {
        this.matchPattern = tryCompile(matchRegex);
        this.nonMatchPattern = tryCompile(nonMatchRegex);
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.requirements = requirements;
    }

    public boolean test(IResourceLocation registryName, ItemStack itemStack) {
        // the item id in the whitelist?
        var id = registryName.toString();
        if (whitelist.contains(id)) {
            return true;
        }
        // the system shutdown?
        if (!ModConfig.Common.enableMatchingByItemId) {
            return false;
        }
        // the item id in the blacklist?
        if (ModConfig.Common.disableMatchingItems.contains(id) || blacklist.contains(id)) {
            return false;
        }
        // we only check the path part, to avoid keywords exists in the mod id.
        if (matchPattern != null && matchPattern.matcher(registryName.getPath()).find()) {
            if (nonMatchPattern != null && nonMatchPattern.matcher(registryName.getPath()).find()) {
                return false;
            }
            // check the item the requirements.
            return requirements == null || requirements.test(itemStack);
        }
        return false;
    }

    private Pattern tryCompile(String regex) {
        if (!regex.isEmpty()) {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        return null;
    }
}
