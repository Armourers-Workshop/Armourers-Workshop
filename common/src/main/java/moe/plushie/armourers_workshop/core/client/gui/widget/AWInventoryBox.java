package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.math.Size2i;
import moe.plushie.armourers_workshop.utils.math.Vector2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

@Environment(value = EnvType.CLIENT)
public class AWInventoryBox extends Button {

    protected ResourceLocation texture;
    protected int u;
    protected int v;

    protected Vector2i offset = new Vector2i(0, 0);
    protected Size2i itemSize = new Size2i(10, 10);

    public AWInventoryBox(int x, int y, int width, int height, int u, int v, ResourceLocation texture, Button.OnPress handler) {
        super(x, y, width, height, TextComponent.EMPTY, handler);
        this.texture = texture;
        this.u = u;
        this.v = v;
    }

    public Vector2i getOffset() {
        return offset;
    }

    public void setOffset(Vector2i offset) {
        this.offset = offset;
    }

    public Size2i getItemSize() {
        return itemSize;
    }

    public void setItemSize(Size2i itemSize) {
        this.itemSize = itemSize;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        if (!visible) {
            return;
        }
        isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        RenderUtils.bind(texture);
        int hoverWidth = MathUtils.clamp(mouseX - x, 0, width);
        int hoverHeight = MathUtils.clamp(mouseY - y, 0, height);
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
            offset = new Vector2i((int) (mouseX - x), (int) (mouseY - y));
            return true;
        }
        return false;
    }
}
