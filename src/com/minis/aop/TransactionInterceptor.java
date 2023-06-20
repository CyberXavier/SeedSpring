package com.minis.aop;

public class TransactionInterceptor implements MethodInterceptor{

    TransactionManager transactionManager;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        transactionManager.doBegin();
        Object ret = invocation.proceed();
        transactionManager.doCommit();
        return null;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
