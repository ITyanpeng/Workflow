package cn.sccl.common.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.sccl.common.util.StringUtil;

@SuppressWarnings("serial")
public class ColumnInfo
{
	private String								name;
	private String								type;
	private int									size;
	private int									digits;
	private boolean								nullable;
	private final WordsParser					wordsParser;
	private String								columnExplain;//列注释
	/**
	 * 设计要求默认列
	 */
	public static final Map<String, FieldInfo>	DEFAULT_COLUMNS	= new HashMap<String, FieldInfo>()
																{
																};
	/**
	 * 设计要求默认属性
	 */
	public static final Map<String, FieldInfo>	DEFAULT_FIELDS	= new HashMap<String, FieldInfo>()
																{
																};
	/**
	 * 默认分区标识
	 */
	public static final String					PARTITION_FIELD	= "provinceCode";
	
	static
	{
		FieldInfo createTime = new FieldInfo("CREATE_TIME", "TIMESTAMP", "systimestamp", "createTime",
				FieldInfo.OpType.INSERT, "timestamp");
		FieldInfo updateTime = new FieldInfo("CREATE_TIME", "TIMESTAMP", "systimestamp", "updateTime",
				FieldInfo.OpType.UPDATE, "timestamp");
		FieldInfo deleteFlag = new FieldInfo("DELETE_FLAG", "CHAR", "'0'", "deleteFlag", FieldInfo.OpType.DELETE,
				"deleteFlag");
		//年份非必填
		FieldInfo year = new FieldInfo("YEAR", "NUMBER", "to_char(sysdate,'yyyy')", "year",
				FieldInfo.OpType.INSERT, "int", false);
		//省份非必填
		FieldInfo provinceCode = new FieldInfo("PROVINCE_CODE", "VARCHAR2", null, "provinceCode",
				FieldInfo.OpType.INSERT, "String", false);
		
		DEFAULT_COLUMNS.put("CREATE_TIME", createTime);
		DEFAULT_COLUMNS.put("UPDATE_TIME", updateTime);
		DEFAULT_COLUMNS.put("DELETE_FLAG", deleteFlag);
		DEFAULT_COLUMNS.put("YEAR", year);
		DEFAULT_COLUMNS.put("PROVINCE_CODE", provinceCode);
		
		DEFAULT_FIELDS.put("createTime", createTime);
		DEFAULT_FIELDS.put("updateTime", updateTime);
		DEFAULT_FIELDS.put("deleteFlag", deleteFlag);
		DEFAULT_FIELDS.put("year", year);
		DEFAULT_FIELDS.put("provinceCode", provinceCode);
	}
	
	
	public ColumnInfo(String name, String type, int size, int decimalDigits, int nullable,String columnExplain)
	{
		this.name = name;
		this.type = type;
		this.size = size;
		this.digits = decimalDigits;
		if (nullable == 1)
			this.nullable = true;
		if (StringUtil.isUpperCase(name) || name.contains(StringUtil.UNDER_LINE))
		{
			this.wordsParser = new UnderlineSplitWordsParser();
		}
		else
		{
			this.wordsParser = new UncapitalizeWordsParser();
		}
		
		this.columnExplain=columnExplain;
	}
	

	public String getName()
	{
		return name;
	}
	

	public void setName(String name)
	{
		this.name = name;
	}
	

	public String getType()
	{
		return type;
	}
	

	public void setType(String type)
	{
		this.type = type;
	}
	

	public int getSize()
	{
		return size;
	}
	

	public void setSize(int size)
	{
		this.size = size;
	}
	

	public int getDigits()
	{
		return digits;
	}
	

	public void setDigits(int digits)
	{
		this.digits = digits;
	}
	

	public boolean isNullable()
	{
		return nullable;
	}
	

	public void setNullable(boolean nullable)
	{
		this.nullable = nullable;
	}
	

	public String parseJavaType()
	{
		return parseJavaType(getType(), getDigits()).getName();
	}
	

	public String parseJdbcType()
	{
		String javaType = parseJavaType();
		String result = "NUMERIC";
		if (javaType.endsWith("String"))
		{
			result = "VARCHAR";
		}
		// 加入timestamp
		if (javaType.endsWith("Timestamp"))
		{
			result = null;
		}
		
		if (javaType.endsWith("Date"))
		{
			result = null;
		}
		return result;
	}
	

	public String parseFieldName()
	{
		return wordsParser.parseWords(name);
	}
	

	@Override
	public String toString()
	{
		return name + " " + type + " " + size + " " + digits + " " + nullable;
	}
	

	/**
	 * 获取列的java类型
	 * 
	 * @param jdbcType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Class parseJavaType(String jdbcType, int digit)
	{
		// String result = "int";
		jdbcType = StringUtils.upperCase(jdbcType);
		Class result = Long.class;
		if (jdbcType.contains("CHAR"))
		{
			result = String.class;
		}
		if (jdbcType.contains("CLOB"))
		{
			result = String.class;
		}
		if (jdbcType.contains("DATE"))
		{
			result = java.util.Date.class;
		}
		if (jdbcType.contains("TIMESTAMP"))
		{
			result = java.sql.Timestamp.class;
		}
		if (jdbcType.contains("NUMBER") && digit > 0)
		{// 有小数精度
			result = java.math.BigDecimal.class;
		}
		return result;
	}
	

	/**
	 * 列名转换成java 字段名
	 * 
	 * @param jdbcName
	 * @return
	 */
	public static String parseFieldName(String jdbcName)
	{
		WordsParser wordsParser = null;
		if (jdbcName.contains(StringUtil.UNDER_LINE))
		{
			wordsParser = new UnderlineSplitWordsParser();
		}
		else
		{
			wordsParser = new UncapitalizeWordsParser();
		}
		return wordsParser.parseWords(jdbcName);
	}


	/**
	 * @return the columnExplain
	 */
	public String getColumnExplain()
	{
		return columnExplain;
	}


	/**
	 * @param columnExplain the columnExplain to set
	 */
	public void setColumnExplain(String columnExplain)
	{
		this.columnExplain = columnExplain;
	}
}
