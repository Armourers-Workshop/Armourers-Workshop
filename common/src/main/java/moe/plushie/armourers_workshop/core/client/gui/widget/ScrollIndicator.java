package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.SoundManagerImpl;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.MathUtils;

import java.util.HashMap;

public class ScrollIndicator extends UIControl {

    private final UIButton topButton = new UIButton(CGRect.ZERO);
    private final UIButton middleButton = new DragButton(CGRect.ZERO);
    private final UIButton bottomButton = new UIButton(CGRect.ZERO);

    float value = 0;
    float stepValue = 0.1f;

    public ScrollIndicator(CGRect frame) {
        super(frame);
        this.setup();
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        SoundManagerImpl.click();
        middleButton.setSelected(true);
        updateValue(valueAtPoint(convertPointFromView(event.locationInWindow(), null)));
    }

    @Override
    public void mouseUp(UIEvent event) {
        super.mouseUp(event);
        middleButton.setSelected(false);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        super.mouseDragged(event);
        updateValue(valueAtPoint(convertPointFromView(event.locationInWindow(), null)));
    }

    private void setup() {
        CGRect rect = bounds();
        int size = 10;

        topButton.setFrame(new CGRect(0, 0, size, size));
        topButton.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        topButton.setBackgroundImage(makeImage(20, 0), State.ALL);
        topButton.addTarget(this, Event.MOUSE_LEFT_DOWN, ScrollIndicator::upAction);
        topButton.setCanBecomeFocused(false);
        addSubview(topButton);

        bottomButton.setFrame(new CGRect(0, rect.getMaxY() - size, size, size));
        bottomButton.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleTopMargin);
        bottomButton.setBackgroundImage(makeImage(30, 0), State.ALL);
        bottomButton.addTarget(this, Event.MOUSE_LEFT_DOWN, ScrollIndicator::downAction);
        bottomButton.setCanBecomeFocused(false);
        addSubview(bottomButton);

        UIImageView bg1 = new UIImageView(new CGRect(0, topButton.frame().getMaxY(), size, size));
        UIImageView bg2 = new UIImageView(new CGRect(0, bottomButton.frame().getMinY() - size, size, size));
        UIImageView bg3 = new UIImageView(new CGRect(0, bg1.frame().getMaxY(), size, bg2.frame().getMinY() - bg1.frame().getMaxY()));
        bg1.setImage(makeImage(20, 20));
        bg2.setImage(makeImage(30, 20));
        bg3.setImage(UIImage.of(ModTextures.SCROLLBAR).uv(246, 0).resizable(10, 246).build());
        bg1.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        bg2.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleTopMargin);
        bg3.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(bg1);
        addSubview(bg2);
        addSubview(bg3);

        middleButton.setFrame(new CGRect(0, size, size, size));
        middleButton.setBackgroundImage(makeImage(40, 0), State.ALL);
        middleButton.setCanBecomeFocused(false);
        addSubview(middleButton);
    }

    private UIImage makeImage(int u, int v) {
        HashMap<Integer, CGPoint> offsets = new HashMap<>();
        offsets.put(State.NORMAL, new CGPoint(0, 0));
        offsets.put(State.HIGHLIGHTED, new CGPoint(0, 1));
        offsets.put(State.SELECTED, new CGPoint(0, 1));
        return UIImage.of(ModTextures.SCROLLBAR).uv(u, v).fixed(10, 10).unzip(offsets::get).build();
    }

    private void updateOffsetIfNeeded() {
        int minY = topButton.frame().getMaxY();
        int maxY = bottomButton.frame().getMinY();
        CGRect frame = middleButton.frame();
        int y = (int) (((maxY - minY) - frame.height) * value);
        middleButton.setFrame(new CGRect(frame.x, minY + y, frame.width, frame.height));
    }

    private void updateValue(float value) {
        setValue(value);
        sendEvent(Event.VALUE_CHANGED);
    }

    private float valueAtPoint(CGPoint point) {
        int minY = topButton.frame().getMaxY();
        int maxY = bottomButton.frame().getMinY();
        int y = point.y;
        if (y < minY) {
            return 0;
        }
        if (y > maxY) {
            return 1;
        }
        return (y - minY) / (float)(maxY - minY);
    }

    private void upAction(UIControl sender) {
        updateValue(value - stepValue);
    }

    private void downAction(UIControl sender) {
        updateValue(value + stepValue);
    }

    private void dragAction(UIControl sender) {
    }


