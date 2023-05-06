package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.init.platform.ClientNativeManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenKeyModifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class KeyBindingBuilderImpl<T extends IKeyBinding> implements IKeyBindingBuilder<T> {

    private static final ArrayList<Pair<KeyMapping, Supplier<Runnable>>> INPUTS = createAndAttach();

    private IKeyModifier modifier = OpenKeyModifier.NONE;
    private String category = "";
    private Supplier<Runnable> handler;
    private final String key;

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
        KeyMapping binding = new OnceKeyBinding(nameKey, InputConstants.Type.KEYSYM, input.getValue(), categoryKey);
        if (handler != null) {
            INPUTS.add(Pair.of(binding, handler));
        }
        ClientNativeManager.getProvider().willRegisterKeyMapping(registry -> registry.register(binding));
        IKeyBinding binding1 = new IKeyBinding() {

            @Override
            public Component getKeyName() {
                return binding.getTranslatedKeyMessage();
            }

            @Override
            public IKeyModifier getKeyModifier() {
                return OpenKeyModifier.NONE;
            }
        };
        return ObjectUtils.unsafeCast(binding1);
    }

    public static class OnceKeyBinding extends KeyMapping {

        // Once consumed, must need to release the key to reset this flags.
        private boolean canConsumeClick = true;

        public OnceKeyBinding(String string, InputConstants.Type type, int i, String string2) {
            super(string, type, i, string2);
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

    private static <T> ArrayList<T> createAndAttach() {
        // attach the input event to client.
        ClientNativeManager.getProvider().willInput(ignored -> INPUTS.forEach(pair -> {
            if (pair.getKey().consumeClick()) {
                pair.getValue().get().run();
            }
        }));
        return new ArrayList<>();
    }
}
