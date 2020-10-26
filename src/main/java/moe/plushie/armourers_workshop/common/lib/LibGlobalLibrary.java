package moe.plushie.armourers_workshop.common.lib;

public class LibGlobalLibrary {

    public static final String URL_BASE = "plushie.moe/armourers_workshop/";
    public static final String URL_BASE_NORMAL = "http://" + URL_BASE;
    public static final String URL_BASE_SECURE = "https://" + URL_BASE;

    public static final String BETA_CHECK = "beta-check.php?uuid=%s";
    public static final String BETA_CODE_CHECK = "beta-code-check.php?code=%s";
    public static final String BETA_JOIN = "beta-join.php?username=%s&uuid=%s&serverId=%s&betaCode=%s";

    public static final String MOST_DOWNLOADED = "most-downloaded.php?limit=%d&maxFileVersion=%d";
    public static final String MOST_LIKED = "most-liked.php?limit=%d&maxFileVersion=%d";
    public static final String NEED_RATED = "need-rated.php?limit=%d&maxFileVersion=%d";
    public static final String RECENTLY_UPLOADED = "recently-uploaded.php?limit=%d&maxFileVersion=%d";
    public static final String GET_SKIN_REPORTS = "mod-get-skin-reports.php";
    public static final String GET_SKIN_INFO = "";
    public static final String STATS = "stats.php";
    public static final String DELETE_SKIN = "user-skin-delete.php?userId=%d&accessToken=%s&skinId=%d";
    public static final String EDIT_SKIN = "user-skin-edit.php?userId=%d&accessToken=%s&skinId=%d";
    public static final String USER_SKINS = "user-skins-page.php?userId=%d&maxFileVersion=%d&pageIndex=%d&pageSize=%d";
    public static final String REPORT_SKIN = "user-skin-report.php";
    public static final String SKIN_SEARCH = "skin-search-page.php?search=%s&maxFileVersion=%d&pageIndex=%d&pageSize=%d";
    public static final String USER_INFO = "user-info.php?userId=%d";
    public static final String SKIN_RATE = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d&rating=%d";
    public static final String SKIN_HAVE_RATED = "user-skin-action.php?userId=%d&accessToken=%s&action=%s&skinId=%d";
}
