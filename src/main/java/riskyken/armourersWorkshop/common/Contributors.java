package riskyken.armourersWorkshop.common;

import java.util.ArrayList;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

public class Contributors {
    
    public static Contributors INSTANCE = new Contributors();
    
    private final ArrayList<Contributor> contributors;
    
    public Contributors() {
        contributors = new ArrayList<Contributor>();
        addContributor("eba64cb1-0d29-4434-8d5e-31004b00488c", "RiskyKen", 249, 223, 140);
        addContributor("b027a4f4-d480-426c-84a3-a9cb029f4b72", "Vic", 208, 212, 248);
        addContributor("4fda0709-ada7-48a6-b4bf-0bbce8c40dfa", "Nanoha", 255, 173, 255);
        addContributor("b9e99f95-09fe-497a-8a77-1ccc839ab0f4", "VermillionX", 45, 45, 45);
        addContributor("0d98df01-26da-496c-ba7c-744a20a7b2c2", "Servantfly", 0, 247, 141);
        addContributor("eda5e4cb-3b09-4b2c-b56c-d27d658d2e5d", "Gray_Mooo", 255, 0, 0);
        addContributor("3e6a5f19-bb37-4f9a-94e0-7ccd67ef1d61", "Flummie2000", 92, 32, 102);
        addContributor("e10ebd90-7922-4777-9cf6-76ecc70848ec", "LordPhrozen", 66, 244, 110);
        addContributor("3683eab5-5a23-4cdb-b1f5-38090f1ba4a8", "TheEpicJames", 255, 153, 0);
        addContributor("55b1659a-810f-4687-a514-b3201b09fd69", "V972", 67, 232, 113);
        addContributor("a865907c-6b83-47b2-a088-35688169dc6a", "EzerArch", 255, 255, 255);
        addContributor("d7d977c8-b264-49a3-ac6e-2fae419c8191", "Thundercat_", 74, 89, 169);
        addContributor("41dfb793-40df-4f14-aecc-9c6d265f2813", "BredFace", 40, 214, 63);
        addContributor("4dbf2d3c-884a-4db2-a050-ed0dfedcc1e3", "Equine0x", 128, 0, 0);
    }
    
    private void addContributor(String uuid, String username, int r, int g, int b) {
        contributors.add(new Contributor(uuid, username, (byte)r, (byte)g, (byte)b));
    }
    
    public Contributor getContributor(GameProfile gameProfile) {
        if (gameProfile == null) {
            return null;
        }
        if (gameProfile.getId() == null) {
            return null;
        }
        for (int i = 0; i < contributors.size(); i++) {
            if (gameProfile.getId().equals(contributors.get(i).uuid)) {
                return contributors.get(i);
            }
        }
        return null;
    }
    
    public static class Contributor {
        
        public final UUID uuid;
        public final String username;
        public final byte r;
        public final byte g;
        public final byte b;
        
        public Contributor(String uuid, String username, byte r, byte g, byte b) {
            this.uuid = UUID.fromString(uuid);
            this.username = username;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
