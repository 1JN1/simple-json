package com.study.json.pojo;

import com.study.json.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 王文涛
 * @date 2025/4/3
 * @description json token
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private TokenType type;

    private String value;

}
