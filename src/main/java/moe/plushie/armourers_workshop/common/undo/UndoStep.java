package moe.plushie.armourers_workshop.common.undo;

import java.awt.Color;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UndoStep {
	
	private ArrayList<UndoData> undos;
	
	public UndoStep() {
		undos = new ArrayList<UndoData>();
	}
	
	public void addUndo(UndoData undoData) {
		undos.add(undoData);
	}
	
	public void undo(World world) {
		for (int i = 0; i < undos.size(); i++) {
			undos.get(i).undo(world);
		}
		undos.clear();
	}
}
