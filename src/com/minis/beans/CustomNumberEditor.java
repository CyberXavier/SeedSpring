package com.minis.beans;

import com.minis.utils.NumberUtils;
import com.minis.utils.StringUtils;

import java.text.NumberFormat;

public class CustomNumberEditor implements PropertyEditor{

    private Class<? extends Number> numberClass; //数据类型
    private NumberFormat numberFormat; //指定格式
    private boolean allowEmpty;
    private Object value;

    public CustomNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty){
        this(numberClass, null, allowEmpty);
    }

    public CustomNumberEditor(Class<? extends Number> numberClass, NumberFormat numberFormat,
                              boolean allowEmpty) throws IllegalArgumentException{
        this.numberClass = numberClass;
        this.numberFormat = numberFormat;
        this.allowEmpty = allowEmpty;
    }

    // 将一个字符串转换成number赋值
    @Override
    public void setAsText(String text) {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            // 空 String 赋值 null
            setValue(null);
        }
        else if (this.numberFormat != null){
            // 根据特定格式解析text
            setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
        }
        else {
            setValue(NumberUtils.parseNumber(text, this.numberClass));
        }
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            this.value = (NumberUtils.convertNumberToTargetClass((Number) value, this.numberClass));
        }
        else {
            this.value = value;
        }
    }

    @Override
    public Object getValue() {
        return this.value;
    }
    //将number表示成格式化串
    @Override
    public Object getAsText() {
        Object value = this.value;
        if (value == null) {
            return "";
        }
        if (this.numberFormat != null) {
            // 给定格式.
            return this.numberFormat.format(value);
        }
        else {
            return value.toString();
        }
    }
}
