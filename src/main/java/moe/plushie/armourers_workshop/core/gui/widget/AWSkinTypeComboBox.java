package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AWSkinTypeComboBox extends AWComboBox {

    protected final List<ISkinType> skinTypes;

    public AWSkinTypeComboBox(int x, int y, int width, int height, List<ISkinType> skinTypes, ISkinType skinType, Consumer<ISkinType> changeHandler) {
        super(x, y, width, height, getComboItemsFromSkinTypes(skinTypes), skinTypes.indexOf(skinType), button -> {
            if (button instanceof AWSkinTypeComboBox) {
                changeHandler.accept(((AWSkinTypeComboBox) button).getSelectedSkin());
            }
        });
        this.skinTypes = skinTypes;
    }

    private static ITextComponent getTitleFromSkinType(ISkinType skinType) {
        if (skinType == SkinTypes.UNKNOWN) {
            return TranslateUtils.title("inventory.armourers_workshop.all");
        }
        return TranslateUtils.title("skinType." + skinType.getRegistryName());
    }

    private static List<AWComboBox.ComboItem> getComboItemsFromSkinTypes(List<ISkinType> skinTypes) {
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinType skinType : skinTypes) {
            items.add(new ComboItem(skinType));
        }
        return items;
    }

    @Nullable
    public ISkinType getSelectedSkin() {
        int index = getSelectedIndex();
        if (index >= 0 && index < skinTypes.size()) {
            return skinTypes.get(index);
        }
        return null;
    }

    public void setSelectedSkin(@Nullable ISkinType skinType) {
        super.setSelectedIndex(skinTypes.indexOf(skinType));
    }

    public List<ISkinType> getSkinTypes() {
        return skinTypes;
    }

    public static class ComboItem extends AWComboBox.ComboItem {

        protected final ISkinType skinType;

        public ComboItem(ISkinType skinType) {
            super(getTitleFromSkinType(skinType));
            this.skinType = skinType;
        }

        @Override
        public void renderLabels(MatrixStack matrixStack, int x, int y, int width, int height, boolean isHovered, boolean isTopRender) {
            if (!isTopRender) {
                ResourceLocation texture = AWCore.getItemIcon(skinType);
                if (texture != null) {
                    RenderSystem.enableAlphaTest();
                    RenderUtils.resize(matrixStack, x - 2, y - 1, 0, 0, 9, 9, 16, 16, 16, 16, texture);
                    x += 9;
                }
            }
            super.renderLabels(matrixStack, x, y, width, height, isHovered, isTopRender);
        }
    }
}
