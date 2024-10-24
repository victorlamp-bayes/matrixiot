package com.victorlamp.matrixiot.service.rule.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;

/**
 * TCA 模型里的事件类
 *
 * @author: Dylan
 * @date: 2023/8/31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Action {

    /**
     * uri
     */
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\-/]+$")
    private String uri;

    /**
     * 参数
     */
    @NotNull
    private HashMap params;
}
