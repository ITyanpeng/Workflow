package cn.sccl.common.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author Administrator
 * 
 */
public class DataRow implements Serializable
{
	
	private static final long	serialVersionUID	= -3369118574438101732L;
	
	/**
	 * 该行的DataSet父对象
	 */
	private DataSet				m_dsParent			= null;
	
	
	/**
	 * 构造函数
	 * 
	 * @param ds
	 *            该行的DataSet父对象
	 */
	public DataRow(DataSet ds)
	{
		m_dsParent = ds;
	}
	
	/**
	 * 该行所有的列对象
	 */
	// private ArrayList<Object> Columns = null;//new ArrayList<Object>(20);
	private Object[]	aryColumnsObjects	= null;
	
	
	/**
	 * 获取当前行所有的列值
	 * 
	 * @return
	 */
	public Object[] getColumnsObjects()
	{
		return this.aryColumnsObjects;
	}
	

	/**
	 * 设置当前行的列值
	 * 
	 * @param aryColumnsObj
	 */
	public void setColumnsObjects(Object[] aryColumnsObj)
	{
		this.aryColumnsObjects = null;
		this.aryColumnsObjects = aryColumnsObj;
	}
	

	/**
	 * 从数据集中初始化该行
	 * 
	 * @param rs
	 * @param iColumnCount
	 */
	public void InitDataRow(ResultSet rs, int iColumnCount)
	{
		aryColumnsObjects = null;
		aryColumnsObjects = new Object[iColumnCount];
		
		// 用于新增一行
		if (rs == null)
		{
			return;
		}
		
		for (int iColIdx = 1; iColIdx <= iColumnCount; iColIdx++)
		{
			try
			{
				aryColumnsObjects[iColIdx - 1] = rs.getObject(iColIdx);
			}
			catch (Exception e)
			{
				// 当不能获取到列对象时，以空字符串代替
				this.aryColumnsObjects[iColIdx - 1] = "";
			}
		}
		
		// this.Columns.clear();
		// for (int iColIdx = 1; iColIdx <= iColumnCount; iColIdx ++)
		// {
		// try {
		// this.Columns.add(rs.getObject(iColIdx));
		// }
		// catch (Exception e)
		// {
		// //当不能获取到列对象时，以空字符串代替
		// this.Columns.add("");
		// }
		// }
		
	}
	

	/**
	 * 根据索引列获取该列的对象，可以在上层根据列类型强制转换
	 * 
	 * @param iIndex
	 * @return
	 * @throws Exception
	 */
	public Object Column(int iColIndex) throws Exception
	{
		// return Columns.get(iColIndex);
		return aryColumnsObjects[iColIndex];
	}
	

	/**
	 * 获取指定列索引的字符串值
	 * 
	 * @param iColIndex
	 *            列索引，从 0 开始
	 * @return 该列值 字符串类型
	 */
	public String getString(int iColIndex)
	{
		// return Columns.get(iColIndex).toString();
		if (aryColumnsObjects[iColIndex] == null)
		{
			return "";
		}
		
		return aryColumnsObjects[iColIndex].toString();
	}
	

	/**
	 * 获取指定列索引的字符串值，带格式化字符串
	 * 
	 * @param iColIndex
	 *            列索引 0 开始
	 * @param strFormat
	 *            格式化字符串 格式化参考 Formatter
	 * @return
	 */
	public String getString(int iColIndex, String strFormat)
	{
		// String strResult = String.format(strFormat, Columns.get(iColIndex));
		if (aryColumnsObjects[iColIndex] == null)
		{
			return "";
		}
		
		String strResult = String.format(strFormat, aryColumnsObjects[iColIndex]);
		return strResult;
	}
	

	/**
	 * 获取指定列名的字符串值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return 列的字符串值
	 */
	public String getString(String strColumnName)
	{
		int iColumnIdx = -1;
		
		String strColName = strColumnName;
		if (m_dsParent.FieldsIndex.containsKey(strColName) == false)
		{
			strColName = strColName.toUpperCase();
			if (m_dsParent.FieldsIndex.containsKey(strColName) == false)
			{
				throw new RuntimeException("未知列名：" + strColumnName);
			}
		}
		
		iColumnIdx = m_dsParent.FieldsIndex.get(strColName);
		
		Object objTemp = aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx)
		// ;
		
