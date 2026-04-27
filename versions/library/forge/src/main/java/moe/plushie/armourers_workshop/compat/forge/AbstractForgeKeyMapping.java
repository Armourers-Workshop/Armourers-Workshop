package moe.plushie.armourers_workshop.compat.forge;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.api.client.key.IKeyMapping;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;
import net.minecraft.network.chat.Component;

import java.util.Collection;

@Available("[16, )")
public class AbstractForgeKeyMapping extends AbstractForgeKeyMappingImpl implements IKeyMapping {

    private boolean canConsumeClick = false;

    private final IKeyCategory category;

    public AbstractForgeKeyMapping(OpenResourceKey name, String key, Collection<IKeyModifier> modifiers, IKeyCategory category) {
        super(name, unwrap(key), unwrap(modifiers), AbstractForgeKeyCategory.unwrap(category));
        this.category = category;
    }

    public static InputConstants.Key unwrap(String key) {
        return InputConstants.getKey(key);
    }

    public static AbstractForgeKeyMapping unwrap(IKeyMapping keyMapping){
        return Objects.unsafeCast(keyMapping);
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

    @Override
    public Component name() {
        return getTranslatedKeyMessage();
    }

    @Override
    public IKeyCategory category() {
        return category;
    }

    @Override
    public Collection<? extends IKeyModifier> modifiers() {
        return wrap(getKeyModifier());
    }
}
