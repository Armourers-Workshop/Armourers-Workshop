package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ItemMatcher {

    private final Pattern pattern;
    private final Collection<String> whitelist;
    private final Collection<String> blacklist;
    private final Predicate<ItemStack> requirements;

    public ItemMatcher(String regex, Collection<String> whitelist, Collection<String> blacklist, Predicate<ItemStack> requirements) {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.requirements = requirements;
    }

    public boolean test(ResourceLocation registryName, ItemStack itemStack) {
        // the item id in the whitelist?
        String id = registryName.toString();
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
        if (pattern.matcher(registryName.getPath()).find()) {
            // check the item the requirements.
            return requirements == null || requirements.test(itemStack);
        }
        return false;
    }
}
