package cn.sccl.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DevLog
{
	private final static Log	log	= LogFactory.getLog(DevLog.class);
	
	
	private DevLog()
	{
		// nothing
	}
	

	public static void trace(Object o)
	{
		if (log.isTraceEnabled())
		{
			log.trace(o);
		}
	}
	

	public static void trace(Object o, Throwable t)
	{
		if (log.isTraceEnabled())
		{
			log.trace(o, t);
		}
	}
	

	public static void debug(Object o)
	{
		if (log.isDebugEnabled())
		{
			log.debug(o);
		}
	}
	

	public static void debug(Object o, Throwable t)
	{
		if (log.isDebugEnabled())
		{
			log.debug(o, t);
		}
	}
	

	public static void info(Object o)
	{
		if (log.isInfoEnabled())
		{
			log.info(o);
		}
	}
	

	public static void info(Object o, Throwable t)
	{
		if (log.isInfoEnabled())
		{
			log.info(o, t);
		}
	}
	

	public static void warn(Object o)
	{
		if (log.isWarnEnabled())
		{
			log.warn(o);
		}
	}
	

	public static void warn(Object o, Throwable t)
	{
		if (log.isWarnEnabled())
		{
			log.warn(o, t);
		}
	}
	

	public static void error(Object o)
	{
		if (log.isErrorEnabled())
		{
			log.error(o);
		}
	}
	

	public static void error(Object o, Throwable t)
	{
		if (log.isErrorEnabled())
		{
			log.error(o, t);
		}
	}
	

	public static void fatal(Object o)
	{
		if (log.isFatalEnabled())
		{
			log.fatal(o);
		}
	}
	

	public static void fatal(Object o, Throwable t)
	{
		if (log.isFatalEnabled())
		{
			log.fatal(o, t);
		}
	}
	
	/**
	 * 
	* @Title: traceException
	* @Description: V20151211-raoifeng@20151124 工具方法，记录日志
	* @Return: void
	* @Throws: 
	* @Author: raoifeng
	* @Date: 2015-11-24 下午3:25:53
	 */
	public static void traceException(Exception ex){
		StackTraceElement[] stackTraceElements = ex.getStackTrace();
		StringBuffer sb=new StringBuffer();
		sb.append("logId:").append(+System.currentTimeMillis()).append("");
		for(StackTraceElement stackTraceElement:stackTraceElements){
			sb.append("File=").append( stackTraceElement.getFileName()).append(" ");
			sb.append("Class=" ).append(stackTraceElement.getClassName()).append(" ");
			sb.append("Method=" ).append(stackTraceElement.getMethodName()).append(" ");
			sb.append("Line=" ).append(stackTraceElement.getLineNumber()).append(" ;");
		}
		sb.append(" Message=").append(ex.getMessage());
		System.out.println(sb.toString());
		if (log.isErrorEnabled())
		{
			log.error(sb.toString());
		}
	}
	
}
