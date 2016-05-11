package cn.sccl.common.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;



public class ConfigPropertiesUtil
{
	private static PropertiesConfiguration	propertiesConfig;
	
	private static PropertiesConfiguration getConfig()
	{
		if (propertiesConfig !=null )return propertiesConfig;
		propertiesConfig=(PropertiesConfiguration) ContextUtil.getBean("configUtil");
		return propertiesConfig ;
	}
	
	public static String getString(String propertyKey)
	{
		AbstractConfiguration.setDefaultListDelimiter('~');
		//getConfig().reload();
		try {
			propertiesConfig= new PropertiesConfiguration(getConfig().getFileName());
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return propertiesConfig.getString(propertyKey);
	}
	

	public static String getString(String propertyKey, String defaultValue)
	{
		return getConfig().getString(propertyKey, defaultValue);
	}
	

	public static int getInt(String propertyKey)
	{
		return getConfig().getInt(propertyKey);
	}
	

	public static int getInt(String key, int defaultValue)
	{
		return getConfig().getInt(key, defaultValue);
	}
	

	public static float getFloat(String propertyKey)
	{
		return getConfig().getFloat(propertyKey);
	}
	

	public static float getFloat(String propertyKey, float defaultValue)
	{
		return getConfig().getFloat(propertyKey, defaultValue);
	}
	

	public static boolean getBoolean(String propertyKey)
	{
		return getConfig().getBoolean(propertyKey);
	}
	

	public static boolean getBoolean(String propertyKey, boolean defualtValue)
	{
		return getConfig().getBoolean(propertyKey, defualtValue);
	}
	

	public static String[] getStringArray(String propertyKey)
	{
		return getConfig().getStringArray(propertyKey);
	}
	

	public static List<String> getStringList(String propertyKey)
	{
		List<String> list = new ArrayList<String>();
		String[] strArr = getStringArray(propertyKey);
		for (String value : strArr)
		{
			list.add(value);
		}
		return list;
	}
	

	public static List getList(String propertyKey)
	{
		return getConfig().getList(propertyKey);
	}
	
}


