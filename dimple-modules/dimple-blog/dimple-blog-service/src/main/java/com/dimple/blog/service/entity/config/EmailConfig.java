package com.dimple.blog.service.entity.config;

import lombok.Data;

/**
 * EmailConfig
 *
 * @author Dimple
 * @date 3/30/2023
 */
@Data
public class EmailConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
}
