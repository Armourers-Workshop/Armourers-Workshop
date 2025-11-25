package moe.plushie.armourers_workshop.compat.forge;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Available("[1.21, )")
public abstract class AbstractForgeKeyMappingImpl extends KeyMapping {

    private static final HashMap<InputConstants.Key, ArrayList<KeyMapping>> MAPPINGS = new HashMap<>();

    public AbstractForgeKeyMappingImpl(OpenResourceLocation name, InputConstants.Key key, KeyModifier modifier, AbstractForgeKeyCategory category) {
        super(name.toLanguageKey("key"), category.context(), modifier, key, category.category());
        bind(getKey(), this);
    }

    public static Collection<IKeyModifier> wrap(KeyModifier modifier) {
        var modifiers = new ArrayList<IKeyModifier>();
        switch (modifier) {
            case ALT -> modifiers.add(OpenKeyModifier.ALT);
            case SHIFT -> modifiers.add(OpenKeyModifier.SHIFT);
            case CONTROL -> modifiers.add(OpenKeyModifier.CONTROL);
        }
        return modifiers;
    }

    public static KeyModifier unwrap(Collection<IKeyModifier> modifiers) {
        var value = KeyModifier.NONE;
        for (var modifier : modifiers) {
            if (modifier == OpenKeyModifier.CONTROL) {
                value = KeyModifier.CONTROL;
            } else if (modifier == OpenKeyModifier.SHIFT) {
                value = KeyModifier.SHIFT;
            } else if (modifier == OpenKeyModifier.ALT) {
                value = KeyModifier.ALT;
            }
        }
        return value;
    }

    @Nullable
    public static List<KeyMapping> findKeysByCode(InputConstants.Key keyCode) {
        // fix the neo forge wrong active modifier by `ctrl-shift-<key>`
        var mappings = MAPPINGS.get(keyCode);
        if (mappings != null) {
            return mappings.stream()
                    .filter(binding -> binding.isActiveAndMatches(keyCode))
                    .toList();
        }
        return null;
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, InputConstants.Key keyCode) {
        unbind(getKey(), this);
        super.setKeyModifierAndCode(keyModifier, keyCode);
        bind(getKey(), this);
    }

    private void bind(InputConstants.Key keyCode, KeyMapping keyMapping) {
        MAPPINGS.computeIfAbsent(keyCode, k -> new ArrayList<>()).add(keyMapping);
    }

    private void unbind(InputConstants.Key keyCode, KeyMapping keyMapping) {
        var mappings = MAPPINGS.get(keyCode);
        if (mappings != null) {
            mappings.remove(keyMapping);
        }
    }
}
