package moe.plushie.armourers_workshop.compatibility.client;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

@Available("[1.18, 1.19)")
public interface AbstractEntityRendererProviderImpl {

    interface Provider<T extends Entity> extends AbstractEntityRendererProviderImpl {

        EntityRenderer<T> create(Context context);

        @Environment(EnvType.CLIENT)
        default EntityRenderer<T> create(EntityRendererProvider.Context context) {
            return create(new Context(context));
        }
    }

    class Context extends EntityRendererProvider.Context {

        public Context(Minecraft minecraft) {
            super(minecraft.getEntityRenderDispatcher(),
                    minecraft.getItemRenderer(),
                    minecraft.getResourceManager(),
                    minecraft.getEntityModels(),
                    minecraft.font);
        }

        public Context(EntityRendererProvider.Context impl) {
            super(impl.getEntityRenderDispatcher(),
                    impl.getItemRenderer(),
                    impl.getResourceManager(),
                    impl.getModelSet(),
                    impl.getFont());
        }

        public static Context sharedContext() {
            return new Context(Minecraft.getInstance());
        }
    }
}
