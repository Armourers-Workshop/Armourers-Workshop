package moe.plushie.armourers_workshop.init.platform.forge.builder;

import moe.plushie.armourers_workshop.api.registry.IPermissionNodeBuilder;
import moe.plushie.armourers_workshop.api.permission.IPermissionNode;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgePermissionManager;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PermissionNodeBuilderImpl<T extends IPermissionNode> implements IPermissionNodeBuilder<T> {

    private int level = 0;

    @Override
    public IPermissionNodeBuilder<T> level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public T build(String name) {
        ResourceLocation registryName = ModConstants.key(name);
        ModLog.debug("Registering Permission '{}'", registryName);
        return ObjectUtils.unsafeCast(AbstractForgePermissionManager.makeNode(registryName, level));
    }

    public static abstract class NodeImpl implements IPermissionNode {

        private final String key;
        private final ResourceLocation registryName;

        public NodeImpl(ResourceLocation registryName) {
            this.registryName = registryName;
            this.key = registryName.getNamespace() + "." + registryName.getPath();
        }

        public String getKey() {
            return key;
        }

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
    }

}
