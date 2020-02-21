package moe.plushie.armourers_workshop.common.library.global.task;

import java.net.URLEncoder;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.common.library.global.auth.PlushieAuth;
import moe.plushie.armourers_workshop.common.library.global.auth.PlushieSession;
import moe.plushie.armourers_workshop.common.library.global.permission.PermissionSystem.PlushieAction;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskBetaJoin.BetaJoinResult.JoinResult;
import net.minecraft.client.Minecraft;

public class GlobalTaskBetaJoin extends GlobalTask<BetaJoinResult> {

    private static final String URL_CHECK = "beta-code-check.php?uuid=%s";
    private static final String URL_JOIN = "beta-join.php?username=%s&uuid=%s&serverId=&betaCode=%s";

    private final UUID betaCode;

    public GlobalTaskBetaJoin(UUID betaCode) {
        super(PlushieAction.BETA_JOIN, true);
        this.betaCode = betaCode;
    }

    @Override
    public BetaJoinResult call() throws Exception {
        permissionCheck();
        PlushieSession session = PlushieAuth.PLUSHIE_SESSION;
        if (session.hasServerId()) {
            return new BetaJoinResult(JoinResult.ALREADY_JOINED);
        }
        
        String code = URLEncoder.encode(betaCode.toString(), "UTF-8");
        
        try {
            String urlCheck = String.format(getBaseUrl() + URL_CHECK, code);
            JsonObject jsonCheckResult = new JsonParser().parse(downloadString(urlCheck)).getAsJsonObject();
            if (jsonCheckResult.has("action") & jsonCheckResult.has("valid")) {
                String action = jsonCheckResult.get("action").getAsString();
                boolean valid = jsonCheckResult.get("valid").getAsBoolean();
                if (action.equals("beta-code-check")) {
                    if (!valid) {
                        return new BetaJoinResult(JoinResult.CODE_INVALID);
                    }
                } else {
                    return new BetaJoinResult(JoinResult.CODE_CHECK_FAILED);
                }
            } else {
                return new BetaJoinResult(JoinResult.CODE_CHECK_FAILED);
            }
            
        } catch (Exception e) {
            return new BetaJoinResult(JoinResult.CODE_CHECK_FAILED);
        }

        GameProfile gameProfile = Minecraft.getMinecraft().player.getGameProfile();
        String username = URLEncoder.encode(gameProfile.getName(), "UTF-8");
        String uuid = URLEncoder.encode(gameProfile.getId().toString(), "UTF-8");
        String urlJoin = String.format(getBaseUrl() + URL_JOIN, username, uuid, session.getAccessToken(), code);

        JsonObject jsonJoinResult = new JsonParser().parse(downloadString(urlJoin)).getAsJsonObject();
        
        if (jsonJoinResult.has("action") & jsonJoinResult.has("valid")) {
            String action = jsonJoinResult.get("action").getAsString();
            boolean valid = jsonJoinResult.get("valid").getAsBoolean();
            if (action.equals("beta-join")) {
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

    private boolean checkCode() throws Exception {
        String urlCheck = String.format(getBaseUrl() + URL_CHECK, URLEncoder.encode(betaCode.toString(), "UTF-8"));
        JsonObject checkJson = new JsonParser().parse(downloadString(urlCheck)).getAsJsonObject();
        if (checkJson.has("action") & checkJson.has("valid")) {
            String action = checkJson.get("action").getAsString();
            boolean valid = checkJson.get("valid").getAsBoolean();
            if (action.equals("beta-code-check")) {
                if (valid) {
                    return true;
                }
            }
        }
        return false;
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
            ALREADY_JOINED,
            CODE_CHECK_FAILED,
            CODE_INVALID,
            JOIN_FAILED,
            JOINED
        }
    }
}
