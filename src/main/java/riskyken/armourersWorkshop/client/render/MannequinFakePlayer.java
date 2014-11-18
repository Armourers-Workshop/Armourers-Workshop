package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

public class MannequinFakePlayer extends AbstractClientPlayer {

    public MannequinFakePlayer(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public void addChatMessage(IChatComponent p_145747_1_) {
    }

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
        return false;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return null;
    }

}
