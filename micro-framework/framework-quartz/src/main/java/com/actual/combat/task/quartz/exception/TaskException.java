package com.actual.combat.task.quartz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskException extends Exception {
    private int code;
    private String message;

    public TaskException(String message) {
        super(message);
        this.code = 500;
    }
}
