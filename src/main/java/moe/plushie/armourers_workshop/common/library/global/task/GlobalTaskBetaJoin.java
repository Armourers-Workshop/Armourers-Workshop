package moe.plushie.armourers_workshop.common.library.global.task;

import java.net.URLEncoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.common.library.global.auth.MinecraftAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult.JoinResult;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;

public class GlobalTaskBetaJoin extends GlobalTask<BetaJoinResult> {

    private static final String URL_JOIN = "join.php?username=%s&uuid=%s&serverId=%s";

    public GlobalTaskBetaJoin() {
        super(PlushieAction.BETA_JOIN, true);
    }

    @Override
    public BetaJoinResult call() throws Exception {
        permissionCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;
        if (session.hasServerId()) {
            return new BetaJoinResult(JoinResult.ALREADY_JOINED);
        }

        GameProfile gameProfile = Minecraft.getMinecraft().player.getGameProfile();
        String username = URLEncoder.encode(gameProfile.getName(), "UTF-8");
        String uuid = URLEncoder.encode(gameProfile.getId().toString(), "UTF-8");
        String serverId = String.valueOf(getBaseUrl().hashCode());

        if (!MinecraftAuth.checkAndRefeshAuth(Minecraft.getMinecraft().getSession(), serverId)) {
            ModLogger.log("Failed MC Auth");
            return new BetaJoinResult(JoinResult.MINECRAFT_AUTH_FAIL);
        } else {
            ModLogger.log("MC Auth Done");
        }

        String urlJoin = String.format(getBaseUrl() + URL_JOIN, username, uuid, serverId);

        JsonObject jsonJoinResult = new JsonParser().parse(downloadString(urlJoin)).getAsJsonObject();

        if (jsonJoinResult.has("action") & jsonJoinResult.has("valid")) {
            String action = jsonJoinResult.get("action").getAsString();
            boolean valid = jsonJoinResult.get("valid").getAsBoolean();
            if (action.equals("join")) {
                if (valid) {
                    PlushieAuth.doRemoteUserCheck();
                    PlushieAuth.PLUSHIE_SESSION.authenticate(jsonJoinResult);
                    return new BetaJoinResult(JoinResult.JOINED);
                } else {
                    String reason = "";
                    if (jsonJoinResult.has("reason")) {
                        reason = jsonJoinResult.get("reason").getAsString();
                    }
                    return new BetaJoinResult(JoinResult.JOIN_FAILED, reason);
                }
            }
        }

        return new BetaJoinResult(JoinResult.JOIN_FAILED);
    }

    public static class BetaJoinResult {

        private final JoinResult joinResult;
        private final String message;

        public BetaJoinResult(JoinResult joinResult) {
            this.joinResult = joinResult;
            this.message = joinResult.toString().toLowerCase();
        }

        public BetaJoinResult(JoinResult joinResult, String message) {
            this.joinResult = joinResult;
            this.message = message;
        }

        public JoinResult getJoinResult() {
            return joinResult;
        }

        public String getMessage() {
            return message;
        }

        public enum JoinResult {
            ALREADY_JOINED, MINECRAFT_AUTH_FAIL, JOIN_FAILED, JOINED
        }
    }
}
