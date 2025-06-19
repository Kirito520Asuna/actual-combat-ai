package com.actual.combat.basic.file.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author yan
 * @Date 2025/3/13 下午11:24:34
 * @Description
 */
@Data @NoArgsConstructor
@AllArgsConstructor
public class FileException extends RuntimeException{
    private String message;
}
