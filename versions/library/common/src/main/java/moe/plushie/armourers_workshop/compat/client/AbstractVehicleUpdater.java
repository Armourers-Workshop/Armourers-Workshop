package moe.plushie.armourers_workshop.compat.client;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.compat.core.AbstractDeltaTracker;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.init.event.client.RenderFrameEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;

import java.util.ArrayList;

@Available("[1.16, )")
@OnlyIn(Dist.CLIENT)
public class AbstractVehicleUpdater extends AbstractVehicleUpdaterImpl {

    private static final AbstractVehicleUpdater INSTANCE = new AbstractVehicleUpdater();

    private final Int2ObjectMap<Entity> entities = new Int2ObjectOpenHashMap<>();
    private Int2ObjectMap<Entity> active;

    private AbstractVehicleUpdater() {
        EventBus.register(RenderFrameEvent.Pre.class, event -> update(event.deltaTracker()));
    }

    public static AbstractVehicleUpdater getInstance() {
        return INSTANCE;
    }

    public void submit(EntityRenderState renderState) {
        active = entities;
        findEntity(renderState, entity -> {
            entities.put(entity.getId(), entity);
        });
    }

    private void update(AbstractDeltaTracker delta) {
        if (active == null) {
            return;
        }
        var oldValue = new ArrayList<>(entities.values());
        active = null; // clear on next frame
        entities.clear();
        oldValue.forEach(it -> apply(it, delta.getPartialTick(it)));
    }

    private void apply(Entity entity, float partialTicks) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var passengers = entity.getPassengers();
        for (int index = 0; index < passengers.size(); index++) {
            var attachmentPose = renderData.getAttachmentPose(SkinAttachmentTypes.RIDING, index);
            if (attachmentPose == null) {
                continue; // pass, use vanilla behavior.
            }
            var passenger = passengers.get(index);
            apply(entity, partialTicks, index, passenger, attachmentPose);
        }
    }

    private void apply(Entity entity, float partialTicks, int index, Entity passenger, SkinAttachmentPose pose) {
        // compute and save the custom riding position into entity.
        var scale = getRiddingScale(entity);
        var mat = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        mat.rotate(OpenVector3f.YP.rotationDegrees(180 - entity.getViewYRot(partialTicks)));
        mat.scale(-1, -1, 1);
        mat.scale(scale, scale, scale);
        mat.translate(0, -1.501f, 0);
        mat.scale(1 / 16f, 1 / 16f, 1 / 16f);
        mat.multiply(pose.pose());
        var offset = OpenVector3f.ZERO.transforming(mat);
        entity.setCustomRidding(index, offset);

        // update the entity riding position.
        double tx = passenger.getX();
        double ty = passenger.getY();
        double tz = passenger.getZ();

        entity.positionRider(passenger);

        double dx = passenger.getX() - tx;
        double dy = passenger.getY() - ty;
        double dz = passenger.getZ() - tz;

        // the `setOldPosAndRot` special version
        passenger.xo += dx;
        passenger.yo += dy;
        passenger.zo += dz;
        passenger.xOld += dx;
        passenger.yOld += dy;
        passenger.zOld += dz;
    }

    private float getRiddingScale(Entity entity) {
        if (entity instanceof Horse) {
            return 1.1f;
        }
        return 1.0f;
    }
}
