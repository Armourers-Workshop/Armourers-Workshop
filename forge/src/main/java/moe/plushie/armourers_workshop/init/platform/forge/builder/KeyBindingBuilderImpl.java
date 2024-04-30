package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.client.key.IKeyBinding;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.api.registry.IKeyBindingBuilder;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeKeyMapping;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.client.RenderFrameEvent;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.ext.OpenKeyModifier;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Supplier;

public class KeyBindingBuilderImpl<T extends IKeyBinding> implements IKeyBindingBuilder<T> {

    private static final ArrayList<Pair<KeyMapping, Supplier<Runnable>>> INPUTS = createAndAttach();

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
        OnceKeyBinding binding = createKeyBinding(nameKey, modifier, input, categoryKey);
        if (handler != null) {
            INPUTS.add(Pair.of(binding, handler));
        }
        AbstractForgeKeyMapping.register(name, binding);
        IKeyBinding binding1 = new IKeyBinding() {

            @Override
            public Component getKeyName() {
                return binding.getTranslatedKeyMessage();
            }

            @Override
            public IKeyModifier getKeyModifier() {
                return binding.getOpenKeyModifier();
            }
        };
        return ObjectUtils.unsafeCast(binding1);
    }

    public static class OnceKeyBinding extends AbstractForgeKeyMapping {

        // Once consumed, must need to release the key to reset this flags.
        private boolean canConsumeClick = true;

        public OnceKeyBinding(String description, IKeyModifier keyModifier, InputConstants.Key keyCode, String category) {
            super(description, keyModifier, keyCode, category);
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

    private static OnceKeyBinding createKeyBinding(String description, IKeyModifier keyModifier, InputConstants.Key keyCode, String category) {
        return new OnceKeyBinding(description, keyModifier, keyCode, category);
    }

    private static <T> ArrayList<T> createAndAttach() {
        // attach the input event to client.
        EventManager.listen(RenderFrameEvent.Post.class, event -> INPUTS.forEach(pair -> {
            if (pair.getKey().consumeClick()) {
                pair.getValue().get().run();
            }
        }));
        return new ArrayList<>();
    }
}
