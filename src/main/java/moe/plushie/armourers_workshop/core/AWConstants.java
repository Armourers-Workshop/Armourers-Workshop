package moe.plushie.armourers_workshop.core;

import net.minecraft.world.storage.FolderName;

public final class AWConstants {

    public final static class Folder {
        public static final FolderName LOCAL_DB = new FolderName("skin-database");
    }


    public final static class NBT {
        public static final String SKIN = "ArmourersWorkshop";
        public static final String SKIN_TYPE = "SkinType";
        public static final String SKIN_IDENTIFIER = "Identifier";

        public static final String TEXTURE_TYPE = "Type";
        public static final String TEXTURE_URL = "URL";
        public static final String TEXTURE_PROFILE = "Profile";

        public static final String COLOR = "Color";

        public static final String MANNEQUIN_IS_CHILD = "Child";
        public static final String MANNEQUIN_IS_FLYING = "Flying";
        public static final String MANNEQUIN_IS_GHOST = "Ghost";
        public static final String MANNEQUIN_IS_VISIBLE = "Visible";
        public static final String MANNEQUIN_EXTRA_RENDER = "ExtraRender";
        public static final String MANNEQUIN_TEXTURE = "Texture";
    }

}
