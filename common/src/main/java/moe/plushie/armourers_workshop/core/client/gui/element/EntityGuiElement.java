package moe.plushie.armourers_workshop.core.client.gui.element;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGGraphicsElement;
import com.apple.library.coregraphics.CGPoint;
import moe.plushie.armourers_workshop.compat.client.gui.element.AbstractEntityGuiElement;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("unsed")
public abstract class EntityGuiElement implements CGGraphicsElement {

    public static EntityGuiElement newInstance(Entity entity, CGPoint origin, float scale, CGPoint focus) {
        return AbstractEntityGuiElement.newInstance(entity, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, origin, scale, focus);
    }

    @Override
    public void prepare(CGGraphicsContext context) {
        // the rendering entities will change the model view stack,
        // which may cause the pending draw to be executed in the wrong context,
        // so we need to flush first.
        context.flush();
    }

    protected void apply(Entity entity, float xRot, float yRot, Runnable handler) {
        var accessor = EntityAccessor.create(entity);

        var xRotO = accessor.xRot();
        var yRotO = accessor.yRot();
        var yHeadRot = accessor.yHeadRot();
        var yHeadRotO = accessor.yHeadRotO();
        var yBodyRot = accessor.yBodyRot();

        accessor.setXRot(-xRot * 20.0f);
        accessor.setYRot(180.0f + yRot * 40.0f);
        accessor.setYHeadRot(accessor.yRot());
        accessor.setYHeadRotO(accessor.yRot());
        accessor.setYBodyRot(180.0f + yRot * 20.0f);

        handler.run();

        accessor.setXRot(xRotO);
        accessor.setYRot(yRotO);
        accessor.setYHeadRot(yHeadRot);
        accessor.setYHeadRotO(yHeadRotO);
        accessor.setYBodyRot(yBodyRot);
    }

    protected static class EntityAccessor<T extends Entity> {

        protected final T entity;

        protected EntityAccessor(T entity) {
            this.entity = entity;
        }

        public static EntityAccessor<?> create(Entity entity) {
            if (entity instanceof LivingEntity livingEntity) {
                return new LivingEntityAccessor<>(livingEntity);
            }
            return new EntityAccessor<>(entity);
        }

        public float xRot() {
            return entity.getXRot();
        }

        public float yRot() {
            return entity.getYRot();
        }

        public float yHeadRot() {
            return 0;
        }

        public float yHeadRotO() {
            return 0;
        }

        public float yBodyRot() {
            return 0;
        }

        public void setXRot(float value) {
            entity.setXRot(value);
        }

        public void setYRot(float value) {
            entity.setYRot(value);
        }

        public void setYHeadRot(float value) {
            // nop
        }

        public void setYHeadRotO(float value) {
            // nop
        }

        public void setYBodyRot(float value) {
            // nop
        }
    }

    protected static class LivingEntityAccessor<T extends LivingEntity> extends EntityAccessor<T> {

        protected LivingEntityAccessor(T entity) {
            super(entity);
        }

        @Override
        public float yHeadRot() {
            return entity.yHeadRot;
        }

        @Override
        public float yHeadRotO() {
            return entity.yHeadRotO;
        }

        @Override
        public float yBodyRot() {
            return entity.yBodyRot;
        }

        @Override
        public void setYHeadRot(float value) {
            entity.yHeadRot = value;
        }

        @Override
        public void setYHeadRotO(float value) {
            entity.yHeadRotO = value;
        }

        @Override
        public void setYBodyRot(float value) {
            entity.yBodyRot = value;
        }
    }
}
