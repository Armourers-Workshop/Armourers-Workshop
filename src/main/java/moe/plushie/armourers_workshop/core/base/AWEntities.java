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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public final class AWEntities {

    public static final EntityType<MannequinEntity> MANNEQUIN = register("mannequin", MannequinEntity::new, EntityClassification.MISC, b -> b.sized(0.6f, 1.88f));

    private static <T extends Entity> EntityType<T> register(String name, EntityType.IFactory<T> entityFactory) {
        return register(name, entityFactory, EntityClassification.CREATURE, null);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.IFactory<T> entityFactory, EntityClassification classification, Consumer<EntityType.Builder<T>> customizer) {
        ResourceLocation registryName = AWCore.resource(name);
        EntityType.Builder<T> builder = EntityType.Builder.of(entityFactory, classification);
        if (customizer != null) {
            customizer.accept(builder);
        }
        EntityType<T> result = builder.build(name);
        result.setRegistryName(registryName);
        return result;
    }
}
