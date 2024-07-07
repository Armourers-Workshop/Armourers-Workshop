package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.resources.ResourceLocation;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class Constructor {

    public static ResourceLocation create(@ThisClass Class<?> clazz, String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ModelResourceLocation create(@ThisClass Class<?> clazz, IResourceLocation location, String variant) {
        var namespace = location.getNamespace();
        var path = location.getPath();
        switch (EnvironmentManager.getPlatformType()) {
            case FABRIC -> {
                // in 1.21 fabric only allows adding fabric_resources models.
                if (variant.equals("inventory")) {
                    path = "item/" + path;
                    variant = "fabric_resource";
                }
            }
            case FORGE -> {
                // in 1.21 neoforge only allows adding standalone models.
                if (variant.equals("inventory")) {
                    path = "item/" + path;
                    variant = "standalone";
                }
            }
        }
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path), variant);
    }
}