		return objTemp == null ? "" : objTemp.toString();
	}
	

	/**
	 * 获取指定列名的字符串值，带格式化字符串
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @param strFormatter
	 *            格式化字符串
	 * @return
	 */
	public String getString(String strColumnName, String strFormatter) throws Exception
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			try
			{
				iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
			}
			catch (Exception ee)
			{
				throw new RuntimeException("未知列名：" + strColumnName);
			}
		}
		
		Object objTemp = aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx)
		// ;
		
		return String.format(strFormatter, objTemp == null ? "" : objTemp);
	}
	

	/**
	 * 获取指定列索引的BigDecimal值
	 * 
	 * @param iColumnIndx
	 *            列索引，从0开始
	 * @return
	 */
	public BigDecimal getBigDecimal(int iColumnIndx)
	{
		if (aryColumnsObjects[iColumnIndx] == null)
		{
			return new BigDecimal(0);
		}
		
		if (aryColumnsObjects[iColumnIndx] instanceof String)
		{
			return new BigDecimal(aryColumnsObjects[iColumnIndx].toString());
		}
		
		return (BigDecimal) aryColumnsObjects[iColumnIndx];
	}
	

	/**
	 * 获取指定列名的BigDecimal值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public BigDecimal getBigDecimal(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		if (aryColumnsObjects[iColumnIdx] instanceof String)
		{
			return new BigDecimal(aryColumnsObjects[iColumnIdx].toString());
		}
		return (BigDecimal) aryColumnsObjects[iColumnIdx];// Columns.get(
		// iColumnIdx);
	}
	

	/**
	 * 获取指定列的boolean值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public boolean getBoolean(int iColumnIndx)
	{
		return (Boolean) aryColumnsObjects[iColumnIndx];
	}
	

	/**
	 * 获取指定列名的boolean值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public boolean getBoolean(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		return (Boolean) aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx)
		// ;
	}
	

	/**
	 * 获取指定列的Date值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public Date getDate(int iColumnIndx)
	{
		return (Date) aryColumnsObjects[iColumnIndx];// Columns.get(iColumnIndx);
	}
	

	/**
	 * 获取指定列名的Date值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 * @throws ParseException 
	 */
	public java.util.Date getDate(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		if (aryColumnsObjects[iColumnIdx] == null)
		{
			return null;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // HH:mm:ss
		java.util.Date dt;
		try
		{
			dt = df.parse(aryColumnsObjects[iColumnIdx].toString());
		}
		catch (ParseException e)
		{
			
			try{
				df = new SimpleDateFormat("yyyy-MM-dd");
				dt = df.parse(aryColumnsObjects[iColumnIdx].toString());
			}catch(Exception ex){
				throw new RuntimeException(e);
			}
		}
		return dt;
	}
	

	/**
	 * 获取指定列的double值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public double getDouble(int iColumnIndx)
	{
		if (aryColumnsObjects[iColumnIndx] == null)
		{
			return 0;
		}
		return Double.parseDouble(aryColumnsObjects[iColumnIndx].toString());
	}
	

	/**
	 * 获取指定列名的double值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public double getDouble(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		if (aryColumnsObjects[iColumnIdx] == null)
		{
			return 0;
		}
		
		return Double.parseDouble(aryColumnsObjects[iColumnIdx].toString());
		
	}
	

	/**
	 * 获取指定列的float值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public float getFloat(int iColumnIndx)
	{
		if (aryColumnsObjects[iColumnIndx] == null)
		{
			return 0;
		}
		
		return Float.parseFloat(aryColumnsObjects[iColumnIndx].toString());
	}
	

	/**
	 * 获取指定列名的float值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public float getFloat(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		if (aryColumnsObjects[iColumnIdx] == null)
		{
			return 0;
		}
		
		return Float.parseFloat(aryColumnsObjects[iColumnIdx].toString());
	}
	

	/**
	 * 获取指定列的Int值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public int getInt(int iColumnIndx)
	{
		if (aryColumnsObjects[iColumnIndx] == null)
		{
			return 0;
		}
		
		return Integer.parseInt(aryColumnsObjects[iColumnIndx].toString());// Columns
	}
	

	/**
	 * 获取指定列名的int值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public int getInt(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		
		if (aryColumnsObjects[iColumnIdx] == null)
		{
			return 0;
		}
		
		return Integer.parseInt(aryColumnsObjects[iColumnIdx].toString());// Columns
		
	}
	

	/**
	 * 获取指定列的Long值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public long getLong(int iColumnIndx)
	{
		if ("BIGDECIMAL".equals(this.m_dsParent.ColumnsType[iColumnIndx]))
		{
			return ((BigDecimal) aryColumnsObjects[iColumnIndx]).longValue();
		}
		return Long.parseLong(aryColumnsObjects[iColumnIndx].toString());
	}
	

	/**
	 * 获取指定列名的Long值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public long getLong(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		if ("BIGDECIMAL".equals(this.m_dsParent.ColumnsType[iColumnIdx]))
		{
			return ((BigDecimal) aryColumnsObjects[iColumnIdx]).longValue();// Columns
		}
		// .
		// get
		// (
		// iColumnIndx
		// )
		// ;
		return Long.parseLong(aryColumnsObjects[iColumnIdx].toString());
		
	}
	

	/**
	 * 获取指定列的Object值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public Object getObject(int iColumnIndx)
	{
		return aryColumnsObjects[iColumnIndx];// Columns.get(iColumnIndx);
	}
	

	/**
	 * 获取指定列名的Object值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public Object getObject(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		return aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx);
	}
	

	/**
	 * 用新值重置指定列的值
	 * 
	 * @param iColumnIndex
	 *            列索引
	 * @param objValue
	 *            新的列值
	 */
	public void setObject(int iColumnIndex, Object objValue)
	{
		aryColumnsObjects[iColumnIndex] = null;
		aryColumnsObjects[iColumnIndex] = objValue;
	}
	

	/**
	 * 用新值重置指定列的值
	 * 
	 * @param strColumnName
	 *            列名
	 * @param objValue
	 *            新的列值
	 */
	public void setObject(String strColumnName, Object objValue)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		aryColumnsObjects[iColumnIdx] = objValue;// Columns.get(iColumnIdx);
	}
	

	/**
	 * 获取指定列的Short值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public short getShort(int iColumnIndx)
	{
		return Short.parseShort(aryColumnsObjects[iColumnIndx].toString());// Columns
		
	}
	

	/**
	 * 获取指定列名的Short值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public short getShort(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		return Short.parseShort(aryColumnsObjects[iColumnIdx].toString());// Columns
		
	}
	

	/**
	 * 获取指定列的Time值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public Time getTime(int iColumnIndx)
	{
		return (Time) aryColumnsObjects[iColumnIndx];// Columns.get(iColumnIndx);
	}
	

	/**
	 * 获取指定列名的Time值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public Time getTime(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		return (Time) aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx);
	}
	

	/**
	 * 获取指定列的Timestamp值
	 * 
	 * @param iColumnIndx
	 *            列索引
	 * @return
	 */
	public Timestamp getTimestamp(int iColumnIndx)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			java.util.Date dt = df.parse(aryColumnsObjects[iColumnIndx].toString());
			return new java.sql.Timestamp(dt.getTime());// (Timestamp) aryColumnsObjects[iColumnIndx];// Columns.get(iColumnIndx);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * 获取指定列名的Timestamp值
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return
	 */
	public Timestamp getTimestamp(String strColumnName)
	{
		int iColumnIdx = -1;
		try
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName);
		}
		catch (Exception e)
		{
			iColumnIdx = m_dsParent.FieldsIndex.get(strColumnName.toUpperCase());
		}
		if (aryColumnsObjects[iColumnIdx] == null)
		{
			return null;
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			java.util.Date dt = df.parse(aryColumnsObjects[iColumnIdx].toString());
			return new java.sql.Timestamp(dt.getTime());// (Timestamp) aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	
	

	


	

	/**
	 * 返回该行的所有值，用`作为间隔符
	 */
	public String toString()
	{
		String strResult = "";
		for (int iCol = 0; iCol < aryColumnsObjects.length; iCol++)
		{
			
		}
		return strResult;
	}
	/**
	 * 获取指定列名的字符串值针对将clob转String
	 * 
	 * @param strColumnName
	 *            列名(为提高效率，请使用大写，如ID, NAME)
	 * @return 列的字符串值
	 * @throws Exception  
	 */
	public String getClob(String strColumnName)  
	{
		int iColumnIdx = -1;
		
		String strColName = strColumnName;
		if (m_dsParent.FieldsIndex.containsKey(strColName) == false)
		{
			strColName = strColName.toUpperCase();
			if (m_dsParent.FieldsIndex.containsKey(strColName) == false)
			{
				throw new RuntimeException("未知列名：" + strColumnName);
			}
		}
		
		iColumnIdx = m_dsParent.FieldsIndex.get(strColName);
		
		Object objTemp = aryColumnsObjects[iColumnIdx];// Columns.get(iColumnIdx)
		Clob clob=(Clob)objTemp;
		;
		// ;
		
		return clob!=null?clobToString(clob):"";
	}
	

	/** 
	* @Title: ClobToString
	* @Description: 将clob转String
	* @param clob clob
	* @return 
	* @Return: String
	* @Throws: 
	* @Author: yl
	* @Date: 2014-1-21
	*/
	public String clobToString(Clob clob)
	{
		String reString = "";
		Reader is = null;
		try
		{
			is = clob.getCharacterStream();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			throw new RuntimeException("转换Clob出错");
		}
		// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = null;
		try
		{
			s = br.readLine();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			throw new RuntimeException("转换Clob出错");
		}
		StringBuffer sb = new StringBuffer();
		while (s != null)
		{
			// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
			sb.append(s);
			try
			{
				s = br.readLine();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				throw new RuntimeException("转换Clob出错");
			}
		}
		reString = sb.toString();
		return reString;
	}
}