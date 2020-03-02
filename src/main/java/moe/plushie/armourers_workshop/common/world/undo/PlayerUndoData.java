package moe.plushie.armourers_workshop.common.world.undo;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerUndoData {
    
    private ArrayList<UndoStep> undoSteps;
    private boolean isPainting;
    private UndoStep currentStep = null;
    
    public PlayerUndoData(EntityPlayer player) {
        undoSteps = new ArrayList<UndoStep>();
    }
    
    public void begin() {
        if (isPainting) {
            ModLogger.log(Level.ERROR, "Last tool undo did not end correctly.");
        }
        isPainting = true;
        currentStep = new UndoStep();
    }

    public void end() {
        if (!isPainting) {
            ModLogger.log(Level.ERROR, "Tool ended painting with out marking the start.");
            StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
            for (int i = 1; i < stElements.length; i++) {
                ModLogger.log(Level.ERROR, stElements[i]);
            }
        }
        isPainting = false;
        if (currentStep != null) {
        	undoSteps.add(currentStep);
        	if (undoSteps.size() > ConfigHandler.maxUndos) {
        		undoSteps.remove(0);
        	}
        }
    }

    public void addUndoData(UndoData undoData) {
        if (!isPainting) {
            ModLogger.log(Level.ERROR, "Tool painting with out marking the start.");
        }
        if (currentStep != null) {
        	currentStep.addUndo(undoData);
        } else {
        	ModLogger.log(Level.ERROR, "No undo step. Something is wrong!");
        }
    }

    public void playerPressedUndo(World world) {
    	if (undoSteps.isEmpty()) {
    		return;
    	}
    	UndoStep undoStep = undoSteps.get(undoSteps.size() - 1);
    	undoStep.undo(world);
    	undoSteps.remove(undoSteps.size() - 1);
    }
    
    public int getAvalableUndos() {
        return undoSteps.size();
    }
}
