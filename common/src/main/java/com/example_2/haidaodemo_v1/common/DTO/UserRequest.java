package com.example_2.haidaodemo_v1.common.DTO;

import lombok.Data;

/**
 * 🛰️ 用来接受UserController的请求
 * <hr/>
 * 🧩 职责：统一请求接口
 * 🛡️ 关联：
 * 🗺️ 架构： (haidaoDemo_v1)
 *
 * @author Sisfuzues
 * @date 2026/3/15 23:39
 */
public class UserRequest {

    @Data
    public static class UpdatePassword {
        private String oldPassword;
        private String newPassword;
    }

    @Data
    public static class UpdateProfile {
        private String nickname;
    }
}
