package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.Minecraft;

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
            .build();

    protected final DifferenceSkinProperties skinProperties = new DifferenceSkinProperties();
    protected final ArmourerBlockEntity tileEntity;
    protected final HashMap<ISkinType, ArmourerBaseSkinPanel> screens = new HashMap<>();

    protected ArmourerBaseSkinPanel screen;

    public ArmourerSkinSetting(ArmourerMenu container) {
        super("inventory.armourers_workshop.armourer.skinSettings");
        this.tileEntity = container.getTileEntity();
    }

    @Override
    public void reloadData() {
        ISkinType skinType = tileEntity.getSkinType();
        skinProperties.copyFrom(tileEntity.getSkinProperties());
        screen = screens.get(skinType);
        if (screen == null) {
            Function<SkinProperties, ArmourerBaseSkinPanel> supplier = REGISTERED.get(skinType);
            if (supplier != null) {
                screen = supplier.apply(skinProperties);
                screen.setApplier(this::updateSkinProperties);
                screens.put(skinType, screen);
            }
        }
        if (screen == null) {
            return;
        }
        screen.leftPos = leftPos;
        screen.topPos = topPos;
        screen.init(Minecraft.getInstance(), width, height);
        children.clear();
        children.add(screen);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        this.reloadData();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float p_230430_4_) {
        super.render(matrixStack, mouseX, mouseY, p_230430_4_);
        if (screen != null) {
            screen.render(matrixStack, mouseX, mouseY, p_230430_4_);
        }
    }

    private void updateSkinProperties(SkinProperties skinProperties) {
        SkinProperties skinProperties1 = new SkinProperties(tileEntity.getSkinProperties());
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
