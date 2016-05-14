package cn.sccl.common.tool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.sccl.common.util.DevLog;
import cn.sccl.common.util.StringUtil;

public class ScaffoldGen
{
	
	private static final String	NULLABLE			= "NULLABLE";
	private static final String	DECIMAL_DIGITS		= "DECIMAL_DIGITS";
	private static final String	COLUMN_SIZE			= "COLUMN_SIZE";
	private static final String	TYPE_NAME			= "TYPE_NAME";
	private static final String	SQLSERVER			= "sqlserver";
	private static final String	COLUMN_NAME			= "COLUMN_NAME";
	private static final String	JDBC_PASSWORD		= "jdbc.password";
	private static final String	JDBC_USER			= "jdbc.user";
	private static final String	JDBC_URL			= "jdbc.url";
	private static final String	JDBC_DRIVER			= "jdbc.driver";
	private static final String	JDBC_SCHEMA			= "jdbc.schema";
	private static final String	CONFIG_PROPERTIES	= "if_config.properties";
	private final Log			log					= LogFactory.getLog(getClass());
	private Connection			conn;
	private String				schema;
	private DatabaseMetaData	metaData;
	private final String		pkgName;
	private final String		clzName;
	private final String		tblName;
	private Map<String,String>	columnExplains;
	
	
	/**
	 * @param pkgName
	 *            包路径
	 * @param clzName
	 *            model 名称
	 */
	public ScaffoldGen(String pkgName, String clzName)
	{
		this(pkgName, clzName, clzName + "s");
	}
	

	/**
	 * @param pkgName
	 *            包路径
	 * @param clzName
	 *            model 名称
	 * @param tblName
	 *            表名
	 */
	public ScaffoldGen(String pkgName, String clzName, String tblName)
	{
		this.pkgName = pkgName;
		this.clzName = StringUtils.capitalize(clzName);
		this.tblName = tblName.toUpperCase();
	}
	

	public void execute() throws Exception
	{
		execute(false);
	}
	

	public void execute(boolean debug) throws Exception
	{
		// 初始化数据库信息
		if (!initConnection())
		{
			DevLog.debug("数据库连接设备,请检查ClassPath路径下的配置文件" + CONFIG_PROPERTIES);
			return;
		}
		// 初始化表信息
		TableInfo tableInfo = parseDbTable(this.tblName);
		if (tableInfo == null)
		{
			return;
		}
		// 生成全套文件
		ScaffoldBuilder sf = new ScaffoldBuilder(this.pkgName, this.clzName, tableInfo);
		List<FileGenerator> list = sf.buildGenerators();
		for (FileGenerator gen : list)
		{
			gen.execute(debug);
		}
	}
	

	/**
	 * 初始化数据库连接环境
	 * 
	 * @return
	 */
	private boolean initConnection()
	{
		Configuration config;
		String driver = null;
		String url = StringUtils.EMPTY;
		String user = StringUtils.EMPTY;
		String password = StringUtils.EMPTY;
		String schema = StringUtils.EMPTY;
		
		try
		{
			config = new PropertiesConfiguration(CONFIG_PROPERTIES);
			driver = config.getString(JDBC_DRIVER);
			url = config.getString(JDBC_URL);
			user = config.getString(JDBC_USER);
			password = config.getString(JDBC_PASSWORD);
			schema = config.getString(JDBC_SCHEMA);
			if (StringUtil.isNotBlank(schema))
			{
				this.schema = schema;
			}
			if (driver.toLowerCase().contains(SQLSERVER))
			{
				this.schema = "dbo";
			}
			if (StringUtil.isBlank(this.schema))
			{
				this.schema = user.toUpperCase();
			}
			Class.forName(driver);
		}
		catch (ConfigurationException e1)
		{
			e1.printStackTrace();
			log.fatal("Jdbc connection config file not found - " + CONFIG_PROPERTIES);
			return false;
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			log.fatal("Jdbc driver not found - " + driver);
			return false;
		}
		
		try
		{
			conn = DriverManager.getConnection(url, user, password);
			if (conn == null)
			{
				log.fatal("Database connection is null");
				return false;
			}
			// 数据元数据
			metaData = conn.getMetaData();
			if (metaData == null)
			{
				log.fatal("Database MetaData is null");
				return false;
			}
			
		}
		catch (SQLException e)
		{
			log.fatal("Database connect failed");
			e.printStackTrace();
		}
		return true;
	}
	

