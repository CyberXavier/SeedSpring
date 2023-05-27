package src.com.minis.beans;

import src.com.minis.core.Resource;
import org.dom4j.Element;

/**
 * 将解析好的 XML 转换成需要的 BeanDefinition 并注册到 BeanFactory
 */
public class XmlBeanDefinitionReader {
    BeanFactory beanFactory;
    public XmlBeanDefinitionReader(BeanFactory beanFactory){
        this.beanFactory = beanFactory;
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
            this.beanFactory.registerBeanDefinition(beanDefinition);
        }
    }
}
