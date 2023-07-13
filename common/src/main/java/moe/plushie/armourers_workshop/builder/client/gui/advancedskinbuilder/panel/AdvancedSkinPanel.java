package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIBarItem;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.data.properties.BooleanProperty;
import moe.plushie.armourers_workshop.builder.data.properties.FloatProperty;
import moe.plushie.armourers_workshop.builder.data.properties.VectorProperty;
import moe.plushie.armourers_workshop.init.ModTextures;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AdvancedSkinPanel extends UIView {

    protected ArrayList<Group> groups = new ArrayList<>();
    protected UIBarItem barItem = new UIBarItem();

    public AdvancedSkinPanel() {
        super(CGRect.ZERO);
    }

    @Override
    public void sizeToFit() {
        super.sizeToFit();
        CGRect bounds = bounds();
        UIEdgeInsets edge = new UIEdgeInsets(8, 8, 8, 8);
        float width = bounds.width - edge.left - edge.right;
        float top = edge.top;
        float left = bounds.width * 0.4f;
        for (Group group : groups) {
            float h = group.layout(4, left, left + 4, width - 4 - 4, 2);
            group.setFrame(new CGRect(edge.left, top, width, h));
            group.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
            top += h + 4f;
        }
        setBounds(new CGRect(0, 0, bounds.width, top + edge.bottom));
    }

    @Override
    public CGSize sizeThatFits(CGSize size) {
        setBounds(new CGRect(0, 0, size.width, size.height));
        sizeToFit();
        return bounds().size();
    }

    public UIBarItem barItem() {
        return barItem;
    }

    protected NSString translatable(String key) {
        return new NSString(key);
    }

    protected void addGroup(NSString name, Consumer<Group> builder) {
        Group group = new Group(name);
        builder.accept(group);
        addSubview(group);
        groups.add(group);
    }

    public static class Group extends UIView {

        protected final UILabel titleView = new UILabel(CGRect.ZERO);
        protected final ArrayList<Pair<UIView, UIView>> lines = new ArrayList<>();

        public Group(NSString name) {
            super(CGRect.ZERO);
            this.titleView.setText(name);
            this.titleView.setTextColor(UIColor.WHITE);
            this.titleView.setFrame(new CGRect(8, 4, bounds().getWidth(), 10));
            this.titleView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
            this.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 24).clip(4, 4, 4, 4).build());
            this.addSubview(titleView);
        }

        public float layout(float x0, float x1, float x2, float x3, float spacing) {
            float top = titleView.frame().getMaxY() + 4f + spacing;
            for (Pair<UIView, UIView> it : lines) {
                UIView leftView = it.getLeft();
                UIView rightView = it.getRight();
                CGRect leftFrame = leftView.frame().copy();
                CGRect rightFrame = rightView.frame().copy();
                float height = Math.max(leftFrame.getHeight(), rightFrame.getHeight());
                leftFrame.x = x0;
                leftFrame.y = top + (height - leftFrame.getHeight()) / 2;
                leftFrame.width = x1 - x0;
                rightFrame.x = x2;
                rightFrame.y = top + (height - rightFrame.getHeight()) / 2;
                rightFrame.width = x3 - x2;
                leftView.setFrame(leftFrame);
                rightView.setFrame(rightFrame);
                leftView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleBottomMargin);
                rightView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
                top += height + spacing;
            }
            top += 4f;
            return top - spacing;
        }

        // [x] name
        public void bool(NSString name, BooleanProperty property) {
            UIView i = new UIView(new CGRect(0, 0, 80, 16));
            i.setBackgroundColor(UIColor.GREEN);
            addLine(name, i);
        }

        // name [ --- ]
        public void slider(NSString name, FloatProperty property, Function<Float, NSString> formatter) {
            UIView i = new UIView(new CGRect(0, 0, 80, 16));
            i.setBackgroundColor(UIColor.RED);
            addLine(name, i);
        }

        // name x [ --- ]
        //      y [ --- ]
        //      z [ --- ]
        public void vector(NSString name, VectorProperty property, Function<Float, NSString> formatter) {
            NSMutableString name1 = new NSMutableString(name);
            name1.append(" ");
            name1.append("X");
            slider(name1, property.x(), formatter);
            slider(new NSString("Y"), property.y(), formatter);
            slider(new NSString("Z"), property.z(), formatter);
        }

        private void addLine(NSString name, UIView view) {
            UILabel label = new UILabel(new CGRect(0, 0, 30, 10));
            label.setText(name);
            label.setTextColor(UIColor.WHITE);
            label.setTextHorizontalAlignment(NSTextAlignment.Horizontal.RIGHT);
            addView(label, view);
        }

        private void addView(UIView leftView, UIView rightView) {
            lines.add(Pair.of(leftView, rightView));
            addSubview(leftView);
            addSubview(rightView);
        }
    }

}

