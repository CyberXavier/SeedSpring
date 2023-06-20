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

    public TransactionManager getTxManager() {
        return transactionManager;
    }

    public void setTxManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
}
