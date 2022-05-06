package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({"NullableProblems", "unused"})
@OnlyIn(Dist.CLIENT)
public class AWTextField extends Widget implements IRenderable, IGuiEventListener {
    protected final int lineHeight;
    protected final FontRenderer font;
    protected String lastValue;
    protected boolean isSingleLine = true;
    protected Insets contentInset = new Insets(4, 4, 4, 4);
    protected Consumer<String> returnHandler;
    protected String value = "";
    protected int maxLength = 32;
    protected int frame;
    protected boolean bordered = true;
    protected boolean canLoseFocus = true;
    protected boolean isEditable = true;
    protected boolean shiftPressed;
    protected int displayPos = 0;
    protected int cursorPos;
    protected int highlightPos;
    protected int textColor = 14737632;
    protected int textColorUneditable = 7368816;
    protected String suggestion;
    protected Consumer<String> responder;
    protected Predicate<String> filter = Objects::nonNull;
    protected BiFunction<String, Integer, IReorderingProcessor> formatter = (p_195610_0_, p_195610_1_) -> IReorderingProcessor.forward(p_195610_0_, Style.EMPTY);
    protected ArrayList<Line> wrappedTextLines;

    private boolean isEditing = false;
    private BiConsumer<AWTextField, EditEvent> eventListener;

    private int scrollAmount = 0;

    private int cursorPosX = 0;
    private int cursorPosY = 0;
    private int highlightPosX = 0;
    private int highlightPosY = 0;

    public AWTextField(FontRenderer font, int x, int y, int width, int height, ITextComponent message) {
        super(x, y, width, height, message);
        this.font = font;
        this.lineHeight = font.lineHeight;
    }

    public void setFrame(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.wrappedTextLines = null;
    }

