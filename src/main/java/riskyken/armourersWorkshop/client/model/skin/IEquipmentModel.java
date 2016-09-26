package riskyken.armourersWorkshop.client.model.skin;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.skin.data.Skin;

@SideOnly(Side.CLIENT)
public interface IEquipmentModel {
    
    public void render(Entity entity, Skin armourData, float limb1, float limb2, float limb3, float headY, float headX);
    
    public void render(Entity entity, ModelBiped modelBiped, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance);
}
