package moe.plushie.armourers_workshop.common.permission;

import java.util.ArrayList;

public interface IPermissionHolder {
    
    public void getPermissions(ArrayList<Permission> permissions);
    
    public String getPermissionName();
}
