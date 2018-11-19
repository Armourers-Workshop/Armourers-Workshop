package moe.plushie.armourers_workshop.common.permission;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class Permission {
    
    public final String node;
    public final DefaultPermissionLevel level;
    public final String description;
    
    public Permission(String node, DefaultPermissionLevel level, String description) {
        this.node = node;
        this.level = level;
        this.description = description;
    }
    
    public Permission(String node, DefaultPermissionLevel level) {
        this(LibModInfo.ID + "." + node, level, "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
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
        Permission other = (Permission) obj;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Permission [node=" + node + ", level=" + level + ", description=" + description + "]";
    }
}
