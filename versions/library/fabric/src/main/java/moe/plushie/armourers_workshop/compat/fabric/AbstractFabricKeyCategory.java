package moe.plushie.armourers_workshop.compat.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.key.IKeyCategory;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.network.chat.Component;

@Available("[1.16, 1.22)")
public class AbstractFabricKeyCategory implements IKeyCategory {

    private final OpenResourceLocation name;

    public AbstractFabricKeyCategory(OpenResourceLocation name) {
        this.name = name;
    }

    public static AbstractFabricKeyCategory unwrap(IKeyCategory category) {
        return Objects.unsafeCast(category);
    }

    @Override
    public Component name() {
        return Component.translatable(category());
    }

    public String category() {
        return name.toLanguageKey("key.category");
    }
}