	/**
	 * 初始化表信息
	 * 
	 * @param TABLE_NAME
	 * @return
	 * @throws Exception
	 */
	private TableInfo parseDbTable(String TABLE_NAME) throws Exception
	{
		TableInfo tableInfo = new TableInfo(TABLE_NAME);
		columnExplains=new HashMap<String,String>();
		ResultSet rs = null;
		log.trace("parseDbTable begin");
		try
		{
			// 获取表的主键
			rs = metaData.getPrimaryKeys(null, schema, TABLE_NAME);
			if (rs.next())
			{
				tableInfo.setPrimaryKey(rs.getString(COLUMN_NAME));
			}
			if (rs.next())
			{
				DevLog.debug("该表为复合主键，不适用于代码脚手架生成工具");
				return null;
			}
		}
		catch (SQLException e)
		{
			log.error("Table " + TABLE_NAME + " parse error.");
			e.printStackTrace();
			return null;
		}
		log.info("PrimaryKey : " + tableInfo.getPrimaryKey());
		
		try
		{
			// 获取表的元信息
			rs = metaData.getColumns(conn.getCatalog(), schema, TABLE_NAME, null);
			if (!rs.next())
			{
				log.fatal("Table " + schema + "." + TABLE_NAME + " not found.");
				return null;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		//获取表列的注释comments
		try{
			String sql=" SELECT T.TABLE_NAME,T.COLUMN_NAME,T.COMMENTS FROM USER_COL_COMMENTS  T WHERE T.TABLE_NAME=upper('"+TABLE_NAME+"')  ";
			PreparedStatement ps=conn.prepareStatement(sql);
			ResultSet rs1=ps.executeQuery();
			while(rs1.next()){
				columnExplains.put(rs1.getString("COLUMN_NAME"), rs1.getString("COMMENTS"));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		try
		{
			while (rs.next())
			{
				
				String columnName = rs.getString(COLUMN_NAME);
				String columnType = rs.getString(TYPE_NAME);
				int datasize = rs.getInt(COLUMN_SIZE);
				int digits = rs.getInt(DECIMAL_DIGITS);
				int nullable = rs.getInt(NULLABLE);
				// DevLog.debug(rs.getString("REMARKS"));
				
				String columnExplain=columnExplains.get(columnName);
				// 初始化字段信息
				ColumnInfo colInfo = new ColumnInfo(columnName, columnType, datasize, digits, nullable,columnExplain);
				
				log.info("DB column : " + colInfo);
				log.info("Java field : " + colInfo.parseFieldName() + " / " + colInfo.parseJavaType()+" / "+colInfo.getColumnExplain());
				tableInfo.addColumn(colInfo);
				if (ColumnInfo.DEFAULT_COLUMNS.containsKey(columnName))
				{
					FieldInfo field = ColumnInfo.DEFAULT_COLUMNS.get(columnName);
					if (!columnType.contains(field.getColumnType()))
					{
						throw new Exception("列[" + columnName + "]类型为：" + columnType + "，与设计要求的["
								+ ColumnInfo.DEFAULT_COLUMNS.get(columnName).getColumnType() + "]不匹配");
					}
					ColumnInfo.DEFAULT_COLUMNS.remove(columnName);
				}
			}
			// 校验表中必填字段是否存在（排除年份、省份编码字段）
			ColumnInfo.DEFAULT_COLUMNS.remove("YEAR");
			ColumnInfo.DEFAULT_COLUMNS.remove("PROVINCE_CODE");
			if (!ColumnInfo.DEFAULT_COLUMNS.isEmpty())
			{
				String filed = "设计要求的以下列缺失：";
				for (Map.Entry<String, FieldInfo> entry : ColumnInfo.DEFAULT_COLUMNS.entrySet())
				{
					filed = filed + "[" + entry.getKey() + ":" + entry.getValue().getColumnType() + "] ";
				}
				throw new Exception(filed);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("Table " + TABLE_NAME + " parse error.");
		}
		log.trace("parseDbTable end");
		return tableInfo;
	}
	
}
