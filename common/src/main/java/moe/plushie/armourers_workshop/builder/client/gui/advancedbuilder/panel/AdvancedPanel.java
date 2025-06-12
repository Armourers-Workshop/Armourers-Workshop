package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIBarItem;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.client.gui.widget.NewSlider;
import moe.plushie.armourers_workshop.builder.data.properties.DataProperty;
import moe.plushie.armourers_workshop.builder.data.properties.Vector3fProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocument;
import moe.plushie.armourers_workshop.init.ModTextures;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AdvancedPanel extends UIView {

    protected ArrayList<Section> sections = new ArrayList<>();
    protected UIBarItem barItem = new UIBarItem();

    protected final SkinDocument document;
    protected final DocumentEditor editor;

    public AdvancedPanel(DocumentEditor editor) {
        super(CGRect.ZERO);
        this.document = editor.document();
        this.editor = editor;
    }

    @Override
    public void sizeToFit() {
        var bounds = bounds();
        var edge = new UIEdgeInsets(8, 8, 8, 8);
        var width = bounds.width - edge.left - edge.right;
        var top = edge.top;
        var left = bounds.width * 0.4f;
        for (Section section : sections) {
            var h = section.layout(4, left, left + 4, width - 4 - 4, 2);
            section.setFrame(new CGRect(edge.left, top + section.headerSize(), width, h));
            section.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
            top += section.headerSize() + h + section.footerSize();
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
        var group = new Group(editor, name);
        builder.accept(group);
        addSection(group);
    }

    protected void addContent(UIView contentView) {
        addSection(new Content(contentView));
    }

    protected void addSection(Section section) {
        addSubview(section);
        sections.add(section);
    }

    public static class Group extends Section {

        protected final UILabel titleView = new UILabel(CGRect.ZERO);
        protected final ArrayList<Pair<UIView, UIView>> lines = new ArrayList<>();

        public Group(DocumentEditor editor, NSString name) {
            super(CGRect.ZERO);
            this.titleView.setText(name);
            this.titleView.setTextColor(UIColor.WHITE);
            this.titleView.setFrame(new CGRect(8, 4, bounds().width(), 10));
            this.titleView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
            this.setContents(UIImage.of(ModTextures.ADVANCED_SKIN_BUILDER).uv(24, 24).fixed(24, 24).clip(4, 4, 4, 4).build());
            this.addSubview(titleView);
        }

        @Override
        public float layout(float x0, float x1, float x2, float x3, float spacing) {
            float top = titleView.frame().maxY() + 4f + spacing;
            for (var it : lines) {
                var rightView = it.getValue();
                var rightFrame = rightView.frame().copy();
                var leftView = it.getLeft();
                var height = rightFrame.height;
                if (leftView != null) {
                    var leftFrame = leftView.frame().copy();
                    height = Math.max(height, leftFrame.height);
                    leftFrame.x = x0;
                    leftFrame.y = top + (height - leftFrame.height) / 2;
                    leftFrame.width = x1 - x0;
                    leftView.setFrame(leftFrame);
                    leftView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleBottomMargin);
                }
                rightFrame.x = x2;
                rightFrame.y = top + (height - rightFrame.height) / 2;
                rightFrame.width = x3 - x2;
                rightView.setFrame(rightFrame);
                rightView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
                top += height + spacing;
            }
            top += 4f;
            return top - spacing;
        }

        // [x] name
        public void bool(@Nullable NSString name, DataProperty<Boolean> property) {
            bool(name, null, property);
        }

        // [x] name
        public void bool(@Nullable NSString name, @Nullable NSString desc, DataProperty<Boolean> property) {
            var box = new UICheckBox(new CGRect(0, 0, 80, 16));
            box.setTitle(desc);
            box.addTarget(property, UIControl.Event.VALUE_CHANGED, (pro, ctrl) -> {
                var checkBox = (UICheckBox) ctrl;
                pro.set(checkBox.isSelected());
            });
            property.addObserver(box::setSelected);
            addRow(name, box);
        }

        // name [ --- ]
        public void slider(@Nullable NSString name, DataProperty<Float> property, Unit unit) {
            var view = new NewSlider(new CGRect(0, 0, 80, 16));
            view.setFormatter(unit);
            view.setStepValue(unit.stepValue);
            view.setValue(unit.defaultValue);
            view.setMultipler(unit.multipler);
            view.addTarget(property, UIControl.Event.EDITING_DID_BEGIN, (pro, ctrl) -> pro.beginEditing());
            view.addTarget(property, UIControl.Event.EDITING_DID_END, (pro, ctrl) -> pro.endEditing());
            view.addTarget(property, UIControl.Event.VALUE_CHANGED, (pro, ctrl) -> {
                var slider = (NewSlider) ctrl;
                pro.set((float) slider.value());
            });
            property.addObserver(view::setValue);
            addRow(name, view);
        }

        // name x [ --- ]
        //      y [ --- ]
        //      z [ --- ]
        public void vector(NSString name, Vector3fProperty property, Unit unit) {
            var name1 = new NSMutableString(name);
            name1.append(" ");
            name1.append("X");
            slider(name1, property.x(), unit);
            slider(new NSString("Y"), property.y(), unit);
            slider(new NSString("Z"), property.z(), unit);
        }

        private void addRow(@Nullable NSString name, UIView view) {
            if (name == null) {
                addView(null, view);
                return;
            }
            var title = new UILabel(new CGRect(0, 0, 30, 10));
            title.setText(name);
            title.setTextColor(UIColor.WHITE);
            title.setTextHorizontalAlignment(NSTextAlignment.Horizontal.RIGHT);
            addView(title, view);
        }

        private void addView(@Nullable UIView leftView, UIView rightView) {
            lines.add(Pair.of(leftView, rightView));
            if (leftView != null) {
                addSubview(leftView);
            }
            addSubview(rightView);
        }

        public enum Unit implements NewSlider.Formatter {

            POINT("#.####", "#.#####", 0, 0.01, 1),
            DEGREES("#.#°", "#.#####", 0, 0.1, 10),
            SCALE("0.000", "0.0####", 1, 0.01, 1);

            public final double stepValue;
            public final double defaultValue;
            public final float multipler;

            public final DecimalFormat inputFormat;
            public final DecimalFormat displayFormat;

            Unit(String displayFormat, String inputFormat, double defaultValue, double stepValue, float multipler) {
                this.stepValue = stepValue;
                this.defaultValue = defaultValue;
                this.multipler = multipler;
                this.displayFormat = new DecimalFormat(displayFormat);
                this.inputFormat = new DecimalFormat(inputFormat);
            }

            @Override
            public String display(double value) {
                return displayFormat.format(value);
            }

            @Override
            public String input(double value) {
                return inputFormat.format(value);
            }

            @Override
            public Optional<Double> parse(String value) {
                try {
                    return Optional.of(inputFormat.parse(value).doubleValue());
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }
    }

    public static class Content extends Section {

        protected final UIView contentView;

        public Content(UIView contentView) {
            super(contentView.bounds());
            this.contentView = contentView;
            this.contentView.setFrame(bounds());
            this.contentView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
            this.addSubview(contentView);
        }

        @Override
        public float layout(float x0, float x1, float x2, float x3, float spacing) {
            return contentView.bounds().height();
        }

        @Override
        public float footerSize() {
            return 8;
        }
    }

    public static abstract class Section extends UIView {

        public Section(CGRect frame) {
            super(frame);
        }

        public abstract float layout(float x0, float x1, float x2, float x3, float spacing);

        public float headerSize() {
            return 0;
        }

        public float footerSize() {
            return 4;
        }
    }
}

