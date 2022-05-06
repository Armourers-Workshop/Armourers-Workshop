package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Size2i;

import java.awt.*;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class AWInventoryBox extends Button {

    protected ResourceLocation texture;
    protected int u;
    protected int v;

    protected Point offset = new Point(0, 0);
    protected Size2i itemSize = new Size2i(10, 10);

    public AWInventoryBox(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.IPressable handler) {
        super(x, y, width, height, StringTextComponent.EMPTY, handler);
        this.texture = texture;
        this.u = u;
        this.v = v;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }

    public Size2i getItemSize() {
        return itemSize;
    }

    public void setItemSize(Size2i itemSize) {
        this.itemSize = itemSize;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        if (!visible) {
            return;
        }
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        RenderUtils.bind(texture);
        int hoverWidth = MathHelper.clamp(mouseX - x, 0, width);
        int hoverHeight = MathHelper.clamp(mouseY - y, 0, height);
        for (int iy = 0; iy < height; iy += itemSize.height) {
            for (int ix = 0; ix < width; ix += itemSize.width) {
                int iu = u;
                if (ix <= offset.x && iy <= offset.y) {
                    iu += itemSize.width;
                }
                int iv = v;
                if (ix <= hoverWidth && iy <= hoverHeight && isHovered) {
                    iv += itemSize.height;
                }
                RenderUtils.blit(matrixStack, x + ix, y + iy, iu, iv, itemSize.width, itemSize.height);
            }
        }
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return false;
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        if (super.clicked(mouseX, mouseY)) {
            offset = new Point((int) (mouseX - x), (int) (mouseY - y));
            return true;
        }
        return false;
    }
}
