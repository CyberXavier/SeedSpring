package com.minis.beans;

/**
 * 提供一些方法可以让字符串和 Obejct 之间进行双向灵活转换
 */
public interface PropertyEditor {
    void setAsText(String text);
    void setValue(Object value);
    Object getValue();
    Object getAsText();
}
