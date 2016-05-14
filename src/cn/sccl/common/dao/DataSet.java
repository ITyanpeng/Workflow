package cn.sccl.common.dao;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sccl.common.tool.ColumnInfo;
import cn.sccl.common.util.DevLog;
import cn.sccl.common.util.StringUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DataSet implements Serializable
{
	
	/**
	 * 
	 */
	private static final long		serialVersionUID	= 7685857145867509064L;
	
	/**
	 * 查询返回的数据集行对象
	 */
	private ArrayList<DataRow>		Rows				= new ArrayList<DataRow>(15);
	
	/**
	 * 该数据包含的表名
	 */
	public String[]					TABLE_NAMEs			= null;
	
	/**
	 * 数据集名称
	 */
	public String					Name				= "";
	
	/**
	 * 字段名和对应的列索引
	 */
	public HashMap<String, Integer>	FieldsIndex			= new HashMap<String, Integer>();
	
	/**
	 * 所有列名
	 */
	public String[]					columnsNames		= null;
	
	/**
	 * 列类型，用从0开始的下标值获取
	 */
	public String[]					ColumnsType			= null;
	
	/**
	 * 行总数
	 */
	public long						RowCount			= 0;
	
	/**
	 * 列总数
	 */
	public int						ColumnsCount		= 0;
	
	/**
	 * 未分页的SQL语句
	 */
	public String					SQLNoPaging			= "";
	
	
	/**
	 * 用默认的DataRow
	 */
	public DataRow AddRow()
	{
		DataRow ds = new DataRow(this);
		ds.InitDataRow(null, this.ColumnsCount);
		this.Rows.add(ds);
		this.RowCount++;
		return ds;
	}
	

	public DataRow AddRow(int index)
	{
		DataRow ds = new DataRow(this);
		ds.InitDataRow(null, this.ColumnsCount);
		this.Rows.add(index, ds);
		this.RowCount++;
		return ds;
	}
	

	/**
	 * 新增行
	 * 
	 * @param dRow
	 */
	public void AddRow(DataRow dRow)
	{
		this.Rows.add(dRow);
		this.RowCount++;
	}
	

	/**
	 * 新增在指定的位置新增行
	 * 
	 * @param dRow
	 * @param iIdx
	 */
	public void AddRow(int iIdx, DataRow dRow)
	{
		this.Rows.add(iIdx, dRow);
		this.RowCount++;
	}
	

	public void Clear()
	{
		this.Rows.clear();
		this.RowCount = 0;
		this.ColumnsCount = 0;
		this.ColumnsType = null;
	}
	

	public void clearRow()
	{
		this.Rows.clear();
		this.RowCount = 0;
	}
	

	/**
	 * 
	 * @param aryColumnsName
	 */
	public void setColumnsName(String[] aryColumnsName)
	{
		this.columnsNames = aryColumnsName;
		this.ColumnsCount = aryColumnsName.length;
		FieldsIndex.clear();
		for (int iIdx = 0; iIdx < aryColumnsName.length; iIdx++)
		{
			FieldsIndex.put(aryColumnsName[iIdx], iIdx);
		}
		if (this.ColumnsType != null)
		{
			// 列类型定义
			String[] ColumnsTypeTemp = new String[this.ColumnsCount];
			System.arraycopy(this.ColumnsType, 0, ColumnsTypeTemp, 0, this.ColumnsType.length);
			this.ColumnsType = null;
			this.ColumnsType = ColumnsTypeTemp;
		}
		
		// 复制行的数据到扩展后的行数据
		for (int iRow = 0; iRow < this.Rows.size(); iRow++)
		{
			DataRow dRow = this.Row(iRow);
			Object[] objTemp = new Object[aryColumnsName.length];
			System.arraycopy(dRow.getColumnsObjects(), 0, objTemp, 0, dRow.getColumnsObjects().length);
			dRow.setColumnsObjects(objTemp);
		}
	}
	

	/**
	 * 
	 * @param aryColumnsName
	 */
	public void setColumnsName(Object[] aryColumnsName)
	{
		this.columnsNames = new String[aryColumnsName.length];
		this.ColumnsCount = aryColumnsName.length;
		FieldsIndex.clear();
		for (int iIdx = 0; iIdx < aryColumnsName.length; iIdx++)
		{
			this.columnsNames[iIdx] = (String) aryColumnsName[iIdx];
			FieldsIndex.put(this.columnsNames[iIdx], iIdx);
		}
		if (this.ColumnsType != null)
		{
			// 列类型定义
			String[] ColumnsTypeTemp = new String[this.ColumnsCount];
			System.arraycopy(this.ColumnsType, 0, ColumnsTypeTemp, 0, this.ColumnsType.length);
			this.ColumnsType = null;
			this.ColumnsType = ColumnsTypeTemp;
		}
		
		// 复制行的数据到扩展后的行数据
		for (int iRow = 0; iRow < this.Rows.size(); iRow++)
		{
			DataRow dRow = this.Row(iRow);
			Object[] objTemp = new Object[aryColumnsName.length];
			System.arraycopy(dRow.getColumnsObjects(), 0, objTemp, 0, dRow.getColumnsObjects().length);
			dRow.setColumnsObjects(objTemp);
		}
		
	}
	

	/**
	 * 获取指定行，下标从0开始
	 * 
	 * @param iRowIdx
	 *            行索引
	 * @return
	 */
	public DataRow getRow(int iRowIdx)
	{
		return this.Rows.get(iRowIdx);
	}
	

	/**
	 * 获取指定行，下标从0开始
	 * 
	 * @param iRowIdx
	 *            行索引
	 * @return 行DataRow
	 */
	public DataRow Row(int iRowIdx)
	{
		return this.Rows.get(iRowIdx);
	}
	

	/**
	 * 将查询出的数据集转换成DataSet对象
	 * 
	 * @param ds
	 * @param rs
	 */
	public static void FillDataSet(DataSet ds, ResultSet rs)
	{
		
		ResultSetMetaData rsmd = null;
		try
		{
			// 清除所内容
			ds.Clear();
			ds.FieldsIndex.clear();
			
			// 初始化查询结果中的列名
			rsmd = rs.getMetaData();
			int iColumnCount = rsmd.getColumnCount();
			
			ds.ColumnsType = new String[iColumnCount];
			ds.columnsNames = new String[iColumnCount];
			ds.ColumnsCount = iColumnCount;
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				String strFieldName = rsmd.getColumnLabel(iIdx + 1);
				ds.FieldsIndex.put(strFieldName, iIdx);
				ds.columnsNames[iIdx] = strFieldName;
				ds.ColumnsType[iIdx] = rsmd.getColumnTypeName(iIdx + 1);
			}
			
			// 得到行数
			// rs.last();
			// int iRowCount = rs.getRow();
			// rs.beforeFirst();
			// ds.Rows.ensureCapacity(iRowCount);
			
			// 初始化每行
			long lRowCount = 0;
			while (rs.next())
			{
				DataRow dRow = new DataRow(ds);
				dRow.InitDataRow(rs, iColumnCount);
				ds.AddRow(dRow);
				lRowCount++;
				// 最大输出只能是10000条记录
				if (lRowCount > 10000)
					break;
			}
			
			ds.RowCount = lRowCount;
			ds.ColumnsCount = iColumnCount;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	

	/**
	 * 将单条记录填充到HashMap中
	 * 
	 * @param hsStore
	 * @param rs
	 */
	public static void FillHashMap(HashMap<String, Object> hsStore, ResultSet rs)
	{
		ResultSetMetaData rsmd = null;
		try
		{
			// 如果是空记录值，则返回，否则定位到第一条记录
			if (rs.isAfterLast() || (rs.next() && rs.isAfterLast()))
				return;
			
			// 初始化查询结果中的列名
			rsmd = rs.getMetaData();
			int iColumnCount = rsmd.getColumnCount();
			// 定义字段名称数组
			String[] aryFieldName = new String[iColumnCount];
			// 定义字段日期类型数组
			Boolean[] aryBooleans = new Boolean[iColumnCount];
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				aryFieldName[iIdx] = rsmd.getColumnLabel(iIdx + 1);
				if (rsmd.getColumnTypeName(iIdx + 1).equals("DATE")
						|| rsmd.getColumnTypeName(iIdx + 1).equals("TIMESTAMP"))
					aryBooleans[iIdx] = true;
				else
					aryBooleans[iIdx] = false;
			}
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				Object objTemp = rs.getObject(iIdx + 1);
				// 如果是日期型，格式化标准的格式化
				if (aryBooleans[iIdx] == true)
				{
					if (objTemp == null)
						objTemp = "";
					else
					{
						try
						{
							java.util.Date dt = df.parse(objTemp.toString());
							objTemp = df.format(dt);
						}
						catch (ParseException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				hsStore.put(aryFieldName[iIdx], objTemp);
			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	

	/**
	 * 将查询结果集返回到jsonObject对象中，用户对
	 * 
	 * @param jsonStore
	 * @param rs
	 */
	public static void FillJSON(JSONArray jsaryStore, ResultSet rs)
	{
		ResultSetMetaData rsmd = null;
		try
		{
			// 初始化查询结果中的列名
			rsmd = rs.getMetaData();
			int iColumnCount = rsmd.getColumnCount();
			// 定义字段名称数组
			String[] aryFieldName = new String[iColumnCount];
			// 定义字段日期类型数组
			Boolean[] aryBooleansDate = new Boolean[iColumnCount];
			
			// 定义字段日期类型数组
			Boolean[] aryBooleansTimestamp = new Boolean[iColumnCount];
			
			// 数字型数组
			Boolean[] aryBooleansInt = new Boolean[iColumnCount];
			// 字符串型数组
			Boolean[] aryBooleansString = new Boolean[iColumnCount];
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				aryFieldName[iIdx] = rsmd.getColumnLabel(iIdx + 1);
				String strFieldType = rsmd.getColumnTypeName(iIdx + 1).toUpperCase().trim();
				// if ("FLOAT_PERCENT".equals(aryFieldName[iIdx]))
				// strTest = "11";
				// 定义日期型
				if (strFieldType.equals("DATE") || strFieldType.equals("DATETIME") )
					aryBooleansDate[iIdx] = true;
				else
					aryBooleansDate[iIdx] = false;
				
				//定义timestamp类型
				if ( strFieldType.equals("TIMESTAMP"))
					aryBooleansTimestamp[iIdx] = true;
				else
					aryBooleansTimestamp[iIdx] = false;
				
				
				// 定义数字型
				if ("INTEGER".equals(strFieldType) || "NUMBER".equals(strFieldType) || "NUMERIC".equals(strFieldType)
						|| "DECIMAL".equals(strFieldType) || "FLOAT".equals(strFieldType)
						|| "DOUBLE".equals(strFieldType) || "REAL".equals(strFieldType)
						|| "BIGINT ".equals(strFieldType) || "BIT".equals(strFieldType)
						|| "SMALLINT".equals(strFieldType))
					aryBooleansInt[iIdx] = true;
				else
				{
					aryBooleansInt[iIdx] = false;
				}
				
				// 定义字符串型
				if ("VARCHAR".equals(strFieldType) || "CLOB".equals(strFieldType) || "CHAR".equals(strFieldType))
					aryBooleansString[iIdx] = true;
				else
				{
					aryBooleansString[iIdx] = false;
				}
				
			}
			
			// 初始化每行
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // HH:mm:ss
			
			while (rs.next())
			{
				JSONObject jsobjTempObject = new JSONObject();
				for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
				{
					Object objTemp = null;
					/**
					 * timestamp类型采用单独取值的方式  zhouxinxi 2014-6-24修改
					 */
					if(aryBooleansTimestamp[iIdx] == true){
						 objTemp = rs.getTimestamp(iIdx + 1);
					}else{
						 objTemp = rs.getObject(iIdx + 1);
					}
					/**
					 * end
					 */
					
					
					// 如果是日期型，格式化标准的格式化
					if (aryBooleansDate[iIdx] == true || aryBooleansTimestamp[iIdx] == true)
					{
						if (objTemp == null){
							objTemp = "";
						}else
						{
							try
							{
								java.util.Date dt = df.parse(objTemp.toString());
								
								objTemp = df.format(dt);
							}
							catch (ParseException e)
							{
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}
						}
					}
					// 如果为空，则根据数据类型初始化值
					if (objTemp == null)
					{
						if (aryBooleansInt[iIdx])
							jsobjTempObject.put(aryFieldName[iIdx], 0);
						else if (aryBooleansString[iIdx])
							jsobjTempObject.put(aryFieldName[iIdx], "");
						else
							jsobjTempObject.put(aryFieldName[iIdx], "");
					}
					else
						jsobjTempObject.put(aryFieldName[iIdx], objTemp);
				}
				
				jsaryStore.add(jsobjTempObject);
			}
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	

	/**
	 * 根据列类型的数组，填充数值型，字符串型，日期型的布尔数值标识
	 * 
	 * @param aryColumnsType
	 *            字段类型数值
	 * @param aryBooleansDate
	 *            字段日期型标识
	 * @param aryBooleansInt
	 *            字段数值型标识
	 * @param aryBooleansString
	 *            字段字符串型标识
	 */
	public static void getColumnsTypeAry(String[] aryColumnsType, Boolean[] aryBooleansDate, Boolean[] aryBooleansInt,
			Boolean[] aryBooleansString)
	{
		
		String strFieldType = "";
		for (int iIdx = 0; iIdx < aryColumnsType.length; iIdx++)
		{
			strFieldType = aryColumnsType[iIdx];
			// 定义日期型
			if (strFieldType.equals("DATE") || strFieldType.equals("DATETIME") || strFieldType.equals("TIMESTAMP"))
				aryBooleansDate[iIdx] = true;
			else
				aryBooleansDate[iIdx] = false;
			
			// 定义数字型
			if ("INTEGER".equals(strFieldType) || "NUMBER".equals(strFieldType) || "NUMERIC".equals(strFieldType)
					|| "DECIMAL".equals(strFieldType) || "FLOAT".equals(strFieldType) || "DOUBLE".equals(strFieldType)
					|| "REAL".equals(strFieldType) || "BIGINT ".equals(strFieldType) || "BIT".equals(strFieldType)
					|| "SMALLINT".equals(strFieldType))
				aryBooleansInt[iIdx] = true;
			else
			{
				aryBooleansInt[iIdx] = false;
			}
			
			// 定义字符串型
			if ("VARCHAR".equals(strFieldType) || "CLOB".equals(strFieldType) || "CHAR".equals(strFieldType))
				aryBooleansString[iIdx] = true;
			else
			{
				aryBooleansString[iIdx] = false;
			}
		}
		
	}
	

	/**
	 * 将DataSet对象转换为JSONArray对象
	 * 
	 * @param dsDataSet
	 * @return
	 */
	public static JSONArray ConvertDataSetToJSONArrary(DataSet dsDataSet)
	{
		JSONArray jsaryStore = new JSONArray();
		if (dsDataSet.RowCount == 0)
			return jsaryStore;
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		// 定义字段日期类型数组
		Boolean[] aryBooleansDate = new Boolean[dsDataSet.ColumnsCount];
		// 数字型数组
		Boolean[] aryBooleansInt = new Boolean[dsDataSet.ColumnsCount];
		// 字符串型数组
		Boolean[] aryBooleansString = new Boolean[dsDataSet.ColumnsCount];
		// 初始化数组值
		DataSet.getColumnsTypeAry(dsDataSet.ColumnsType, aryBooleansDate, aryBooleansInt, aryBooleansString);
		
		// 将数据集转换成为JsonArray对象
		for (int iRow = 0; iRow < dsDataSet.RowCount; iRow++)
		{
			JSONObject jsobjTempObject = new JSONObject();
			for (int iIdx = 0; iIdx < dsDataSet.ColumnsCount; iIdx++)
			{
				Object objTemp = dsDataSet.Row(iRow).getObject(iIdx);
				// 如果是日期型，格式化标准的格式化
				if (aryBooleansDate[iIdx] == true)
				{
					if (objTemp == null)
						objTemp = "";
					else
					{
						try
						{
							java.util.Date dt = df.parse(objTemp.toString());
							objTemp = df.format(dt);
						}
						catch (ParseException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				// 如果为空，则根据数据类型初始化值
				if (objTemp == null)
				{
					if (aryBooleansInt[iIdx])
						jsobjTempObject.put(dsDataSet.columnsNames[iIdx], 0);
					else if (aryBooleansString[iIdx])
						jsobjTempObject.put(dsDataSet.columnsNames[iIdx], "");
					else
						jsobjTempObject.put(dsDataSet.columnsNames[iIdx], "");
				}
				else
					jsobjTempObject.put(dsDataSet.columnsNames[iIdx], objTemp);
			}
			
			jsaryStore.add(jsobjTempObject);
			
		}
		
		return jsaryStore;
	}
	

	/**
	 * 实现两个DS数据的合并
	 * 
	 * @param dsDataSet
	 */
	public void addAll(DataSet dsDataSet)
	{
		if (dsDataSet == null || dsDataSet.RowCount == 0)
		{
			return;
		}
		// 验证两个DS数据一致性
		if (dsDataSet.ColumnsCount != this.ColumnsCount)
		{
			throw new RuntimeException("DataSet 类型不匹配");
		}
		for (int index = 0; index < dsDataSet.columnsNames.length; index++)
		{
			if (dsDataSet.columnsNames[index] == null
					|| !dsDataSet.columnsNames[index].equals(this.columnsNames[index]))
			{
				throw new RuntimeException("DataSet 类型不匹配");
			}
		}
		// 数据合并
		this.Rows.addAll(dsDataSet.Rows);
		// 更新rowCount
		this.RowCount = this.RowCount + dsDataSet.RowCount;
	}
	

	/**
	 * 将DS插入到指定的位置
	 * 
	 * @param dsDataSet
	 * @param index
	 * @throws Exception
	 */
	public void addRow(DataSet dsDataSet, int index)
	{
		if (dsDataSet == null || dsDataSet.RowCount == 0)
		{
			return;
		}
		// 验证两个DS数据一致性
		if (dsDataSet.ColumnsCount != this.ColumnsCount)
		{
			throw new RuntimeException("DataSet 类型不匹配");
		}
		for (int i = 0; i < dsDataSet.columnsNames.length; i++)
		{
			if (dsDataSet.columnsNames[i] == null || !dsDataSet.columnsNames[i].equals(this.TABLE_NAMEs[i]))
			{
				throw new RuntimeException("DataSet 类型不匹配");
			}
		}
		// 数据合并
		if (index >= this.RowCount)
		{// 直接加入到末尾
			this.Rows.addAll(dsDataSet.Rows);
		}
		else
		{
			if (index <= 0)
			{// 插入列表头
				index = 0;
			}
			for (int k = 0; k < dsDataSet.RowCount; k++)
			{
				this.Rows.add(index + k, dsDataSet.Rows.get(k));
			}
		}
		// 更新rowCount
		this.RowCount = this.RowCount + dsDataSet.RowCount;
	}
	

	/**
	 * 判断DataSet是否为空
	 * 
	 * @return 为空 返回 true
	 */
	public boolean isEmpty()
	{
		if (this.RowCount == 0)
			return true;
		return false;
	}
	

	/**
	 * 将查询出的数据集转换成Model对象
	 * 
	 * @param ds
	 * @param rs
	 */

	public static <T> List<T> FillModel(Class<T> cls, ResultSet rs, Boolean bStrictMaching)
	{
	
		ResultSetMetaData rsmd = null;
		List<T> list = null;
		try
		{
			// 获取元数据
			rsmd = rs.getMetaData();
			int iColumnCount = rsmd.getColumnCount();
			// 定义字段名称数组
			String[] aryFieldName = new String[iColumnCount];
			// 定义字段类型数组
			Class[] aryFieldType = new Class[iColumnCount];
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				// 转换成java 名称
				aryFieldName[iIdx] = ColumnInfo.parseFieldName(rsmd.getColumnLabel(iIdx + 1));
				// 转换成java类型
				aryFieldType[iIdx] = ColumnInfo.parseJavaType(rsmd.getColumnTypeName(iIdx + 1), rsmd.getScale(iIdx + 1));
			}
//			
//			// 得到行数
//			rs.last();
//			int iRowCount = rs.getRow();
//			rs.beforeFirst();
			
			// 初始化每行
			long lRowCount = 0;
			list = new ArrayList<T>();
			while (rs.next())
			{
				// 行记录转换成model
				list.add(row2Model(rs, aryFieldName, aryFieldType, cls, iColumnCount, bStrictMaching));
				lRowCount++;
				// 最大输出只能是10000条记录
				if (lRowCount > 10000)
					break;
			}
			return list;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将查询出的数据集转换成Model对象
	 * 
	 * @param ds
	 * @param rs
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> FillModel(Class<T> cls, ResultSet rs, Boolean bStrictMaching,Boolean isNeedLimitRows)
	{
	
		ResultSetMetaData rsmd = null;
		List<T> list = null;
		try
		{
			// 获取元数据
			rsmd = rs.getMetaData();
			int iColumnCount = rsmd.getColumnCount();
			// 定义字段名称数组
			String[] aryFieldName = new String[iColumnCount];
			// 定义字段类型数组
			Class[] aryFieldType = new Class[iColumnCount];
			
			for (int iIdx = 0; iIdx < iColumnCount; iIdx++)
			{
				// 转换成java 名称
				aryFieldName[iIdx] = ColumnInfo.parseFieldName(rsmd.getColumnLabel(iIdx + 1));
				// 转换成java类型
				aryFieldType[iIdx] = ColumnInfo.parseJavaType(rsmd.getColumnTypeName(iIdx + 1), rsmd.getScale(iIdx + 1));
			}
//			
//			// 得到行数
//			rs.last();
//			int iRowCount = rs.getRow();
//			rs.beforeFirst();
			
			// 初始化每行
			long lRowCount = 0;
			list = new ArrayList<T>();
			while (rs.next())
			{
				// 行记录转换成model
				list.add(row2Model(rs, aryFieldName, aryFieldType, cls, iColumnCount, bStrictMaching));
				lRowCount++;
				// 最大输出只能是20000条记录
				if (lRowCount > 20000&isNeedLimitRows)
					break;
			}
			return list;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 获取行记录转换成java model
	 * 
	 * @param <T>
	 * @param rs
	 * @param aryFieldName
	 * @param aryFieldType
	 * @param cls
	 * @param iColumnCount
	 *            列数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T row2Model(ResultSet rs, String[] aryFieldName, Class[] aryFieldType, Class<T> cls,
			int iColumnCount, Boolean bStrictMaching) throws Exception
	{
		T obj = cls.newInstance();
		for (int iColIdx = 1; iColIdx <= iColumnCount; iColIdx++)
		{
			Object filedObj = null;
			if (aryFieldType[iColIdx - 1].equals(Timestamp.class))
			{
				filedObj = rs.getTimestamp(iColIdx);
			}
			else if (aryFieldType[iColIdx - 1].equals(java.util.Date.class))
			{// sql.Date->util.Date存在 hh:mi:ss部分丢失
				filedObj = rs.getObject(iColIdx);
			}
			
			else if (rs.getObject(iColIdx) != null && rs.getObject(iColIdx).getClass().getName().contains("CLOB"))
			{// clob流数据读取
				filedObj = StringUtil.clob2String((Clob) rs.getClob(iColIdx));
			}
			else if (rs.getObject(iColIdx) != null)
			{
				Constructor constructor = aryFieldType[iColIdx - 1].getDeclaredConstructor(String.class);
				filedObj = constructor.newInstance(rs.getObject(iColIdx).toString());
			}
			// 这句代码有问题，rs.getObject(iColIdx),就有可能为null
			// else{
			// DevLog.debug(iColIdx+"rs.getObject(iColIdx)=="+rs.getObject(iColIdx));
			// throw new RuntimeException("转换失败！可能是使用了oracle 聚合函数，无法映射数据类型");
			// }
			if (filedObj != null)
			{
				boolean flag = setField2Model(filedObj, aryFieldName[iColIdx - 1], obj, aryFieldType[iColIdx - 1], cls);
				if (!flag && bStrictMaching)
				{
					throw new Exception("[" + aryFieldType[iColIdx - 1].getName() + ":" + aryFieldName[iColIdx - 1]
							+ "]在model中不存在同名属性或者属性类型不一致");
				}
			}
		}
		return obj;
	}
	

	/**
	 * 
	 * @param fieldType
	 * @param fieldName
	 * @param val
	 * @param cls
	 */
	@SuppressWarnings("unchecked")
	public static <T> boolean setField2Model(Object val, String fieldName, T obj, Class fieldType, Class cls)
	{
		boolean flag = Boolean.FALSE;
		try
		{
			// 匹配模型中同名的属性
			if ("rownonum".equals(fieldName))
			{// 如果使用了分页查询的，则剔除rownonum进行封装 zhouxinxi edit at:20131210
				return true;
			}
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(Boolean.TRUE);
			// 匹配类型是否一致
			if (field.getType().equals(fieldType))
			{
				// 写入属性值
				field.set(obj, val);
			}
			else if (field.getType().equals(String.class))
			{
				field.set(obj, val.toString());
			}
			else if ((fieldType.equals(Long.class) && field.getType().equals(Integer.class)))
			{
				// 写入属性值
				field.set(obj, ((Long) val).intValue());
			}
			else if ((fieldType.equals(Long.class) && field.getType().equals(BigDecimal.class)))
			{
				// 写入属性值
				field.set(obj, new BigDecimal((Long)val));
			}
			flag = Boolean.TRUE;
		}
		catch (NoSuchFieldException e)
		{
			//
		}
		catch (Exception e)
		{
			DevLog.debug(fieldName + fieldType);
			e.printStackTrace();
		}
		if (!flag && !cls.getSuperclass().equals(Object.class))
		{
			return setField2Model(val, fieldName, obj, fieldType, cls.getSuperclass());
		}
		return flag;
	}
	
}
