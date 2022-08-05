package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.common.builder.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenKeyModifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

@OnlyIn(value = Dist.CLIENT)
public class KeyBindingBuilderImpl<T extends IKeyBinding> implements IKeyBindingBuilder<T> {

    private static final ImmutableMap<IKeyModifier, KeyModifier> MODIFIERS1 = ImmutableMap.<IKeyModifier, KeyModifier>builder()
            .put(OpenKeyModifier.CONTROL, KeyModifier.CONTROL)
            .put(OpenKeyModifier.SHIFT, KeyModifier.SHIFT)
            .put(OpenKeyModifier.ALT, KeyModifier.ALT)
            .put(OpenKeyModifier.NONE, KeyModifier.NONE)
            .build();

    private static final ImmutableMap<KeyModifier, IKeyModifier> MODIFIERS2 = ImmutableMap.<KeyModifier, IKeyModifier>builder()
            .put(KeyModifier.CONTROL, OpenKeyModifier.CONTROL)
            .put(KeyModifier.SHIFT, OpenKeyModifier.SHIFT)
            .put(KeyModifier.ALT, OpenKeyModifier.ALT)
            .put(KeyModifier.NONE, OpenKeyModifier.NONE)
            .build();

    private static final ArrayList<Pair<KeyMapping, Supplier<Runnable>>> INPUTS = new ArrayList<>();

    private final String key;
    private IKeyModifier modifier = OpenKeyModifier.NONE;
    private String category = "";
    private Supplier<Runnable> handler;

    public KeyBindingBuilderImpl(String key) {
        this.key = key;
    }

    @Override
    public IKeyBindingBuilder<T> modifier(IKeyModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    @Override
    public IKeyBindingBuilder<T> category(String category) {
        this.category = category;
        return this;
    }

    @Override
    public IKeyBindingBuilder<T> bind(Supplier<Runnable> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public T build(String name) {
        String nameKey = "keys.armourers_workshop." + name;
        String categoryKey = "keys.armourers_workshop." + category;
        InputConstants.Key input = InputConstants.getKey(key);
        KeyModifier modifier1 = MODIFIERS1.getOrDefault(modifier, KeyModifier.NONE);
        KeyMapping binding = new OnceKeyBinding(nameKey, KeyConflictContext.IN_GAME, modifier1, input, categoryKey);
        if (handler != null) {
            INPUTS.add(Pair.of(binding, handler));
        }
        ClientRegistry.registerKeyBinding(binding);
        IKeyBinding binding1 = new IKeyBinding() {

            @Override
            public Component getKeyName() {
                return binding.getTranslatedKeyMessage();
            }

            @Override
            public IKeyModifier getKeyModifier() {
                return MODIFIERS2.getOrDefault(binding.getKeyModifier(), OpenKeyModifier.NONE);
            }
        };
        return ObjectUtils.unsafeCast(binding1);
    }

    public static class OnceKeyBinding extends KeyMapping {

        // Once consumed, must need to release the key to reset this flags.
        private boolean canConsumeClick = true;

        public OnceKeyBinding(String description, IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, InputConstants.Key keyCode, String category) {
            super(description, keyConflictContext, keyModifier, keyCode, category);
        }

        @Override
        public boolean consumeClick() {
            if (canConsumeClick && isDown()) {
                canConsumeClick = false;
                return true;
            }
            return false;
        }

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (!isDown) {
                canConsumeClick = true;
            }
        }
    }

    public static void tick() {
        INPUTS.forEach(pair -> {
            if (pair.getKey().consumeClick()) {
                pair.getValue().get().run();
            }
        });
    }
}
