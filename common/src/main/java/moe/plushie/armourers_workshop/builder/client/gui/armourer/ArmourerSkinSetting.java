package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.*;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class ArmourerSkinSetting extends ArmourerBaseSetting {

    public static final ImmutableMap<ISkinType, Function<SkinProperties, ArmourerBaseSkinPanel>> REGISTERED = ImmutableMap.<ISkinType, Function<SkinProperties, ArmourerBaseSkinPanel>>builder()
            .put(SkinTypes.ARMOR_HEAD, ArmourerHeadSkinPanel::new)
            .put(SkinTypes.ARMOR_CHEST, ArmourerChestSkinPanel::new)
            .put(SkinTypes.ARMOR_LEGS, ArmourerLegSkinPanel::new)
            .put(SkinTypes.ARMOR_FEET, ArmourerFeetSkinPanel::new)
            .put(SkinTypes.ARMOR_WINGS, ArmourerWingsSkinPanel::new)
            .put(SkinTypes.BLOCK, ArmourerBlockSkinPanel::new)
            .put(SkinTypes.ADVANCED, ArmourerAdvancedSkinPanel::new)
            .build();

    protected final DifferenceSkinProperties skinProperties = new DifferenceSkinProperties();
    protected final ArmourerBlockEntity tileEntity;

    protected ArmourerBaseSkinPanel screen;

    public ArmourerSkinSetting(ArmourerMenu container) {
        super("inventory.armourers_workshop.armourer.skinSettings");
        this.tileEntity = container.getTileEntity();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        if (screen != null) {
            screen.setFrame(bounds());
        }
    }

    @Override
    public void reloadData() {
        ISkinType skinType = tileEntity.getSkinType();
        skinProperties.copyFrom(tileEntity.getSkinProperties());
        Function<SkinProperties, ArmourerBaseSkinPanel> supplier = REGISTERED.get(skinType);
        if (supplier != null) {
            updateScreen(supplier.apply(skinProperties));
            screen.setApplier(this::updateSkinProperties);
            screen.setFrame(bounds());
            screen.init();
        } else {
            updateScreen(null);
        }
    }

    @Override
    public void init() {
        super.init();
        this.reloadData();
    }

    private void updateSkinProperties(SkinProperties skinProperties) {
        SkinProperties skinProperties1 = SkinProperties.create(tileEntity.getSkinProperties());
        this.skinProperties.applyTo(skinProperties1);
        if (skinProperties1.equals(tileEntity.getSkinProperties())) {
            return; // no changes
        }
        this.skinProperties.copyFrom(skinProperties1);
        this.tileEntity.setSkinProperties(skinProperties);
        UpdateArmourerPacket.Field field = UpdateArmourerPacket.Field.SKIN_PROPERTIES;
        UpdateArmourerPacket packet = new UpdateArmourerPacket(tileEntity, field, skinProperties);
        NetworkManager.sendToServer(packet);
    }

    private void updateScreen(ArmourerBaseSkinPanel view) {
        if (screen == view) {
            return;
        }
        if (screen != null) {
            screen.removeFromSuperview();
        }
        screen = view;
        if (screen != null) {
            addSubview(screen);
        }
    }

    public static class DifferenceSkinProperties extends SkinProperties {

        protected HashMap<ISkinProperty<?>, BiConsumer<SkinProperties, SkinProperties>> changes = new HashMap<>();

        @Override
        public <T> void put(ISkinProperty<T> property, T value) {
            changes.put(property, (src, dest) -> dest.put(property, src.get(property)));
            super.put(property, value);
        }

        @Override
        public <T> void remove(ISkinProperty<T> property) {
            changes.put(property, (src, dest) -> dest.remove(property));
            super.remove(property);
        }

        @Override
        public void copyFrom(SkinProperties properties) {
            super.copyFrom(properties);
            this.changes.clear();
        }

        public void applyTo(SkinProperties properties) {
            changes.values().forEach(co -> co.accept(this, properties));
        }
    }
}
