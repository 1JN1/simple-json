package com.study;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description
 **/

@Data
public class User {

    private int id;
    private String name;
    private boolean isActive;
    private LocalDateTime lastLogin;
}
