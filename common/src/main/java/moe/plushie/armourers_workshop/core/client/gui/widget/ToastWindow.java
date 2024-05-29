package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class ToastWindow extends UIWindow {

    protected final UILabel titleLabel = new UILabel(CGRect.ZERO);
    protected final UILabel messageLabel = new UILabel(CGRect.ZERO);

    private double duration = 5000f;

    private Object icon;
    private final CGRect iconRect = new CGRect(8, 8, 18, 18);

    public ToastWindow(CGRect frame) {
        super(frame);
        this.setContents(UIImage.of(getDefaultTexture()).resizable(160, 32).build());
        this.titleLabel.setFrame(new CGRect(30, 7, frame.width - 30 - 5, 9));
        this.titleLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        this.titleLabel.setTextColor(new UIColor(0xffff00));
        this.addSubview(titleLabel);
        this.messageLabel.setFrame(new CGRect(30, 18, frame.width - 30 - 5, 9));
        this.messageLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        this.messageLabel.setTextColor(UIColor.WHITE);
        this.addSubview(messageLabel);
        this.updateIconRect();
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        // render item as icon.
        ItemStack itemStack = ObjectUtils.safeCast(getIcon(), ItemStack.class);
        if (itemStack != null) {
            context.drawItem(itemStack, 8, 8);
            return;
        }
        // render texture as icon.
        CustomTexture texture = ObjectUtils.safeCast(getIcon(), CustomTexture.class);
        if (texture != null) {
            context.drawImage(texture.image, iconRect.offset(texture.origin));
            return;
        }
        // not supported yet.
    }

    public NSString title() {
        return titleLabel.text();
    }

    public void setTitle(NSString title) {
        titleLabel.setText(title);
    }

    public NSString message() {
        return messageLabel.text();
    }

    public void setMessage(NSString title) {
        messageLabel.setText(title);
    }

    public UIColor titleColor() {
        return titleLabel.textColor();
    }

    public void setTitleColor(UIColor color) {
        titleLabel.setTextColor(color);
    }

    public UIColor messageColor() {
        return messageLabel.textColor();
    }

    public void setMessageColor(UIColor color) {
        messageLabel.setTextColor(color);
    }

    public void setIcon(Object icon) {
        this.icon = icon;
        CompoundTag tag = ObjectUtils.safeCast(icon, CompoundTag.class);
        if (tag == null) {
            this.updateIconRect();
            return;
        }
        if (tag.contains("Skin")) {
            String skinId = tag.getString("Skin");
            SkinDescriptor descriptor = new SkinDescriptor(skinId);
            this.icon = descriptor.asItemStack();
        }
        if (tag.contains("Image")) {
            this.icon = new CustomTexture(tag);
        }
        Level level = Minecraft.getInstance().level;
        if (tag.contains("id") && level != null) {
            this.icon = ItemStack.parseOptional(level.registryAccess(), tag);
        }
        this.updateIconRect();
    }

    public Object getIcon() {
        return icon;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    private ResourceLocation getDefaultTexture() {
        return ModTextures.TOASTS;
    }

    private void updateIconRect() {
        float x1 = 0;
        float x2 = titleLabel.frame().getMaxX();
        float x3 = 0;
        float x4 = messageLabel.frame().getMaxX();

        if (icon != null) {
            x1 = 30;
            x3 = 30;
        } else {
            x1 = 8;
            x3 = 8;
        }
        CGRect nf1 = new CGRect(titleLabel.frame());
        nf1.x = x1;
        nf1.width = x2 - x1;
        titleLabel.setFrame(nf1);
        CGRect nf2 = new CGRect(messageLabel.frame());
        nf2.x = x3;
        nf2.width = x4 - x3;
        messageLabel.setFrame(nf2);
    }

    public static class CustomTexture {

        public final CGPoint origin;
        public final UIImage image;

        public CustomTexture(CompoundTag tag) {
            int[] offset = {0, 0};
            UIImage.Builder builder = UIImage.of(new ResourceLocation(tag.getString("Image")));
            apply(tag, "UV", 2, it -> builder.uv(it[0], it[1]));
            apply(tag, "Fixed", 2, it -> builder.fixed(it[0], it[1]));
            apply(tag, "Resizable", 2, it -> builder.resizable(it[0], it[1]));
            apply(tag, "Resize", 4, it -> builder.resize(it[0], it[1], it[2], it[3]));
            apply(tag, "Limit", 2, it -> builder.limit(it[0], it[1]));
            apply(tag, "Clip", 4, it -> builder.clip(it[0], it[1], it[2], it[3]));
            apply(tag, "Origin", 2, it -> {
                offset[0] = it[0];
                offset[1] = it[1];
            });
            this.image = builder.build();
            this.origin = new CGPoint(offset[0], offset[1]);
        }

        private void apply(CompoundTag tag, String key, int limit, Consumer<int[]> consumer) {
            if (!tag.contains(key)) {
                return;
            }
            ListTag list = tag.getList(key, 3);
            if (list.size() < limit) {
                return;
            }
            int[] data = new int[list.size()];
            for (int i = 0; i < data.length; ++i) {
                data[i] = list.getInt(i);
            }
            consumer.accept(data);
        }
    }
}
