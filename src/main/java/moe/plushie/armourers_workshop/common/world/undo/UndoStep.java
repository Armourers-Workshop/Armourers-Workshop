package moe.plushie.armourers_workshop.common.world.undo;

import java.util.ArrayList;

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
