package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import moe.plushie.armourers_workshop.api.skin.property.ISkinProperty;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerAdvancedSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerBaseSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerBlockSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerChestSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerFeetSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerHeadSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerLegSkinPanel;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.panel.ArmourerWingsSkinPanel;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateArmourerPacket;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenProperties;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ArmourerSkinSetting extends ArmourerBaseSetting {

    public static final Map<SkinType, Function<SkinProperties, ArmourerBaseSkinPanel>> REGISTERED = Collections.immutableMap(it -> {
        it.put(SkinTypes.ARMOR_HEAD, ArmourerHeadSkinPanel::new);
        it.put(SkinTypes.ARMOR_CHEST, ArmourerChestSkinPanel::new);
        it.put(SkinTypes.ARMOR_LEGS, ArmourerLegSkinPanel::new);
        it.put(SkinTypes.ARMOR_FEET, ArmourerFeetSkinPanel::new);
        it.put(SkinTypes.ARMOR_WINGS, ArmourerWingsSkinPanel::new);
        it.put(SkinTypes.BLOCK, ArmourerBlockSkinPanel::new);
        it.put(SkinTypes.ADVANCED, ArmourerAdvancedSkinPanel::new);
    });

    protected final DifferenceSkinProperties skinProperties = new DifferenceSkinProperties();
    protected final ArmourerBlockEntity blockEntity;

    protected ArmourerBaseSkinPanel screen;

    public ArmourerSkinSetting(ArmourerMenu container) {
        super("armourer.skinSettings");
        this.blockEntity = container.getBlockEntity();
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
        var skinType = blockEntity.skinType();
        skinProperties.reset(blockEntity.skinProperties());
        var supplier = REGISTERED.get(skinType);
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
        var newValue = blockEntity.skinProperties().copy();
        this.skinProperties.applyTo(newValue);
        if (newValue.equals(blockEntity.skinProperties())) {
            return; // no changes
        }
        this.skinProperties.reset(newValue);
        this.blockEntity.setSkinProperties(newValue);
        NetworkManager.sendToServer(UpdateArmourerPacket.Field.SKIN_PROPERTIES.buildPacket(blockEntity, newValue));
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
        public void clear() {
            changes.clear();
            super.clear();
        }

        @Override
        public void putAll(OpenProperties properties) {
            changes.clear();
            super.putAll(properties);
        }

        public void reset(SkinProperties properties) {
            clear();
            putAll(properties);
        }

        public void applyTo(SkinProperties properties) {
            changes.values().forEach(co -> co.accept(this, properties));
        }
    }
}