    public void setEventListener(BiConsumer<AWTextField, EditEvent> eventListener) {
        this.eventListener = eventListener;
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    public void setFormatter(BiFunction<String, Integer, IReorderingProcessor> formatter) {
        this.formatter = formatter;
    }

    public void tick() {
        ++this.frame;
    }

    protected IFormattableTextComponent createNarrationMessage() {
        ITextComponent itextcomponent = this.getMessage();
        return new TranslationTextComponent("gui.narrate.editBox", itextcomponent, this.value);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String p_146180_1_) {
        if (this.filter.test(p_146180_1_)) {
            String oldValue = this.value;
            if (p_146180_1_.length() > this.maxLength) {
                this.value = p_146180_1_.substring(0, this.maxLength);
            } else {
                this.value = p_146180_1_;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(oldValue, this.value);
        }
    }

    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    public void setFilter(Predicate<String> p_200675_1_) {
        this.filter = p_200675_1_;
    }

    public void insertText(String p_146191_1_) {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (i - j);
        String s = formattedString(p_146191_1_);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }

        String s1 = (new StringBuilder(this.value)).replace(i, j, s).toString();
        if (this.filter.test(s1)) {
            String oldValue = this.value;
            this.value = s1;
            this.setCursorPosition(i + l);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(oldValue, this.value);
        }
    }

    public void onValueChange(String oldValue, String newValue) {
        if (this.responder != null) {
            this.responder.accept(newValue);
        }
        if (!Objects.equals(oldValue, newValue)) {
            if (this.eventListener != null) {
                this.eventListener.accept(this, EditEvent.CHANGE);
            }
        }
        this.nextNarration = Util.getMillis() + 500L;
    }

    public void deleteText(int p_212950_1_) {
        if (Screen.hasControlDown()) {
            this.deleteWords(p_212950_1_);
        } else {
            this.deleteChars(p_212950_1_);
        }

    }

    public void deleteWords(int p_146177_1_) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(p_146177_1_) - this.cursorPos);
            }
        }
    }

    public void deleteChars(int p_146175_1_) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(p_146175_1_);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    String s = (new StringBuilder(this.value)).delete(j, k).toString();
                    if (this.filter.test(s)) {
                        this.value = s;
                        this.moveCursorTo(j);
                    }
                }
            }
        }
    }

    public int getWordPosition(int p_146187_1_) {
        return this.getWordPosition(p_146187_1_, this.getCursorPosition());
    }

    public int getWordPosition(int p_146183_1_, int p_146183_2_) {
        return this.getWordPosition(p_146183_1_, p_146183_2_, true);
    }

    public int getWordPosition(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        int i = p_146197_2_;
        boolean flag = p_146197_1_ < 0;
        int j = Math.abs(p_146197_1_);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.value.length();
                i = this.value.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (p_146197_3_ && i < l && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (p_146197_3_ && i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursor(int p_146182_1_) {
        this.moveCursorTo(this.getCursorPos(p_146182_1_));
    }

    public int getCursorPos(int p_238516_1_) {
        return Util.offsetByCodepoints(this.value, this.cursorPos, p_238516_1_);
    }

    public void moveCursorTo(int p_146190_1_) {
        this.setCursorPosition(p_146190_1_);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        if (!this.canConsumeInput()) {
            return false;
        }

        this.shiftPressed = Screen.hasShiftDown();
        if (Screen.isSelectAll(key)) {
            this.moveCursorToEnd();
            this.setHighlightPos(0);
            return true;
        } else if (Screen.isCopy(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            return true;
        } else if (Screen.isPaste(key)) {
            if (this.isEditable) {
                this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
            }

            return true;
        } else if (Screen.isCut(key)) {
            Minecraft.getInstance().keyboardHandler.setClipboard(this.getHighlighted());
            if (this.isEditable) {
                this.insertText("");
            }

            return true;
        }
        switch (key) {
            case GLFW.GLFW_KEY_ESCAPE:
                // when text field is editing, must end editing, but we do not intercept events.
                if (this.isEditing) {
                    this.setFocus(false);
                }
                return false;

            case GLFW.GLFW_KEY_BACKSPACE:
                if (this.isEditable) {
                    this.shiftPressed = false;
                    this.deleteText(-1);
                    this.shiftPressed = Screen.hasShiftDown();
                }
                return true;

            case GLFW.GLFW_KEY_DELETE:
                if (this.isEditable) {
                    this.shiftPressed = false;
                    this.deleteText(1);
                    this.shiftPressed = Screen.hasShiftDown();
                }
                return true;

            case 260:
            case 266:
            case 267:
                return false;

            case GLFW.GLFW_KEY_RIGHT:
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(1));
                } else {
                    this.moveCursor(1);
                }

                return true;
            case GLFW.GLFW_KEY_LEFT:
                if (Screen.hasControlDown()) {
                    this.moveCursorTo(this.getWordPosition(-1));
                } else {
                    this.moveCursor(-1);
                }
                return true;

            case GLFW.GLFW_KEY_DOWN: {
                if (!this.isSingleLine && this.isFocused()) {
                    this.updateCursorPosWithMouse(cursorPosX + 2, cursorPosY + lineHeight);
                }
                return false;
            }
            case GLFW.GLFW_KEY_UP: {
                if (!this.isSingleLine && this.isFocused()) {
                    this.updateCursorPosWithMouse(cursorPosX + 2, cursorPosY - lineHeight);
                }
                return false;
            }
            case GLFW.GLFW_KEY_HOME: {
                this.moveCursorToStart();
                return true;
            }
            case GLFW.GLFW_KEY_END: {
                this.moveCursorToEnd();
                return true;
            }
            case GLFW.GLFW_KEY_ENTER: {
                if (!this.isSingleLine && this.isEditable) {
                    this.insertText(Character.toString('\n'));
                    return true;
                }
                if (returnHandler != null) {
                    returnHandler.accept(getValue());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    public boolean charTyped(char p_231042_1_, int p_231042_2_) {
        if (!this.canConsumeInput()) {
            return false;
        }
        if (SharedConstants.isAllowedChatCharacter(p_231042_1_)) {
            if (this.isEditable) {
                this.insertText(Character.toString(p_231042_1_));
            }
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isVisible()) {
            return false;
        }
        boolean flag = mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY < (y + height);
        if (this.canLoseFocus) {
            this.setFocus(flag);
        }
        if (this.isFocused() && flag && button == 0) {
            this.updateCursorPosWithMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public int getMaxScroll() {
        if (wrappedTextLines != null) {
            return Math.max(wrappedTextLines.size() * lineHeight - getInnerHeight(), 0);
        }
        return 0;
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(int scrollAmount) {
        this.scrollAmount = MathHelper.clamp(scrollAmount, 0, this.getMaxScroll());
    }

    private void scroll(int p_230937_1_) {
        this.setScrollAmount(this.getScrollAmount() + p_230937_1_);
    }

    public void ensureVisible(int pos) {
        Line line = getLineAtPos(pos);
        if (line == null) {
            return;
        }
        int scrollAmount = getScrollAmount();
        scrollAmount = Math.min(scrollAmount, line.lineIndex * lineHeight);
        scrollAmount = Math.max(scrollAmount + getInnerHeight(), (line.lineIndex + 1) * lineHeight) - getInnerHeight();
        scrollAmount = Math.min(scrollAmount, getMaxScroll());
        if (scrollAmount == this.scrollAmount) {
            return;
        }
        setScrollAmount(scrollAmount);
    }

    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        if (this.isSingleLine) {
            return false;
        }
        this.setScrollAmount(this.getScrollAmount() - (int) (p_231043_5_ * height / 4));
        return true;
    }

    public void setFocus(boolean p_146195_1_) {
        super.setFocused(p_146195_1_);
        if (p_146195_1_) {
            if (!this.isEditing) {
                this.isEditing = true;
                if (this.eventListener != null) {
                    this.eventListener.accept(this, EditEvent.BEGIN);
                }
            }
        } else {
            if (this.isEditing) {
                this.isEditing = false;
                if (this.eventListener != null) {
                    this.eventListener.accept(this, EditEvent.END);
                }
            }
        }
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int p_146203_1_) {
        this.maxLength = p_146203_1_;
        if (this.value.length() > p_146203_1_) {
            String oldValue = this.value;
            this.value = this.value.substring(0, p_146203_1_);
            this.onValueChange(oldValue, this.value);
        }

    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public void setCursorPosition(int p_212422_1_) {
        this.cursorPos = MathHelper.clamp(p_212422_1_, 0, this.value.length());
        this.ensureVisible(cursorPos);
    }

    public boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean p_146185_1_) {
        this.bordered = p_146185_1_;
    }

    public void setTextColor(int p_146193_1_) {
        this.textColor = p_146193_1_;
    }

    public void setTextColorUneditable(int p_146204_1_) {
        this.textColorUneditable = p_146204_1_;
    }

    public boolean changeFocus(boolean p_231049_1_) {
        return this.visible && this.isEditable && super.changeFocus(p_231049_1_);
    }

    public boolean isMouseOver(double p_231047_1_, double p_231047_3_) {
        return this.visible && p_231047_1_ >= (double) this.x && p_231047_1_ < (double) (this.x + this.width) && p_231047_3_ >= (double) this.y && p_231047_3_ < (double) (this.y + this.height);
    }

    public void onFocusedChanged(boolean p_230995_1_) {
        if (p_230995_1_) {
            this.frame = 0;
        }
        setFocus(p_230995_1_);
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean p_146184_1_) {
        this.isEditable = p_146184_1_;
    }

    public int getInnerWidth() {
        return this.width - contentInset.left - contentInset.right;
    }

    public int getInnerHeight() {
        return this.height - contentInset.top - contentInset.bottom;
    }

    public void setHighlightPos(int p_146199_1_) {
        int i = this.value.length();
        this.highlightPos = MathHelper.clamp(p_146199_1_, 0, i);
        if (this.isSingleLine && this.font != null) {
            if (this.displayPos > i) {
                this.displayPos = i;
            }

            int j = this.getInnerWidth();
            String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), j);
            int k = s.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.font.plainSubstrByWidth(this.value, j, true).length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = MathHelper.clamp(this.displayPos, 0, i);
        }
    }

    public void setCanLoseFocus(boolean p_146205_1_) {
        this.canLoseFocus = p_146205_1_;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_146189_1_) {
        this.visible = p_146189_1_;
    }

    public void setSuggestion(@Nullable String p_195612_1_) {
        this.suggestion = p_195612_1_;
    }

    public int getScreenX(int p_195611_1_) {
        return p_195611_1_ > this.value.length() ? this.x : this.x + this.font.width(this.value.substring(0, p_195611_1_));
    }

    public void setX(int p_212952_1_) {
        this.x = p_212952_1_;
    }


    public void renderLine(MatrixStack matrixStack, Line line, int x, int y, float p_230431_4_, int textColor, IRenderTypeBuffer buffers) {
        int dx = x;
        int dy = y + line.offsetY;
        Matrix4f mat = matrixStack.last().pose();

        // first part
        int pos1 = MathHelper.clamp(Math.min(cursorPos, highlightPos) - line.textStart, 0, line.textLength);
        if (pos1 != 0) {
            IReorderingProcessor value = formatter.apply(line.text.substring(0, pos1), line.textStart + pos1);
            dx = font.drawInBatch(value, dx, dy, textColor, true, mat, buffers, false, 0, 15728880) - 1;
        }
        updatePos(line.textStart + pos1, dx, dy);

        // second part
        int pos2 = MathHelper.clamp(Math.max(cursorPos, highlightPos) - line.textStart, 0, line.textLength);
        if (pos2 != pos1) {
            IReorderingProcessor value = formatter.apply(line.text.substring(pos1, pos2), line.textStart + pos2);
            dx = font.drawInBatch(value, dx, dy, textColor, true, mat, buffers, false, 0, 15728880) - 1;
            updatePos(line.textStart + pos2, dx, dy);
        }

        // final part
        int pos3 = line.textLength;
        if (pos2 != pos3) {
            IReorderingProcessor value = formatter.apply(line.text.substring(pos2, pos3), line.textStart + pos3);
            dx = font.drawInBatch(value, dx, dy, textColor, true, mat, buffers, false, 0, 15728880) - 1;
            updatePos(line.textStart + pos3, dx, dy);
        }
    }

    public void renderButton2(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        String value = getValue();
        this.updateMultlineTextIfNeeded();
        if (this.isBordered()) {
            int i = this.isFocused() ? -1 : -6250336;
            fill(matrixStack, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
            fill(matrixStack, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }
        RenderUtils.enableScissor(x + 1, y + 1, width - 2, height - 2);

        int dx = x + contentInset.left;
        int dy = y + contentInset.top - scrollAmount;
        int textColor = this.isEditable ? this.textColor : this.textColorUneditable;

        int j = this.cursorPos - this.displayPos;
        boolean flag = j >= 0 && j <= value.length();

        this.cursorPosX = dx;
        this.cursorPosY = dy + wrappedTextLines.size() * lineHeight;
        this.highlightPosX = dx;
        this.highlightPosY = dy + wrappedTextLines.size() * lineHeight;

        IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        for (Line line : wrappedTextLines) {
            renderLine(matrixStack, line, dx, dy, p_230431_4_, textColor, buffers);
        }
        buffers.endBatch();

        boolean flag1 = this.isFocused() && frame / 6 % 2 == 0 && flag;
        boolean flag2 = this.cursorPos < value.length() || value.length() >= this.maxLength;
        if (flag1) {
            if (flag2) {
                AbstractGui.fill(matrixStack, cursorPosX, cursorPosY - 1, cursorPosX + 1, cursorPosY + lineHeight + 1, -3092272);
            } else {
                font.drawShadow(matrixStack, "_", cursorPosX, cursorPosY + 1, textColor);
            }
        }

        if (cursorPos != highlightPos) {
            this.renderHighlight2(matrixStack, dx, dy);
        }

        RenderUtils.disableScissor();
    }

    public void renderHighlight2(MatrixStack matrixStack, int x, int y) {
        int maxWidth = getInnerWidth();
        int z0 = cursorPosX + ((cursorPosY - y) / lineHeight) * maxWidth;
        int z1 = highlightPosX + ((highlightPosY - y) / lineHeight) * maxWidth;
        int z2 = Math.min(z0, z1);
        int z3 = Math.max(z0, z1);

        Matrix4f mat = matrixStack.last().pose();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);

        int offsetX = 0;
        for (Line line : wrappedTextLines) {
            int x0 = MathHelper.clamp(z2 - offsetX, x, x + maxWidth);
            int x1 = MathHelper.clamp(z3 - offsetX, x, x + maxWidth);
            offsetX += maxWidth;
            if (x0 == x1) {
                continue;
            }
            bufferbuilder.vertex(mat, x0, y + line.offsetY + lineHeight, 0).endVertex();
            bufferbuilder.vertex(mat, x1, y + line.offsetY + lineHeight, 0).endVertex();
            bufferbuilder.vertex(mat, x1, y + line.offsetY, 0).endVertex();
            bufferbuilder.vertex(mat, x0, y + line.offsetY, 0).endVertex();
        }

        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    public void renderButton1(MatrixStack p_230431_1_, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (this.isBordered()) {
            int i = this.isFocused() ? -1 : -6250336;
            fill(p_230431_1_, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, i);
            fill(p_230431_1_, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
        }

        int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
        int j = this.cursorPos - this.displayPos;
        int k = this.highlightPos - this.displayPos;
        String s = this.font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
        int l = x + contentInset.left;
        int i1 = y + getInnerHeight() / 2;
        int j1 = l;
        if (k > s.length()) {
            k = s.length();
        }

        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = this.font.drawShadow(p_230431_1_, this.formatter.apply(s1, this.displayPos), (float) l, (float) i1, i2);
        }

        boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? l + this.width : l;
        } else if (flag2) {
            k1 = j1 - 1;
            --j1;
        }

        if (!s.isEmpty() && flag && j < s.length()) {
            this.font.drawShadow(p_230431_1_, this.formatter.apply(s.substring(j), this.cursorPos), (float) j1, (float) i1, i2);
        }

        if (!flag2 && this.suggestion != null) {
            this.font.drawShadow(p_230431_1_, this.suggestion, (float) (k1 - 1), (float) i1, -8355712);
        }

        if (flag1) {
            if (flag2) {
                AbstractGui.fill(p_230431_1_, k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
            } else {
                this.font.drawShadow(p_230431_1_, "_", (float) k1, (float) i1, i2);
            }
        }

        if (k != j) {
            int l1 = l + this.font.width(s.substring(0, k));
            this.renderHighlight1(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
        }
    }

    public void renderHighlight1(int x0, int y0, int x1, int y1) {
        if (x0 < x1) {
            int i = x0;
            x0 = x1;
            x1 = i;
        }

        if (y0 < y1) {
            int j = y0;
            y0 = y1;
            y1 = j;
        }

        if (x1 > this.x + this.width) {
            x1 = this.x + this.width;
        }

        if (x0 > this.x + this.width) {
            x0 = this.x + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.vertex(x0, y1, 0.0D).endVertex();
        bufferbuilder.vertex(x1, y1, 0.0D).endVertex();
        bufferbuilder.vertex(x1, y0, 0.0D).endVertex();
        bufferbuilder.vertex(x0, y0, 0.0D).endVertex();
        tessellator.end();
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }


    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        if (!isVisible()) {
            return;
        }
        if (isSingleLine) {
            this.renderButton1(matrixStack, mouseX, mouseY, p_230431_4_);
            if (getValue().isEmpty()) {
                this.font.draw(matrixStack, getPlaceholder(), x + contentInset.left, y + (height - lineHeight) / 2f, 0x404040);
            }
        } else {
            this.renderButton2(matrixStack, mouseX, mouseY, p_230431_4_);
            if (getValue().isEmpty()) {
                this.font.draw(matrixStack, getPlaceholder(), x + contentInset.left, y + contentInset.top, 0x404040);
            }
        }
    }

    public ITextComponent getPlaceholder() {
        return super.getMessage();
    }

    public void setPlaceholder(ITextComponent placeholder) {
        super.setMessage(placeholder);
    }

    public void setReturnHandler(Consumer<String> returnHandler) {
        this.returnHandler = returnHandler;
    }

    public boolean isSingleLine() {
        return isSingleLine;
    }

    public void setSingleLine(boolean isSingleLine) {
        this.isSingleLine = isSingleLine;
    }

    protected void updateCursorPosWithMouse(double mouseX, double mouseY) {
        int newCursor = 0;
        if (this.isSingleLine) {
            int i = MathHelper.floor(mouseX) - x - contentInset.left;
            String s = font.plainSubstrByWidth(value.substring(displayPos), getInnerWidth());
            newCursor = font.plainSubstrByWidth(s, i).length() + displayPos;
        } else {
            int j = (MathHelper.floor(mouseY) - y - contentInset.top + scrollAmount) / lineHeight;
            int i = MathHelper.floor(mouseX) - x - contentInset.left;
            if (wrappedTextLines != null && j >= 0 && j < wrappedTextLines.size()) {
                Line line = wrappedTextLines.get(j);
                newCursor = font.plainSubstrByWidth(line.text, i).length() + line.textStart;
            } else if (j > 0) {
                newCursor = value.length();
            }
        }
        this.moveCursorTo(newCursor);
    }

    protected void updatePos(int pos, int x, int y) {
        if (cursorPos == pos) {
            cursorPosX = x;
            cursorPosY = y;
        }
        if (highlightPos == pos) {
            highlightPosX = x;
            highlightPosY = y;
        }
    }

    protected void updateMultlineTextIfNeeded() {
        if (!Objects.equals(lastValue, value)) {
            lastValue = value;
            updateMultlineText();
        }
    }

    protected void updateMultlineText() {
        this.wrappedTextLines = new ArrayList<>();
        String value = getValue();
        this.font.getSplitter().splitLines(value, getInnerWidth(), Style.EMPTY, false, (style, startIndex, endIndex) -> {
            Line line = new Line(wrappedTextLines.size(), startIndex, 0, value.substring(startIndex, endIndex));
            this.wrappedTextLines.add(line);
        });
        if (value.isEmpty() || value.endsWith("\n")) {
            this.wrappedTextLines.add(new Line(wrappedTextLines.size(), value.length(), 0, ""));
        }
        int dy = 0;
        for (Line line : wrappedTextLines) {
            line.offsetY = dy;
            dy += lineHeight;
        }
    }

    protected Line getLineAtIndex(int index) {
        if (!isSingleLine && wrappedTextLines != null && index >= 0 && index < wrappedTextLines.size()) {
            return wrappedTextLines.get(index);
        }
        return null;
    }

    protected Line getLineAtPos(int pos) {
        if (isSingleLine) {
            return null;
        }
        updateMultlineTextIfNeeded();
        if (wrappedTextLines == null || wrappedTextLines.isEmpty()) {
            return null;
        }
        Line lastLine = null;
        for (Line line : wrappedTextLines) {
            if (pos < line.textStart) {
                return lastLine;
            }
            lastLine = line;
            if (pos < line.textEnd) {
                return line;
            }
        }
        return wrappedTextLines.get(wrappedTextLines.size() - 1);
    }


    public String formattedString(String value) {
        if (this.isSingleLine) {
            return SharedConstants.filterText(value);
        }
        return value;
    }

    public enum EditEvent {
        BEGIN, CHANGE, END
    }

    public static class Line {
        int lineIndex;
        int offsetY;
        int textStart;
        int textEnd;
        int textLength;
        String text;

        public Line(int lineIndex, int textStart, int offsetY, String text) {
            this.lineIndex = lineIndex;
            this.offsetY = offsetY;
            this.textStart = textStart;
            this.text = text;
            this.textLength = text.length();
            this.textEnd = textStart + textLength;
        }

        public boolean contains(int pos) {
            return textStart <= pos && pos < textEnd;
        }
    }
}
