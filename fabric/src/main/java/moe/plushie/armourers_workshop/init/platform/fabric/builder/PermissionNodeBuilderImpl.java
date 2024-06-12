package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.network.chat.Component;

public class PermissionNodeBuilderImpl<T extends IPermissionNode> implements IPermissionNodeBuilder<T> {

    @Override
    public IPermissionNodeBuilder<T> level(int level) {
        return this;
    }

    @Override
    public T build(String name) {
        IResourceLocation registryName = ModConstants.key(name);
        ModLog.debug("Registering Permission '{}'", registryName);
        return ObjectUtils.unsafeCast(makeNode(registryName));
    }

    private IPermissionNode makeNode(IResourceLocation registryName) {
        String key = registryName.toLanguageKey();
        return new IPermissionNode() {

            @Override
            public Component getName() {
                return Component.translatable("permission." + key);
            }

            @Override
            public Component getDescription() {
                return Component.translatable("permission." + key + ".desc");
            }

            @Override
            public IResourceLocation getRegistryName() {
                return registryName;
            }

            @Override
            public boolean resolve(GameProfile profile, IPermissionContext context) {
                return true;
            }
        };
    }
}
