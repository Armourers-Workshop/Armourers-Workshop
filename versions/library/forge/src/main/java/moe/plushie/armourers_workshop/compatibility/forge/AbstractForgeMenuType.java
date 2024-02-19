package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;

import java.util.function.Consumer;

@Available("[1.21, )")
public interface AbstractForgeMenuType {

    static <T extends AbstractContainerMenu> MenuType<T> create(IContainerFactory<T> factory) {
        return IMenuTypeExtension.create(factory);
    }

    static void openMenu(ServerPlayer player, MenuProvider containerSupplier, Consumer<FriendlyByteBuf> extraDataWriter) {
        player.openMenu(containerSupplier, extraDataWriter);
    }
}
