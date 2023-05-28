package src.com.minis.beans;

public class PropertyValue{
    //权限限制为只能在本类中被访问或调用,并且在运行时不允许修改其值
    private final String type;
    private final String name;
    private final Object value;

    public PropertyValue(String type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

}

