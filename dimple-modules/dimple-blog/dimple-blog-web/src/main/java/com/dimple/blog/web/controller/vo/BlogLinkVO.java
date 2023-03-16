package com.dimple.blog.web.controller.vo;

import lombok.Data;

/**
 * BlogLinkVO
 *
 * @author Dimple
 * @date 3/10/2023 11:26 AM
 */
@Data
public class BlogLinkVO {
    private Long id;
    private String title;
    private String headerImage;
    private String description;
    private String url;
    private Integer linkStatus;
    private Long visitCount;
    private String email;
}