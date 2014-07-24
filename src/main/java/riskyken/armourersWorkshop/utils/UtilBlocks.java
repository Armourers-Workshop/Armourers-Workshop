package riskyken.armourersWorkshop.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class UtilBlocks {
    public static int determineOrientation(World world, int x, int y, int z, EntityLivingBase entity)
    {
        if (MathHelper.abs((float)entity.posX - (float)x) < 2.0F && MathHelper.abs((float)entity.posZ - (float)z) < 2.0F)
        {
            double d0 = entity.posY + entity.getEyeHeight() - (double)entity.yOffset;

            if (d0 - (double)y > 2.0D)
            {
                return 1;
            }

            if ((double)y - d0 > 0.0D)
            {
                return 0;
            }
        }

        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }
}
