package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.sun.org.apache.regexp.internal.RE;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.color.Palette;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AWPaletteBox extends Button {

    private final int row;
    private final int column;

    private final int cellWidth;
    private final int cellHeight;

    private Palette palette;
    private int selectedIndex = 0;

    public AWPaletteBox(int x, int y, int width, int height, int column, int row, Button.IPressable changeHandler) {
        super(x, y, width, height, StringTextComponent.EMPTY, changeHandler, NO_TOOLTIP);
        this.row = row;
        this.column = column;
        this.cellWidth = (width - 2) / column;
        this.cellHeight = (height - 2) / row;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
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
            int i = (int)(mouseX - x - 1) / cellWidth;
            int j = (int)(mouseY - y - 1) / cellHeight;
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
