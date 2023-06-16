package com.minis.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DefaultObjectMapper implements ObjectMapper{

    String dateFormat = "yyyy-MM-dd";

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);

    String decimalFormat = "#,##0.00";

    DecimalFormat decimalFormatter = new DecimalFormat(decimalFormat);

    public DefaultObjectMapper(){}

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    @Override
    public void setDecimalFormat(String decimalFormat) {
        this.decimalFormat = decimalFormat;
        this.decimalFormatter = new DecimalFormat(decimalFormat);
    }

    @Override
    public String writeValuesAsString(Object obj) {
        String sJsonStr = "{";
        if (obj == null) return "{}";
        Class<?> clz = obj.getClass();

        Field[] fields = clz.getDeclaredFields();
        // 对返回对象种的每一个属性进行格式转换
        for (Field field : fields) {
            String sField = "";
            Object value = null;
            Class<?> type = null;
            String name = field.getName();
            String strValue = "";
            field.setAccessible(true);
            try {
                value = field.get(obj); //取到obj对象field属性值
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            type = field.getType();

            // 针对不同数据类型进行格式转化
            if (value == null) {
                strValue = "";
            }
            else if (value instanceof Date) {
                Date tempValue = (Date) value;
                Timestamp timestamp = new Timestamp(tempValue.getTime());
                LocalDate localDate = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                LocalDate localDate = ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                strValue = localDate.format(this.dateTimeFormatter);
                strValue = localDate.format(this.dateTimeFormatter);
            }
            else if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                strValue = this.decimalFormatter.format(value);
            }
            else { // String 类型
                strValue = value.toString();
            }

            // 拼接JSON字符串
            if (sJsonStr.equals("{")) {
                sField = "\"" + name + "\":\"" + strValue + "\"";
            }
            else {
                sField = ",\"" + name + "\":\"" + strValue + "\"";
            }

            sJsonStr += sField;
        }

        // 将最后一个有括号拼接上
        sJsonStr += "}";
        return sJsonStr;
    }
}
