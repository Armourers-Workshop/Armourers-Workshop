package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.builder.world.SkinCubeColorApplier;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateBlockColorPacket;
import net.minecraft.item.ItemUseContext;

public class BlockPaintUpdater /*implements IPaintApplier.IPaintUpdater*/ {

//    protected final Item item;
//    protected final HashMap<IPaintable, HashMap<Direction, IPaintColor>> changes = new HashMap<>();
//
//    public BlockPaintUpdater(Item item) {
//        this.item = item;
//    }
//
//    @Override
//    public void begin(ItemUseContext context) {
//        ModLog.debug("{} => start capture color changes", item);
//    }
//
//    @Override
//    public void add(IPaintable target, Direction direction, IPaintColor newColor) {
//        ModLog.debug("{} => set color {} at {}", item, newColor, direction);
//        changes.computeIfAbsent(target, k -> new HashMap<>()).put(direction, newColor);
//    }
//
//    @Override
    public static void commit(SkinCubeColorApplier applier, ItemUseContext context) {
//        if (changes.isEmpty()) {
//            return;
//        }
//        ModLog.debug("{} => commit {} block changes", item, changes.size());
//        World world = context.getLevel();
//        if (!world.isClientSide()) {
//            return;
//        }

        // build(client) => receive(server)
        // apply(client)    apply(server)
        applier.apply(context);
        UpdateBlockColorPacket packet = new UpdateBlockColorPacket(context, applier);
        NetworkHandler.getInstance().sendToServer(packet);
    }
}
