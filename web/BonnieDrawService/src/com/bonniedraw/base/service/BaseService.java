package com.bonniedraw.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class BaseService {
	
	@Autowired
	public DataSourceTransactionManager transactionManager;
	
	public void callRollBack(){
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
	
}
