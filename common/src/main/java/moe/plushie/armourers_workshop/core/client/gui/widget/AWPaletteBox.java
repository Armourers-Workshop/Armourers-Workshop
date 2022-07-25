package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.data.palette.Palette;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

@Environment(value = EnvType.CLIENT)
public class AWPaletteBox extends Button {

    private final int row;
    private final int column;

    private final int cellWidth;
    private final int cellHeight;

    private Palette palette;
    private int selectedIndex = 0;

    public AWPaletteBox(int x, int y, int width, int height, int column, int row, Button.OnPress changeHandler) {
        super(x, y, width, height, TextComponent.EMPTY, changeHandler, NO_TOOLTIP);
        this.row = row;
        this.column = column;
        this.cellWidth = (width - 2) / column;
        this.cellHeight = (height - 2) / row;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        RenderUtils.tile(matrixStack, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, RenderUtils.TEX_WIDGETS);

        if (palette == null) {
            return;
        }

        for (int j = 0; j < row; ++j) {
            for (int i = 0; i < column; ++i) {
                int index = j * column + i;
                int cx = x + i * cellWidth + 1;
                int cy = y + j * cellHeight + 1;
                fill(matrixStack, cx, cy, cx + cellWidth, cy + cellHeight, palette.getColor(index) | 0xff000000);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            int i = (int) (mouseX - x - 1) / cellWidth;
            int j = (int) (mouseY - y - 1) / cellHeight;
            if (i < 0 || j < 0 || i >= column || j >= row) {
                return false;
            }
            selectedIndex = j * column + i;
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }
}
