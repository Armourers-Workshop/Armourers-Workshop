package moe.plushie.armourers_workshop.core.render.skin;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;

public class ArrowSkinRenderer<T extends ArrowEntity, M extends Model> extends SkinRenderer<T, M> {
    public ArrowSkinRenderer(EntityType<T> entityType) {
        super(entityType);
    }
}
