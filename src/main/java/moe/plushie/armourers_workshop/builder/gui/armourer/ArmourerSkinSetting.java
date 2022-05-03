package moe.plushie.armourers_workshop.builder.gui.armourer;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.builder.gui.armourer.panel.*;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;

import java.util.function.Supplier;

public class ArmourerSkinSetting extends ArmourerBaseSetting {

    public static final ImmutableMap<ISkinType, Supplier<Object>> CONFIGS = ImmutableMap.<ISkinType, Supplier<Object>>builder()
            .put(SkinTypes.ARMOR_HEAD, ArmourerHeadPanel::new)
            .put(SkinTypes.ARMOR_CHEST, ArmourerChestPanel::new)
            .put(SkinTypes.ARMOR_LEGS, ArmourerLegPanel::new)
            .put(SkinTypes.ARMOR_FEET, ArmourerLegPanel::new)
            .put(SkinTypes.ARMOR_WINGS, ArmourerWingsPanel::new)
            .put(SkinTypes.BLOCK, ArmourerBlockPanel::new)
            .build();

    protected ArmourerSkinSetting(ArmourerContainer container) {
        super("inventory.armourers_workshop.armourer.skinSettings");
    }
}
