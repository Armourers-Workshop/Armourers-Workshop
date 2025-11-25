package moe.plushie.armourers_workshop.compat.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyModifier;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.client.KeyMapping;

import java.util.Collection;

@Available("[1.16, )")
public abstract class AbstractFabricKeyMappingImpl extends KeyMapping {

    public AbstractFabricKeyMappingImpl(OpenResourceLocation name, InputConstants.Key key, Collection<IKeyModifier> modifiers, AbstractFabricKeyCategory category) {
        super(name.toLanguageKey("key"), key.getType(), key.getValue(), category.category());
    }
}
