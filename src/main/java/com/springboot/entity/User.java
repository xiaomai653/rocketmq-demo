package com.springboot.entity;

import lombok.Data;

/**
 * 用户实体
 *
 * @author 麦
 * @date 2023/9/19 11:16
 */
@Data
public class User {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

}
