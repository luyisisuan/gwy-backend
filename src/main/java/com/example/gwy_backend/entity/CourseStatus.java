package com.example.gwy_backend.entity;

public enum CourseStatus {
    NOT_STARTED("未开始"),
    IN_PROGRESS("进行中"),
    COMPLETED("已完成"),
    ON_HOLD("已搁置");

    private final String displayName; // 用于界面显示中文名称

    CourseStatus(String displayName) {
        this.displayName = displayName;
    }

    // 提供一个获取显示名称的方法
    public String getDisplayName() {
        return displayName;
    }

    // 如果前端需要传递中文名，可以添加一个根据中文名查找枚举的方法，但不推荐
    // public static CourseStatus fromDisplayName(String displayName) { ... }
}