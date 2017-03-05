package riskyken.armourersWorkshop.common.undo;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.utils.ModLogger;

public class PlayerUndoData {
    
    private ArrayList<UndoData> undos;
    private ArrayList<Integer> markers;
    private EntityPlayer player;
    private boolean isPainting;
    private int markerCount = 0;
    
    public PlayerUndoData(EntityPlayer player) {
        this.player = player;
        undos = new ArrayList<UndoData>();
        markers = new ArrayList<Integer>();
    }
    
    public void begin() {
        if (isPainting) {
            ModLogger.log(Level.ERROR, "Last tool undo did not end correctly.");
        }
        isPainting = true;
        markerCount = 0;
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
        if (markerCount != 0) {
            markers.add(markerCount);
        }
    }

    public void addUndoData(UndoData undoData) {
        if (!isPainting) {
            ModLogger.log(Level.ERROR, "Tool painting with out marking the start.");
        }
        markerCount++;
        undos.add(undoData);
        if (markers.size() > UndoManager.maxUndos) {
            if (markers.size() < 1) {
                int undoMarker = markers.get(0);
                markers.remove(0);
                for (int i = 0; i < undoMarker; i++) {
                    undos.remove(i);
                }
            } else {
                ModLogger.log(Level.ERROR, "No undo markers. Something is wrong!");
            }
        }
    }

    public void playerPressedUndo(World world) {
        if (markers.size() < 1) {
            ModLogger.log(Level.ERROR, "No undo markers. Something is wrong!");
            undoLast(world);
        } else {
            int undoMarker = markers.get(markers.size() - 1);
            for (int i = 0; i < undoMarker; i++) {
                undoLast(world);
            }
            markers.remove(markers.size() - 1);
        }
    }
    
    private void undoLast(World world) {
        if (undos.size() < 1) {
            return;
        }
        UndoData undoData = undos.get(undos.size() - 1);
        if (world.provider.dimensionId != undoData.dimensionId) {
            return;
        }
        
        Block block = world.getBlock(undoData.blockX, undoData.blockY, undoData.blockZ);
        if (block instanceof IPantableBlock) {
            Color c = new Color(undoData.rgb[0] & 0xFF, undoData.rgb[1] & 0xFF, undoData.rgb[2] & 0xFF);
            int rgb = c.getRGB();
            
            IPantableBlock worldColourable = (IPantableBlock) block;
            worldColourable.setColour(world, undoData.blockX, undoData.blockY, undoData.blockZ, rgb, undoData.side);
            worldColourable.setPaintType(world, undoData.blockX, undoData.blockY, undoData.blockZ, PaintType.getPaintTypeFormSKey(undoData.paintType), undoData.side);
        }
        undos.remove(undos.size() - 1);
    }
    
    public int getAvalableUndos() {
        return undos.size();
    }
}
