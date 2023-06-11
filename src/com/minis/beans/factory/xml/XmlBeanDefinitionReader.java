package com.minis.beans.factory.xml;

import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * 将解析好的 XML 转换成需要的 BeanDefinition 并注册到 BeanFactory
 */
public class XmlBeanDefinitionReader {
    AbstractBeanFactory bf;
    public XmlBeanDefinitionReader(AbstractBeanFactory bf){
        this.bf = bf;
    }

    /**
     * 把解析的 XML 内容转换成 BeanDefinition，并加载到 BeanFactory 中
     * @param resource
     */
    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);

            //handle constructor
            List<Element> constructorElements = element.elements("constructor-arg");
            ConstructorArgumentValues AVS = new ConstructorArgumentValues();
            for (Element e : constructorElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                AVS.addArgumentValue(new ConstructorArgumentValue(pType, pName, pValue));
            }
            beanDefinition.setConstructorArgumentValues(AVS);
            //end of handle constructor

            // handle properties
            List<Element> propertyElements = element.elements("property");
            PropertyValues PVS = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue("type");
                String pName = e.attributeValue("name");
                String pValue = e.attributeValue("value");
                String pRef = e.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                }else if (pRef != null && !pRef.equals("")) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(PVS);
            //end of handle properties

            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);
            // TODO 等到所有的BeanDefinition都加载好了再调用register方法，否则循环依赖时geanBean会报错，
            //  因为此时依赖的beanDefinition还不存在！
            this.bf.registerBeanDefinition(beanID,beanDefinition);
        }
    }
}
