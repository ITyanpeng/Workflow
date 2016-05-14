package cn.sccl.common.util;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseTestCase extends TestCase
{
	
	protected ApplicationContext	ctx;
	
	
	public BaseTestCase()
	{
		super();
	}
	

	public BaseTestCase(String methodName)
	{
		super(methodName);
	}
	

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ctx = new ClassPathXmlApplicationContext("classpath*:main-context.xml");
	}
	
}
