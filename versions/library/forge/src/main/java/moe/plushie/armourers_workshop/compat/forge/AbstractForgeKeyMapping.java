package moe.plushie.armourers_workshop.compat.forge;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.core.utils.FastMapper;
import moe.plushie.armourers_workshop.init.event.client.RegisterKeyMappingsEvent;
import moe.plushie.armourers_workshop.utils.OpenKeyModifier;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Available("[1.21, 1.22)")
public abstract class AbstractForgeKeyMapping extends KeyMapping {

    private static final HashMap<InputConstants.Key, ArrayList<KeyMapping>> MAPPINGS = new HashMap<>();

    private static final FastMapper<OpenKeyModifier, KeyModifier> MODIFIER_MAPPER = FastMapper.builder(OpenKeyModifier.NONE, KeyModifier.NONE, it -> {
        it.put(OpenKeyModifier.CONTROL, KeyModifier.CONTROL);
        it.put(OpenKeyModifier.SHIFT, KeyModifier.SHIFT);
        it.put(OpenKeyModifier.ALT, KeyModifier.ALT);
        it.put(OpenKeyModifier.NONE, KeyModifier.NONE);
    });

    public AbstractForgeKeyMapping(String description, IKeyModifier keyModifier, InputConstants.Key keyCode, String category) {
        super(description, KeyConflictContext.IN_GAME, MODIFIER_MAPPER.getValue((OpenKeyModifier) keyModifier), keyCode, category);
        bind(keyCode, this);
    }

    public static void register(String key, KeyMapping keyMapping) {
        EventBus.register(RegisterKeyMappingsEvent.class, event -> event.register(keyMapping));
    }

    private static void bind(InputConstants.Key keyCode, KeyMapping keyMapping) {
        MAPPINGS.computeIfAbsent(keyCode, k -> new ArrayList<>()).add(keyMapping);
    }

    private static void unbind(InputConstants.Key keyCode, KeyMapping keyMapping) {
        var mappings = MAPPINGS.get(keyCode);
        if (mappings != null) {
            mappings.remove(keyMapping);
        }
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
    public boolean consumeClick() {
        return super.consumeClick();
    }

    public OpenKeyModifier getOpenKeyModifier() {
        return MODIFIER_MAPPER.getKey(getKeyModifier());
    }

    @Override
    public void setKeyModifierAndCode(KeyModifier keyModifier, InputConstants.Key keyCode) {
        unbind(getKey(), this);
        super.setKeyModifierAndCode(keyModifier, keyCode);
        bind(getKey(), this);
    }
}
