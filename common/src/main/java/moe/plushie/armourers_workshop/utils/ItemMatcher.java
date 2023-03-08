package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.init.ModConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public class ItemMatcher {

    private final String name;
    private final Pattern pattern;

    public ItemMatcher(String name, String regex) {
        if (regex == null || regex.isEmpty()) {
            regex = name;
        }
        this.name = name;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public boolean test(ResourceLocation registryName) {
        // the system shutdown?
        if (!ModConfig.Common.enableMatchingByItemId) {
            return false;
        }
        // the item id in the blacklist?
        if (!ModConfig.Common.disableMatchingItems.contains(registryName.toString())) {
            return false;
        }
        // we only check the path part, to avoid keywords exists in the mod id.
        return pattern.matcher(registryName.getPath()).find();
    }
}
