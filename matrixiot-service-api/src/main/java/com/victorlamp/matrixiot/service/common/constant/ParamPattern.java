package com.victorlamp.matrixiot.service.common.constant;

public interface ParamPattern {
    String NAME = "([A-Za-z0-9_\\-()\\[\\]]|[^\\x00-\\xff])*";
    String NAME_MESSAGE = "名称支持使用中文、英文、数字、符号：_-()[]{}";
}
