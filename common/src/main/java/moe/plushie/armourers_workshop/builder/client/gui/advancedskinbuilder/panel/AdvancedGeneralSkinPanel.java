package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.init.ModTextures;

import javax.swing.*;

public class AdvancedGeneralSkinPanel extends AdvancedSkinPanel {

    private final AdvancedSkinBuilderBlockEntity blockEntity;

    public AdvancedGeneralSkinPanel(AdvancedSkinBuilderBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.barItem.setImage(UIImage.of(ModTextures.TAB_ICONS).uv(16, 128).fixed(16, 16).build());
        this.setup();
    }

    private void setup() {

        addGroup(translatable("properties"), builder -> {
            builder.bool(translatable("mirror"), blockEntity.mirror2);
            builder.bool(translatable("enabled"), blockEntity.enabled2);
        });

        addGroup(translatable("transform"), builder -> {
            builder.vector(translatable("location"), blockEntity.location2, Group.Unit.POINT);
            builder.vector(translatable("rotation"), blockEntity.rotation2, Group.Unit.DEGREES);
            builder.vector(translatable("scale"), blockEntity.scale2, Group.Unit.SCALE);
        });
    }
}
