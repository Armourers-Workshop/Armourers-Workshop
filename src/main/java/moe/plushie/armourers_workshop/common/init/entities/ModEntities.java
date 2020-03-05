package moe.plushie.armourers_workshop.common.init.entities;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    private static final ArrayList<ModEntity> MOD_ENTITIES = new ArrayList<ModEntity>();

    private static final ModEntity ENTITY_SEAT = new ModEntity("seat", EntitySeat.class, 1, 10, 20, false);
    private static final ModEntity ENTITY_MANNEQUIN = new ModEntity("mannequin", EntityMannequin.class, 2, 64, 200, false);

    public static void registerEntities() {
        for (ModEntity modEntity : MOD_ENTITIES) {
            registerEntity(modEntity);
        }
    }

    private static void registerEntity(ModEntity modEntity) {
        ResourceLocation rl = new ResourceLocation(LibModInfo.ID, modEntity.getName());
        EntityRegistry.registerModEntity(rl, modEntity.getEntityClass(), modEntity.getName(), modEntity.getId(),
                ArmourersWorkshop.getInstance(), modEntity.getTrackingRange(), modEntity.getUpdateFrequency(), modEntity.isSendsVelocityUpdates());
    }

    private static class ModEntity {

        private final String name;
        private final Class<? extends Entity> entityClass;
        private final int id;
        private final int trackingRange;
        private final int updateFrequency;
        private final boolean sendsVelocityUpdates;

        public ModEntity(String name, Class<? extends Entity> entityClass, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
            this.name = name;
            this.entityClass = entityClass;
            this.id = id;
            this.trackingRange = trackingRange;
            this.updateFrequency = updateFrequency;
            this.sendsVelocityUpdates = sendsVelocityUpdates;
            MOD_ENTITIES.add(this);
        }

        public String getName() {
            return name;
        }

        public Class<? extends Entity> getEntityClass() {
            return entityClass;
        }

        public int getId() {
            return id;
        }

        public int getTrackingRange() {
            return trackingRange;
        }

        public int getUpdateFrequency() {
            return updateFrequency;
        }

        public boolean isSendsVelocityUpdates() {
            return sendsVelocityUpdates;
        }
    }
}
