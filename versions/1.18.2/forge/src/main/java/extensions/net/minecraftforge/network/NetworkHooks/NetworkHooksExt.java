package extensions.net.minecraftforge.network.NetworkHooks;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Consumer;

@Extension
public class NetworkHooksExt {

    public static void openScreen(@ThisClass Class<?> clazz, ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        NetworkHooks.openGui(player, containerSupplier, extraDataWriter);
    }
}
