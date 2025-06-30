package moe.plushie.armourers_workshop.core.client.animation.bind;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.LocalVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.LevelSelector;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class ClientExecutionContextImpl extends ExecutionContextImpl {

    private final Minecraft minecraft = EnvironmentManager.getClient();
    private final ClientLevelSelectorImpl<ClientLevel> levelSelector = new ClientLevelSelectorImpl<>();

    public ClientExecutionContextImpl(Object entity) {
        super(entity);
    }

    protected ClientExecutionContextImpl(Object entity, LocalVariableStorage stack) {
        super(entity, stack);
    }

    @Override
    public ExecutionContext fork(Object target) {
        return new ClientExecutionContextImpl(target, stack);
    }

    @Override
    public LevelSelector level() {
        if (minecraft.level != null) {
            return levelSelector.apply(minecraft.level);
        }
        return null;
    }

    @Override
    public double fps() {
        return minecraft.getFps();
    }


    @Override
    public int entityCount() {
        if (minecraft.level != null) {
            return minecraft.level.getEntityCount();
        }
        return 0;
    }

    @Override
    public boolean isRenderingInInventory() {
        // TODO: @SAGSSE no impl
        return false;
    }

    @Override
    public boolean isRenderingInFirstPersonMod() {
        // TODO: @SAGSSE no impl
        return false;
    }

    @Override
    public boolean isFirstPerson() {
        return minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
    }

    @Override
    public double getCameraDistanceFormEntity(Entity entity) {
        return minecraft.gameRenderer.getMainCamera().getPosition().distanceTo(entity.position());
    }

    @Override
    protected VariableStorage createEntity(Object target) {
        if (target instanceof LocalPlayer entity1) {
            var impl = new ClientPlayerSelectorImpl<>();
            return impl.apply(entity1, this);
        }
        return super.createEntity(target);
    }
}
