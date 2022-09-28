package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.api.common.builder.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.permission.IPermissionContext;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PermissionNodeBuilderImpl<T extends IPermissionNode> implements IPermissionNodeBuilder<T> {

    @Override
    public IPermissionNodeBuilder<T> level(int level) {
        return this;
    }

    @Override
    public T build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        ModLog.debug("Registering Permission '{}'", registryName);
        return ObjectUtils.unsafeCast(makeNode(registryName));
    }

    private IPermissionNode makeNode(ResourceLocation registryName) {
        String key = registryName.getNamespace() + "." + registryName.getPath();
        return new IPermissionNode() {

            @Override
            public Component getName() {
                return TranslateUtils.title("permission." + key);
            }

            @Override
            public Component getDescription() {
                return TranslateUtils.title("permission." + key + ".desc");
            }

            @Override
            public ResourceLocation getRegistryName() {
                return registryName;
            }

            @Override
            public boolean resolve(GameProfile profile, IPermissionContext context) {
                return true;
            }
        };
    }
}
