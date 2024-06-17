package moe.plushie.armourers_workshop.init;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.UUID;

public class ModContributors {

    public static Contributor dev;
    public static final HashMap<UUID, Contributor> values = Builder.builder()
            .add("eba64cb1-0d29-4434-8d5e-31004b00488c", "RiskyKen", 0xf9df8c, ContributionFlags.PROGRAMMING)
            .add("889bebd9-9ebc-4dec-97ee-de9907cbbc85", "SAGESSE_90", 0x00a3a3, ContributionFlags.PROGRAMMING)
            .add("3e6a5f19-bb37-4f9a-94e0-7ccd67ef1d61", "Flummie2000", 0x5c2066, ContributionFlags.SKIN_MAKER, ContributionFlags.TRANSLATOR)
            .add("b9e99f95-09fe-497a-8a77-1ccc839ab0f4", "VermillionX", 0x2d2d2d, ContributionFlags.SKIN_MAKER)
            .add("0d98df01-26da-496c-ba7c-744a20a7b2c2", "Servantfly", 0x00f78d, ContributionFlags.SKIN_MAKER)
            .add("eda5e4cb-3b09-4b2c-b56c-d27d658d2e5d", "Gray_Mooo", 0xff0000, ContributionFlags.SKIN_MAKER)
            .add("d7d977c8-b264-49a3-ac6e-2fae419c8191", "Thundercat_", 0x14e8d3, ContributionFlags.ARTIST, ContributionFlags.MODELER)
            .add("e10ebd90-7922-4777-9cf6-76ecc70848ec", "LordPhrozen", 0x42f46e, ContributionFlags.ARTIST)
            .add("3683eab5-5a23-4cdb-b1f5-38090f1ba4a8", "TheEpicJames", 0xff9900, ContributionFlags.ARTIST)
            .add("a0c827d3-51c2-4cec-b6d2-0096a6b82f03", "NexusTheBrony", 0xff00ff, ContributionFlags.ARTIST)
            .add("2b10d8f1-3273-48a8-9061-cd5e02f45be2", "skylandersking", 0x33b773, ContributionFlags.ARTIST)
            .add("948d2c68-a2c0-4a45-a11c-d24d612af52a", "MikaPikaaa", 0xcd295a, ContributionFlags.ARTIST)
            .add("996505b2-3ecd-4bce-aebd-82c713148b7e", "LillyFae", 0x88cec9, ContributionFlags.ARTIST)
            .add("0b37421b-e74e-4852-bf57-23907d295ea1", "andrew0030", 0x2efd0e, ContributionFlags.ARTIST)
            .add("55b1659a-810f-4687-a514-b3201b09fd69", "V972", 0x43e871, ContributionFlags.TRANSLATOR)
            .add("a865907c-6b83-47b2-a088-35688169dc6a", "EzerArch", 0xffffff, ContributionFlags.TRANSLATOR)
            .add("41dfb793-40df-4f14-aecc-9c6d265f2813", "BredFace", 0x28d63f, ContributionFlags.TRANSLATOR)
            .add("4dbf2d3c-884a-4db2-a050-ed0dfedcc1e3", "Equine0x", 0x800000, ContributionFlags.TRANSLATOR)
            .add("7a84e9dd-6703-4eda-8208-bc5df8b51a61", "_Hoppang_", 0x0c439b, ContributionFlags.TRANSLATOR)
            .add("f4977a7f-2a96-4641-a351-b3044d9866a2", "IS_Jump", 0xb298e7, ContributionFlags.TRANSLATOR)
            .add("58c28704-b377-4080-b3cf-e53bc53eda0a", "BlackGear27", 0x231717, ContributionFlags.TRANSLATOR)
            .add("260d5854-d2f9-4674-bcda-f311426b91de", "JasonJeong", 0x2b2b2b, ContributionFlags.TRANSLATOR)
            .add("40aada04-7e7b-4f5f-a513-a92e581c1097", "M_H_Berre", 0xffff40, ContributionFlags.TRANSLATOR)
            .add("3da3958f-f22c-4064-bddb-148dcfc101ec", "SQwatermark", 0xff0000, ContributionFlags.TRANSLATOR)
            .add("c1a62d17-65bc-4256-9f54-af38270f9559", "DoomRater", 0x9e1902, ContributionFlags.WIKI_EDITOR)
            .add("f9a9c7bc-c73a-4f0e-af73-133468513bb9", "Duvain_Feynorim", 0x7909db, ContributionFlags.MODELER)
            .add("448150f9-ee48-4ce9-a3de-141a57c2857b", "KokonoMiyako", 0Xb1cfeb, ContributionFlags.TESTER)
            .add("c36bf010-3fc8-4def-bba2-ab80527631af", "Shmanit", 0xc98a4b, ContributionFlags.TESTER)
            .add("293c894b-07cc-4115-ab99-2692d63abb7e", "Deep_1mpact", 0xf7dc91, ContributionFlags.TESTER)
            .add("b027a4f4-d480-426c-84a3-a9cb029f4b72", "Vic", 0xd0d4f8, ContributionFlags.OTHER)
            .add("4fda0709-ada7-48a6-b4bf-0bbce8c40dfa", "Nanoha", 0xffadff, ContributionFlags.NONE)
            .add("31873a23-125e-4752-8607-0f1c3cb22c84", "Garoam", 0x601995, ContributionFlags.NONE)
            .build();

    public static Contributor of(GameProfile gameProfile) {
        if (gameProfile != null) {
            UUID uuid = gameProfile.getId();
            if (uuid != null) {
                return values.get(uuid);
            }
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public static Contributor by(Entity entity) {
        if (entity instanceof MannequinEntity mannequin) {
            if (mannequin.isExtraRenderer()) {
                var descriptor = mannequin.getTextureDescriptor();
                return of(PlayerTextureLoader.getInstance().getGameProfile(descriptor));
            }
            return null;
        }
        if (entity instanceof LocalPlayer) {
            return getCurrentContributor();
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public static Contributor getCurrentContributor() {
        if (EnvironmentManager.isDevelopment()) {
            return dev;
        }
        return of(Minecraft.getInstance().getUser().getGameProfile());
    }

    public enum ContributionFlags {
        NONE,
        PROGRAMMING,
        TRANSLATOR,
        ARTIST,
        SKIN_MAKER,
        MODELER,
        WIKI_EDITOR,
        OTHER,
        TESTER
    }

    public static class Contributor {

        public final UUID uuid;
        public final String username;
        public final EnumSet<ContributionFlags> contributions;
        public final int color;

        public Contributor(String uuid, String username, EnumSet<ContributionFlags> contributions, int color) {
            this.uuid = UUID.fromString(uuid);
            this.username = username;
            this.contributions = contributions;
            this.color = color;
        }
    }

    public static class Builder {
        HashMap<UUID, Contributor> contributors = new HashMap<>();

        static Builder builder() {
            return new Builder();
        }

        Builder add(String uuid, String username, int color, ContributionFlags... flags) {
            var set = EnumSet.copyOf(Lists.newArrayList(flags));
            var contributor = new Contributor(uuid, username, set, color);
            contributors.put(contributor.uuid, contributor);
            if (contributor.contributions.contains(ContributionFlags.PROGRAMMING)) {
                dev = contributor;
            }
            return this;
        }

        HashMap<UUID, Contributor> build() {
            return contributors;
        }
    }
}
