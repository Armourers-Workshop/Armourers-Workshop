package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.animation.bind.ClientExecutionContextImpl;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.data.BlockEntityAnimationState;
import moe.plushie.armourers_workshop.core.data.EntityAnimationState;
import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.animation.runtime.SkinAnimationManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AnimationManager extends SkinAnimationManager<SkinDescriptor, BakedSkin> {

    public static final AnimationManager NONE = new AnimationManager(null);

    private final ClientExecutionContextImpl context;

    public AnimationManager(Object entity) {
        this.context = new ClientExecutionContextImpl(entity);
    }

    public static AnimationManager of(Entity entity) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            return renderData.animationManager();
        }
        return null;
    }

    public static AnimationManager of(BlockEntity blockEntity) {
        var renderData = BlockEntityRenderData.of(blockEntity);
        if (renderData != null) {
            return renderData.animationManager();
        }
        return null;
    }

    @Override
    protected Item createItem(BakedSkin skin) {
        return new Item(skin.id(), skin.animations(), context);
    }

    @Override
    protected EntityActionSet extractState(Object source) {
        if (source instanceof Entity entity) {
            var animationState = EntityAnimationState.of(entity);
            if (animationState != null) {
                animationState.tick(entity);
                return animationState;
            }
        }
        if (source instanceof BlockEntity entity) {
            var animationState = BlockEntityAnimationState.of(entity);
            if (animationState != null) {
                animationState.tick(entity);
                return animationState;
            }
        }
        return EntityActionSet.IDLE;
    }
}
