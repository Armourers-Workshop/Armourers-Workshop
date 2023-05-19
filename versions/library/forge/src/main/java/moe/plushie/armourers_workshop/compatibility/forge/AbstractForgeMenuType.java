package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Consumer;

@Available("[1.19, )")
public interface AbstractForgeMenuType {

    static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
        return IForgeMenuType.create(factory);
    }

    static void openMenu(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        NetworkHooks.openScreen(player, containerSupplier, extraDataWriter);
    }
}
