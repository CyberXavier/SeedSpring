package src.com.minis.context;

import src.com.minis.beans.BeanFactory;
import src.com.minis.beans.BeansException;
import src.com.minis.beans.SimpleBeanFactory;
import src.com.minis.beans.XmlBeanDefinitionReader;
import src.com.minis.core.ClassPathXmlResource;

public class ClassPathXmlApplicationContext implements BeanFactory,ApplicationEventPublisher{
    SimpleBeanFactory beanFactory;
    //context负责整合容器的启动过程，读外部配置，解析Bean定义，创建BeanFactory
    public ClassPathXmlApplicationContext(String fileName){
        // 解析 XML 文件中的内容
        ClassPathXmlResource resource = new ClassPathXmlResource(fileName);
        SimpleBeanFactory bf = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(bf);
        // 加载解析的内容，构建 BeanDefinition
        reader.loadBeanDefinitions(resource);
        this.beanFactory = bf;
    }

    /**
     * context再对外提供一个getBean，底下就是调用的BeanFactory对应的方法。
     * 读取 BeanDefinition 的配置信息，实例化 Bean，然后把它注入到 BeanFactory 容器中后返回
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object getBean(String beanName) throws BeansException{
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanFactory.containsBean(name);
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        this.beanFactory.registerBean(beanName, obj);
    }

    @Override
    public void publishEvent(ApplicationEvent event) {

    }

    @Override
    public boolean isSingleton(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPrototype(String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Class<?> getType(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}
