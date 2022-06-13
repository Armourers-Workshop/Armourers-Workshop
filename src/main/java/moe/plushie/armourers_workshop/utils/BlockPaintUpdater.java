package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintable;
import moe.plushie.armourers_workshop.core.item.impl.IPaintApplier;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateBlockColorPacket;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.init.common.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class BlockPaintUpdater implements IPaintApplier.IPaintUpdater {

    protected final Item item;
    protected final HashMap<IPaintable, HashMap<Direction, IPaintColor>> changes = new HashMap<>();

    public BlockPaintUpdater(Item item) {
        this.item = item;
    }

    @Override
    public void begin(ItemUseContext context) {
        ModLog.debug("{} => start capture color changes", item);
    }

    @Override
    public void add(IPaintable target, Direction direction, IPaintColor newColor) {
        ModLog.debug("{} => set color {} at {}", item, newColor, direction);
        changes.computeIfAbsent(target, k -> new HashMap<>()).put(direction, newColor);
    }

    @Override
    public void commit(ItemUseContext context) {
        if (changes.isEmpty()) {
            return;
        }
        ModLog.debug("{} => commit {} block changes", item, changes.size());
        World world = context.getLevel();
        if (!world.isClientSide()) {
            return;
        }
        UpdateBlockColorPacket packet = new UpdateBlockColorPacket(context, changes);
        NetworkHandler.getInstance().sendToServer(packet);

        // ItemShadeNoiseTool/ItemShadeNoiseTool
//        if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
//            worldIn.playSound(null, pos, ModSounds.NOISE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 0.9F);
//        } else {
//            worldIn.playSound(null, pos, ModSounds.NOISE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 1.5F);
//        }

        // ItemPaintbrush/ItemPaintRoller
        //         world.playSound(null, pos, ModSounds.PAINT, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
//        if (spawnParticles) {
//            spawnPaintParticles(world, pos, facing, newColour);
//        ParticleTypes
//        world.addParticle();
//        }

        // from ItemPaintbrush
//        SoundEvent soundEvent = ModSounds.PAINT;
//        if (ModHolidays.APRIL_FOOLS.isHolidayActive()) {
//            soundEvent = ModSounds.BOI;
//        }
//        if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
//            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.2F + 0.9F);
//        } else {
//            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.2F + 1.5F);
//        }
        //        if (spawnParticles) {
//            spawnPaintParticles(world, pos, facing, newColour);
//        }
    }
}
