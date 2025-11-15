package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

public class EntityCollisionContainer {

    private final WeakReference<Entity> entity;
    private final SkinContainerEvaluator evaluator;

    private EntityCollisionShape result;

    public EntityCollisionContainer(SimpleContainer container, Entity entity) {
        this.entity = new WeakReference<>(entity);
        this.evaluator = createIfNeeded(container, entity);
    }

    public void deserialize(IDataSerializer serializer) {
        setResult(serializer.read(CodingKeys.COLLISION_SHAPE));
        if (evaluator != null) {
            evaluator.reset();
        }
    }

    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.COLLISION_SHAPE, result);
    }

    public void beginUpdates() {
        if (evaluator != null) {
            evaluator.beginUpdates();
        }
    }

    public void endUpdates() {
        if (evaluator != null) {
            evaluator.endUpdates();
        }
    }

    public void setResult(EntityCollisionShape boundingBox) {
        var oldValue = result;
        this.result = boundingBox;
        if (Objects.equals(oldValue, boundingBox)) {
            return;
        }
        // refresh the entity size if needed
        var entity1 = entity.get();
        if (entity1 != null) {
            entity1.setCustomCollision(result);
        }
    }

    public EntityCollisionShape result() {
        return this.result;
    }


    private void reload(List<Skin> skins) {
        // when the result is changed, notify all client.
        var newResult = resolve(skins);
        if (!Objects.equals(newResult, result)) {
            ModLog.debug("{} reload collision shape: {}", entity.get(), newResult);
            setResult(newResult);
            sendChanges();
        }
    }

    @Nullable
    private EntityCollisionShape resolve(List<Skin> skins) {
        for (var skin : skins) {
            if (skin.type() == SkinTypes.BLOCK) {
                continue; // can't compute entity bounding box of the block skin.
            }
            var boundingBox = skin.settings().collisionBox();
            if (boundingBox == null || boundingBox.isEmpty()) {
                continue; // can't found collision box.
            }
            return EntityCollisionShape.size(boundingBox.get(0));
        }
        return null;
    }

    private void sendChanges() {
        var entity1 = entity.get();
        if (entity1 == null) {
            return; // the entity is destroyed.
        }
        var wardrobe = SkinWardrobe.of(entity1);
        if (wardrobe == null) {
            return; // can't found wardrobe.
        }
        var result = result();
        NetworkManager.sendToTracking(UpdateWardrobePacket.Field.WARDROBE_COLLISION_SHAPE.buildPacket(wardrobe, result), entity1);
    }

    private SkinContainerEvaluator createIfNeeded(SimpleContainer container, Entity entity) {
        // evaluator only work in the server side.
        if (entity.level().isClientSide()) {
            return null;
        }
        var evaluator = new SkinContainerEvaluator(container);
        evaluator.addListener(this::reload);
        return evaluator;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<EntityCollisionShape> COLLISION_SHAPE = IDataSerializerKey.create("Collisions", EntityCollisionShape.CODEC, null);
    }
}
