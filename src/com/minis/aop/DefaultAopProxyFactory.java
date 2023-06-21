package com.minis.aop;

public class DefaultAopProxyFactory implements AopProxyFactory {

	@Override
	public AopProxy createAopProxy(Object target, PointcutAdvisor advisor) {
		// 检查一个类是否是接口或者是代理类
		//if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
			return new JdkDynamicAopProxy(target, advisor);
		//}
		//return new CglibAopProxy(config);
	}
}
