package moe.plushie.armourers_workshop.core.handler;

import moe.plushie.armourers_workshop.builder.block.SkinCubeBlock;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.undo.UndoManager;
import moe.plushie.armourers_workshop.utils.undo.action.SetBlockAction;
import moe.plushie.armourers_workshop.utils.undo.action.UndoNamedGroupAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockHandler {

    @SubscribeEvent
    public void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getState().getBlock() instanceof SkinCubeBlock && event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            BlockSnapshot snapshot = event.getBlockSnapshot();
            UndoNamedGroupAction group = new UndoNamedGroupAction(TranslateUtils.title("chat.armourers_workshop.undo.placeBlock"));
            group.push(new SetBlockAction(event.getWorld(), event.getPos(), snapshot.getReplacedBlock(), snapshot.getNbt()));
            UndoManager.of(player.getUUID()).push(group);
        }
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() instanceof SkinCubeBlock && event.getPlayer() != null) {
            PlayerEntity player = event.getPlayer();
            CompoundNBT oldNBT = getNBT(event.getWorld(), event.getPos());
            UndoNamedGroupAction group = new UndoNamedGroupAction(TranslateUtils.title("chat.armourers_workshop.undo.breakBlock"));
            group.push(new SetBlockAction(event.getWorld(), event.getPos(), event.getState(), oldNBT));
            UndoManager.of(player.getUUID()).push(group);
        }
    }

    private CompoundNBT getNBT(IWorld world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity != null) {
            return tileEntity.serializeNBT();
        }
        return null;
    }
}
