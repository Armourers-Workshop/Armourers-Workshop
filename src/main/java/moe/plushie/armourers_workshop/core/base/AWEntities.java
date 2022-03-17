/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package moe.plushie.armourers_workshop.core.base;

import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.entity.SeatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Consumer;

public final class AWEntities {

    private static final HashMap<ResourceLocation, EntityType<?>> REGISTERED_ENTITY_TYPES = new HashMap<>();

    public static final EntityType<MannequinEntity> MANNEQUIN = register("mannequin", MannequinEntity::new, EntityClassification.MISC, b -> b.sized(0.6f, 1.88f));
    public static final EntityType<SeatEntity> SEAT = register("seat", SeatEntity::new, EntityClassification.MISC, b -> b.sized(0.0f, 0.0f).noSummon());

    public static void forEach(Consumer<EntityType<?>> action) {
        REGISTERED_ENTITY_TYPES.values().forEach(action);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.IFactory<T> entityFactory, EntityClassification classification) {
        return register(name, entityFactory, classification, null);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.IFactory<T> entityFactory, EntityClassification classification, Consumer<EntityType.Builder<T>> customizer) {
        ResourceLocation registryName = AWCore.resource(name);
        EntityType.Builder<T> builder = EntityType.Builder.of(entityFactory, classification);
        if (customizer != null) {
            customizer.accept(builder);
        }
        EntityType<T> entityType = builder.build(name);
        entityType.setRegistryName(registryName);
        REGISTERED_ENTITY_TYPES.put(registryName, entityType);
        return entityType;
    }

}
