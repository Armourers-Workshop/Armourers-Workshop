package moe.plushie.armourers_workshop.library.data.global;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskUserInfo;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

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
            ListenableFutureTask<PlushieUser> task = taskUserInfo.createTask(new FutureCallback<PlushieUser>() {

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

    // Java 8 or lower: 1.6.0_23, 1.7.0, 1.7.0_80, 1.8.0_211
    // Java 9 or higher: 9.0.1, 11.0.4, 12, 12.0.1
    public static String[] getJavaVersion() {
        try {
            String[] version = System.getProperty("java.version").split("[._]");
            // modify the version to aligned format.
            if (Objects.equals(version[0], "1")) {
                return Arrays.copyOfRange(version, 1, version.length);
            }
            return version;
        } catch (Exception ignored) {
        }
        return new String[]{"6", "0"};
    }

    public static boolean isValidJavaVersion() {
        String[] javaVersion = getJavaVersion();
        int[] targetVersion = new int[]{8, 0, 101};
        for (int i = 0; i < javaVersion.length || i < targetVersion.length; ++i) {
            int sv = 0, dv = 0;
            if (i < javaVersion.length) {
                try {
                    sv = Integer.parseInt(javaVersion[i]);
                } catch (Exception ignored) {
                }
            }
            if (i < targetVersion.length) {
                dv = targetVersion[i];
            }
            if (sv < dv) {
                return false;
            } else if (sv > dv) {
                return true;
            }
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
