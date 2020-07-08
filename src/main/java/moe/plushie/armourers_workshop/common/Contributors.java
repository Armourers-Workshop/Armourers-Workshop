package moe.plushie.armourers_workshop.common;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

public class Contributors {

    public static Contributors INSTANCE = new Contributors();

    private final ArrayList<Contributor> contributors;

    public Contributors() {
        contributors = new ArrayList<Contributor>();
        addContributor("eba64cb1-0d29-4434-8d5e-31004b00488c", "RiskyKen", EnumSet.of(ContributionFlags.PROGRAMMING), 249, 223, 140);

        addContributor("3e6a5f19-bb37-4f9a-94e0-7ccd67ef1d61", "Flummie2000", EnumSet.of(ContributionFlags.SKIN_MAKER, ContributionFlags.TRANSLATOR), 92, 32, 102);
        addContributor("b9e99f95-09fe-497a-8a77-1ccc839ab0f4", "VermillionX", EnumSet.of(ContributionFlags.SKIN_MAKER), 45, 45, 45);
        addContributor("0d98df01-26da-496c-ba7c-744a20a7b2c2", "Servantfly", EnumSet.of(ContributionFlags.SKIN_MAKER), 0, 247, 141);
        addContributor("eda5e4cb-3b09-4b2c-b56c-d27d658d2e5d", "Gray_Mooo", EnumSet.of(ContributionFlags.SKIN_MAKER), 255, 0, 0);

        addContributor("d7d977c8-b264-49a3-ac6e-2fae419c8191", "Thundercat_", EnumSet.of(ContributionFlags.ARTIST, ContributionFlags.MODELER), 20, 232, 211);
        addContributor("e10ebd90-7922-4777-9cf6-76ecc70848ec", "LordPhrozen", EnumSet.of(ContributionFlags.ARTIST), 66, 244, 110);
        addContributor("3683eab5-5a23-4cdb-b1f5-38090f1ba4a8", "TheEpicJames", EnumSet.of(ContributionFlags.ARTIST), 255, 153, 0);
        addContributor("a0c827d3-51c2-4cec-b6d2-0096a6b82f03", "NexusTheBrony", EnumSet.of(ContributionFlags.ARTIST), 255, 0, 255);
        addContributor("2b10d8f1-3273-48a8-9061-cd5e02f45be2", "skylandersking", EnumSet.of(ContributionFlags.ARTIST), 51, 183, 115);
        addContributor("948d2c68-a2c0-4a45-a11c-d24d612af52a", "MikaPikaaa", EnumSet.of(ContributionFlags.ARTIST), 205, 41, 90);
        addContributor("996505b2-3ecd-4bce-aebd-82c713148b7e", "LillyFae", EnumSet.of(ContributionFlags.ARTIST), 136, 206, 201);
        addContributor("0b37421b-e74e-4852-bf57-23907d295ea1", "andrew0030", EnumSet.of(ContributionFlags.ARTIST), 46, 253, 14);

        addContributor("55b1659a-810f-4687-a514-b3201b09fd69", "V972", EnumSet.of(ContributionFlags.TRANSLATOR), 67, 232, 113);
        addContributor("a865907c-6b83-47b2-a088-35688169dc6a", "EzerArch", EnumSet.of(ContributionFlags.TRANSLATOR), 255, 255, 255);
        addContributor("41dfb793-40df-4f14-aecc-9c6d265f2813", "BredFace", EnumSet.of(ContributionFlags.TRANSLATOR), 40, 214, 63);
        addContributor("4dbf2d3c-884a-4db2-a050-ed0dfedcc1e3", "Equine0x", EnumSet.of(ContributionFlags.TRANSLATOR), 128, 0, 0);
        addContributor("7a84e9dd-6703-4eda-8208-bc5df8b51a61", "_Hoppang_", EnumSet.of(ContributionFlags.TRANSLATOR), 12, 67, 155);
        addContributor("f4977a7f-2a96-4641-a351-b3044d9866a2", "IS_Jump", EnumSet.of(ContributionFlags.TRANSLATOR), 178, 152, 231);
        addContributor("58c28704-b377-4080-b3cf-e53bc53eda0a", "BlackGear27", EnumSet.of(ContributionFlags.TRANSLATOR), 35, 23, 23);
        addContributor("260d5854-d2f9-4674-bcda-f311426b91de", "JasonJeong", EnumSet.of(ContributionFlags.TRANSLATOR), 43, 43, 43);
        addContributor("40aada04-7e7b-4f5f-a513-a92e581c1097", "M_H_Berre", EnumSet.of(ContributionFlags.TRANSLATOR), 255, 255, 64);
        addContributor("3da3958f-f22c-4064-bddb-148dcfc101ec", "SQwatermark", EnumSet.of(ContributionFlags.TRANSLATOR), 255, 0, 0);

        addContributor("c1a62d17-65bc-4256-9f54-af38270f9559", "DoomRater", EnumSet.of(ContributionFlags.WIKI_EDITOR), 158, 25, 2);

        addContributor("f9a9c7bc-c73a-4f0e-af73-133468513bb9", "Duvain_Feynorim", EnumSet.of(ContributionFlags.MODELER), 121, 9, 219);

        addContributor("b027a4f4-d480-426c-84a3-a9cb029f4b72", "Vic", EnumSet.of(ContributionFlags.OTHER), 208, 212, 248);

        addContributor("4fda0709-ada7-48a6-b4bf-0bbce8c40dfa", "Nanoha", EnumSet.of(ContributionFlags.NONE), 255, 173, 255);
        addContributor("31873a23-125e-4752-8607-0f1c3cb22c84", "Garoam", EnumSet.of(ContributionFlags.NONE), 96, 25, 149);

    }

    private void addContributor(String uuid, String username, EnumSet<ContributionFlags> contributions, int r, int g, int b) {
        contributors.add(new Contributor(uuid, username, contributions, (byte) r, (byte) g, (byte) b));
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
        public final EnumSet<ContributionFlags> contributions;
        public final byte r;
        public final byte g;
        public final byte b;

        public Contributor(String uuid, String username, EnumSet<ContributionFlags> contributions, byte r, byte g, byte b) {

            this.uuid = UUID.fromString(uuid);
            this.username = username;
            this.contributions = contributions;
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }

    public enum ContributionFlags {
        NONE,
        PROGRAMMING,
        TRANSLATOR,
        ARTIST,
        SKIN_MAKER,
        MODELER,
        WIKI_EDITOR,
        OTHER;

        public static final EnumSet<ContributionFlags> ALL_OPTS = EnumSet.allOf(ContributionFlags.class);
    }
}
