package com.minis.web;

import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class XmlConfigReader {
    public XmlConfigReader(){}

    public Map<String, MappingValue> loadConfig(Resource res){
        Map<String, MappingValue> mappings = new HashMap<String, MappingValue>();
        while (res.hasNext()) {
            Element element = (Element) res.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            String beanMethod = element.attributeValue("value");
            mappings.put(beanID, new MappingValue(beanID, beanClassName, beanMethod));
        }
        return mappings;
    }

}
