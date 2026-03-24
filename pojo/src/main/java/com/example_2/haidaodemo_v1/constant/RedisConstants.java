package com.example_2.haidaodemo_v1.constant;

public class RedisConstants {
    public final static String LOGIN_CODE_KEY = "login:code:";
    public final static String LOGIN_LOCK_KEY = "login:lock:";
    public final static long LOGIN_CODE_TTL = 300L;
    public final static long LOGIN_LOCK_TTL = 60L;

    public final static String POST_LIKE_KEY = "post:like:";
    public final static String POST_LIKE_CHANGE_KEY = "post:change";
    public final static String COMMENT_LIKE_KEY = "comment:like:";
    public final static String COMMENT_LIKE_CHANGE_KEY = "comment:change";

    public final static String CHAT_RECENT_KEY = "chat:recent:";
    public final static String CHAT_SESSION_KEY = "chat:session:";
    public final static String CHAT_UNREAD_KEY = "chat:unread:";

    public final static String USER_CACHE_KEY = "user:cache:";
    public final static long USER_CACHE_TIME = 60L;

    public final static String CHANGE_USERNAME_KEY = "change:username:";
    public final static long CHANGE_USERNAME_TIME = 15L;
}
