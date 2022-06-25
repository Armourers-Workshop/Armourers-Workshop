package moe.plushie.armourers_workshop.library.data.global;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskUserInfo;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;

public final class GlobalSkinLibraryUtils {

    private static final HashMap<Integer, PlushieUser> USERS = new HashMap<Integer, PlushieUser>();
    private static final HashSet<Integer> DOWNLOADED_USERS = new HashSet<Integer>();

    private GlobalSkinLibraryUtils() {
    }

    public static PlushieUser getUserInfo(int userId) {
        synchronized (USERS) {
            if (USERS.containsKey(userId)) {
                return USERS.get(userId);
            }
        }
        if (!DOWNLOADED_USERS.contains(userId)) {
            DOWNLOADED_USERS.add(userId);

            GlobalTaskUserInfo taskUserInfo = new GlobalTaskUserInfo(userId);
            ListenableFutureTask<PlushieUser> task = taskUserInfo.createTask();
            Futures.addCallback(task, new FutureCallback<PlushieUser>() {

                @Override
                public void onSuccess(PlushieUser result) {
                    if (result != null) {
                        synchronized (USERS) {
                            USERS.put(userId, result);
                        }
                    } else {
                        ModLog.error("Failed downloading info for user id: " + userId);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    // NO-OP
                }
            });
            taskUserInfo.runTask(task);
        }
        return null;
    }

    public static int[] getJavaVersion() {
        int[] version = new int[] { 6, 0 };
        try {
            String java = System.getProperty("java.version");
            String[] javaSplit = java.split("_");
            int javaVersion = Integer.valueOf(javaSplit[1]);
            version[1] = javaVersion;
            javaSplit = javaSplit[0].split("\\.");
            version[0] = Integer.valueOf(javaSplit[1]);
        } catch (Exception e) {
        }
        return version;
    }

    public static boolean isValidJavaVersion(int[] javaVersion) {
        if (javaVersion[0] < 8) {
            return false;
        }
        if (javaVersion[1] < 101) {
            return false;
        }
        return true;
    }

    public static boolean isValidJavaVersion() {
        int[] javaVersion = getJavaVersion();
        if (javaVersion[0] < 8 & javaVersion[1] < 101) {
            return false;
        }
        return true;
    }

    public static String allSearchTypes() {
        StringBuilder searchTypesBuilder = new StringBuilder();
        for (ISkinType skinType : SkinTypes.values()) {
            ResourceLocation registryName = skinType.getRegistryName();
            if (skinType != SkinTypes.UNKNOWN && registryName != null) {
                if (searchTypesBuilder.length() != 0) {
                    searchTypesBuilder.append(";");
                }
                searchTypesBuilder.append(registryName);
            }
        }
        return searchTypesBuilder.toString();
    }
}