//    @Override
//    public void drawButton(Minecraft minecraft, int x, int y, float partial) {
//        if (this.visible)
//        {
//            updateMouse();
//            FontRenderer fontRendererObj = minecraft.fontRenderer;
//            minecraft.getTextureManager().bindTexture(texture);
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            //this.field_82253_i = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
//
//            drawArrows(minecraft, x, y);
//
//            //this.drawTexturedModalRect(this.xPosition + 10, this.yPosition, 0, 10, 60, 10);
//            //this.drawTexturedModalRect(this.xPosition + this.width - 10, this.yPosition, 10, 0, 10, 10);
//
//            //this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
//            this.mouseDragged(minecraft, x, y);
//
//            //this.drawCenteredString(fontRendererObj, Float.toString(sliderValue), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, 14737632);
//        }
//    }

//    private void drawArrows(Minecraft minecraft, int x, int y) {
//        int sourceX = 0;
//        int sourceY = 0;
//
//        sourceX = yOffset * 20;
//
//
//        //arrows
//        this.drawHover(this.x, this.y, sourceX, sourceY, 10, 10, x, y);
//        this.drawHover(this.x, this.y + this.height - 10, sourceX + 10, sourceY, 10, 10, x, y);
//
//
//        //gutter sides
//        this.drawTexturedModalRect(this.x, this.y + 10, sourceX, 20, 10, 10);
//        this.drawTexturedModalRect(this.x, this.y + this.height - 20, sourceX + 10, 20, 10, 10);
//
//        //gutter fill
//        this.drawTexturedModalRect(this.x, this.y + 20, 246 * yOffset, 246 * xOffset, width, height - 40);
//
//        //grip
//        float gripPos = (height - 30) / 100F * getPercentageValue();
//        this.drawHover(this.x, (int) (this.y + gripPos + 10), 40, sourceY, 10, 10, x, y);
//    }
//
//    private void drawHover(int x, int y, int sourceX, int sourceY, int width, int height, int mouseX, int mouseY) {
//        int hover = 0;
//        if(isHovering(mouseX, mouseY, x, y, width, height)) { hover = 10; }
//        this.drawTexturedModalRect(x, y, sourceX, sourceY + hover, width, height);
//    }

//    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
//        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
//    }

//    private void updateMouse() {
//        if (Mouse.isCreated()) {
//            int dWheel = Mouse.getDWheel();
//            if (dWheel < 0) {
//                setValue(sliderValue + amount);
//            } else if (dWheel > 0) {
//                setValue(sliderValue - amount);
//            }
//        }
//    }

//    @Override
//    public boolean mousePressed(Minecraft par1Minecraft, int x, int y) {
//        if (super.mousePressed(par1Minecraft, x, y)) {
//
//            if (!isHovering(x, y, this.x, this.y, 10, 10)) {
//                if (!isHovering(x, y, this.x + xOffset * (this.width - 10), this.y + yOffset * (this.height - 10), 10, 10)) {
//                    this.dragging = true;
//                }
//                else {
//                    setValue(sliderValue + amount);
//                }
//            } else {
//                setValue(sliderValue - amount);
//            }
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    @Override
//    protected void mouseDragged(Minecraft par1Minecraft, int x, int y) {
//        if (this.dragging) {
//            if (horizontal) {
//                float per = (x - this.x - 12) / (width - 30F) * sliderMaxValue;
//                setValue((int) per);
//            } else {
//                float per = (y - this.y - 12) / (height - 30F) * sliderMaxValue;
//                setValue((int) per);
//            }
//        }
//    }

//    public int getValue() {
//        return sliderValue;
//    }
//
//    public int getPercentageValue() {
//        if (sliderValue == 0) {
//            return 0;
//        }
//        return (int) (((float)sliderValue / (float)sliderMaxValue) * 100);
//    }

    public float value() {
        return value;
    }

    public void setValue(float value) {
        this.value = MathUtils.clamp(value, 0, 1);
        this.updateOffsetIfNeeded();
    }

    public float stepValue() {
        return stepValue;
    }

    public void setStepValue(float stepValue) {
        this.stepValue = stepValue;
    }

    @Override
    protected boolean shouldPassHighlighted() {
        return false;
    }

    private static class DragButton extends UIButton {

        public DragButton(CGRect frame) {
            super(frame);
        }

        @Override
        public void mouseDown(UIEvent event) {
            super.mouseDown(event);
            setSelected(true);
        }

        @Override
        public void mouseUp(UIEvent event) {
            super.mouseUp(event);
            setSelected(false);
        }

        @Override
        public void mouseDragged(UIEvent event) {
            super.mouseDragged(event);
            nextResponder().mouseDragged(event);
        }
    }
}
