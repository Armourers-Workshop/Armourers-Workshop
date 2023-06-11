package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.StateValueImpl;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

@Environment(value= EnvType.CLIENT)
public class EntityPartView extends UIControl {

    private final StateValueImpl<UIColor> partColor = new StateValueImpl<>();
    private final UIImage backgroundImage = UIImage.of(ModTextures.WARDROBE_2).uv(22, 0).build();

    private Part selectedPart;
    private Part highlightedPart;

    public EntityPartView(CGRect frame) {
        super(frame);
        this.partColor.setValueForState(new UIColor(0xccffff00, true), State.NORMAL);
        this.partColor.setValueForState(new UIColor(0xccffffff, true), State.HIGHLIGHTED);
        this.partColor.setValueForState(new UIColor(0xcc00ff00, true), State.SELECTED);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        Part part = getPart(event.locationInView(this));
        if (part != null) {
            selectedPart = part;
            sendEvent(Event.VALUE_CHANGED);
        }
    }

    @Override
    public void mouseMoved(UIEvent event) {
        super.mouseMoved(event);
        highlightedPart = getPart(event.locationInView(this));
    }

    @Override
    public void mouseExited(UIEvent event) {
        super.mouseExited(event);
        highlightedPart = null;
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        context.drawImage(backgroundImage, bounds());
        for (Part part : Part.values()) {
            context.fillRect(partColor.valueForState(getPartState(part)), part.bounds);
        }
    }

    public Part selectedPart() {
        return selectedPart;
    }

    public void setSelectedPart(Part selectedPart) {
        this.selectedPart = selectedPart;
    }

    private Part getPart(CGPoint point) {
        for (Part part : Part.values()) {
            if (part.bounds.contains(point)) {
                return part;
            }
        }
        return null;
    }

    private int getPartState(Part part) {
        if (part == selectedPart) {
            return State.SELECTED;
        }
        if (part == highlightedPart) {
            return State.HIGHLIGHTED;
        }
        return State.NORMAL;
    }

    public enum Part {
        HEAD(MannequinEntity.DATA_HEAD_POSE, MannequinEntity.DEFAULT_HEAD_POSE, 8, 3, 8, 8),
        BODY(MannequinEntity.DATA_BODY_POSE, MannequinEntity.DEFAULT_BODY_POSE, 8, 12, 8, 12),
        RIGHT_ARM(MannequinEntity.DATA_RIGHT_ARM_POSE, MannequinEntity.DEFAULT_RIGHT_ARM_POSE, 3, 12, 4, 12),
        LEFT_ARM(MannequinEntity.DATA_LEFT_ARM_POSE, MannequinEntity.DEFAULT_LEFT_ARM_POSE, 17, 12, 4, 12),
        RIGHT_LEG(MannequinEntity.DATA_RIGHT_LEG_POSE, MannequinEntity.DEFAULT_RIGHT_LEG_POSE, 7, 25, 4, 12),
        LEFT_LEG(MannequinEntity.DATA_LEFT_LEG_POSE, MannequinEntity.DEFAULT_LEFT_LEG_POSE, 13, 25, 4, 12);

        public final CGRect bounds;
        public final Rotations defaultValue;
        public final EntityDataAccessor<Rotations> dataParameter;

        Part(EntityDataAccessor<Rotations> dataParameter, Rotations defaultValue, int x, int y, int width, int height) {
            this.bounds = new CGRect(x, y, width, height);
            this.dataParameter = dataParameter;
            this.defaultValue = defaultValue;
        }

        public void setValue(Entity entity, Rotations value) {
            entity.getEntityData().set(dataParameter, value);
        }

        public Rotations getValue(Entity entity) {
            if (entity == null) {
                return defaultValue;
            }
            return entity.getEntityData().get(dataParameter);
        }
    }
}
