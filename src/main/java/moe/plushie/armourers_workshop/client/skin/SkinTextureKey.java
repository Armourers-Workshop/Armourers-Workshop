package moe.plushie.armourers_workshop.client.skin;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkinTextureKey {
    
    private final int skinId;
    private final ISkinDye skinDye;
    private final IExtraColours extraColours;
    
    public SkinTextureKey(int skinId, ISkinDye skinDye, IExtraColours extraColours) {
        this.skinId = skinId;
        this.skinDye = skinDye;
        this.extraColours = extraColours;
    }
    
    public ISkinDye getSkinDye() {
        return skinDye;
    }
    
    public IExtraColours getExtraColours() {
        return extraColours;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extraColours == null) ? 0 : extraColours.hashCode());
        result = prime * result + ((skinDye == null) ? 0 : skinDye.hashCode());
        result = prime * result + skinId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinTextureKey other = (SkinTextureKey) obj;
        if (extraColours == null) {
            if (other.extraColours != null)
                return false;
        } else if (!extraColours.equals(other.extraColours))
            return false;
        if (skinDye == null) {
            if (other.skinDye != null)
                return false;
        } else if (!skinDye.equals(other.skinDye))
            return false;
        if (skinId != other.skinId)
            return false;
        return true;
    }
    
    
}
