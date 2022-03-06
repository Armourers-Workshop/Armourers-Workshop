package moe.plushie.armourers_workshop.core.skin.data.adapter;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import javax.annotation.Nullable;
import java.util.Collections;

public class SkinAdapter<T extends Entity> {

    private final EntityType<T> entityType;

    public SkinAdapter(EntityType<T> entityType) {
        this.entityType = entityType;
    }

    public boolean prepare(@Nullable T entity, Model model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemCameraTransforms.TransformType transformType) {

        return false;
    }

    public void apply(@Nullable T entity, Model model, BakedSkin bakedSkin, BakedSkinPart bakedPart, ItemCameraTransforms.TransformType transformType, float partialTicks, MatrixStack matrixStack) {

    }

    public Iterable<SkinType> getSupportedTypes() {
        return Collections.emptyList();
    }

    public EntityType<T> getEntityType() {
        return entityType;
    }

}
