package moe.plushie.armourers_workshop.init.platform.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.init.platform.ClientBuilderManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ext.KeyModifierX;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ClientBuilderManagerImpl implements ClientBuilderManager.Impl {

    private static final ArrayList<Pair<KeyMapping, Supplier<Runnable>>> INPUTS = new ArrayList<>();
    private static final ClientBuilderManagerImpl INSTANCE = new ClientBuilderManagerImpl();

    public static ClientBuilderManager.Impl getInstance() {
        return INSTANCE;
    }

    @Override
    public <T extends IKeyBinding> IKeyBindingBuilder<T> createKeyBindingBuilder(String key) {
        return new IKeyBindingBuilder<T>() {
            private IKeyModifier modifier = KeyModifierX.NONE;
            private String category = "";
            private Supplier<Runnable> handler;
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
                KeyMapping binding = new OnceKeyBinding(
                        nameKey,
                        InputConstants.Type.KEYSYM,
                        input.getValue(),
                        categoryKey
                );
                if (handler != null) {
                    INPUTS.add(Pair.of(binding, handler));
                }
                KeyBindingHelper.registerKeyBinding(binding);
                IKeyBinding binding1 = new IKeyBinding() {
                    @Override
                    public boolean consumeClick() {
                        return binding.consumeClick();
                    }

                    @Override
                    public Component getKeyName() {
                        return binding.getTranslatedKeyMessage();
                    }

                    @Override
                    public IKeyModifier getKeyModifier() {
                        return KeyModifierX.NONE;
                    }
                };
                return ObjectUtils.unsafeCast(binding1);
            }
        };
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

    public static void onInputTick() {
        INPUTS.forEach(pair -> {
            if (pair.getKey().consumeClick()) {
                pair.getValue().get().run();
            }
        });
    }
}
