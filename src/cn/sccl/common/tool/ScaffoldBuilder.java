package cn.sccl.common.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.sccl.common.util.DevLog;
import cn.sccl.common.util.StringUtil;

public class ScaffoldBuilder
{
	protected final Log					logger				= LogFactory.getLog(getClass());
	
	protected final static String		PKG_PREFIX			= "cn.sccl.";
	protected final static String		PKG_SUFFIX_MODEL	= ".model.";
	protected final static String		PKG_SUFFIX_DAO		= ".dao.";
	protected final static String		PKG_SUFFIX_MANAGER	= ".service.";
	protected final static String		PKG_IMPL			= "impl";
	
	protected String					pkgName;
	protected String					clzName;
	protected TableInfo					tableInfo;
	private final Map<String, String>	mapping;
	
	
	public ScaffoldBuilder(String pkgName, String clzName, TableInfo tableInfo)
	{
		this.pkgName = PKG_PREFIX + pkgName;
		this.clzName = clzName;
		this.tableInfo = tableInfo;
		
		mapping = new HashMap<String, String>();
		
		mapping.put("clzName", clzName);
		mapping.put("clzNameLC", StringUtils.uncapitalize(clzName));
		mapping.put("tblName", tableInfo.getName());
		mapping.put("modelPath", getModelPath());
		mapping.put("modelQueryPath", getModelQueryPath());
		mapping.put("daoPath", getDaoPath());
		mapping.put("daoImplPath", getDaoImplPath());
		mapping.put("managerPath", getManagerPath());
		mapping.put("managerImplPath", getManagerImplPath());
		// for Model java
		mapping.put("fieldsDeclareInfo", tableInfo.getFieldsDeclareInfo());
		// for model sqlmapping
		mapping.put("resultMap", tableInfo.getResultMap());
		mapping.put("otherCondition", tableInfo.getOtherCondition());
		
		DevLog.debug(tableInfo.getPrimaryKey());
		mapping.put("primaryKey", tableInfo.getPrimaryKey());
		DevLog.debug(tableInfo.getFindByLike());
		mapping.put("findLikeBy", tableInfo.getFindByLike());
		DevLog.debug(tableInfo.getSelectStatement());
		mapping.put("selectStatement", tableInfo.getSelectStatement());
		DevLog.debug(tableInfo.getInsertStatement());
		mapping.put("insertStatement", tableInfo.getInsertStatement());
		DevLog.debug(tableInfo.getUpdateStatement());
		mapping.put("updateStatement", tableInfo.getUpdateStatement());
		// 动态字段更新
		mapping.put("updateMapModel", tableInfo.getUpdateMapModel());
		// 分页查询查询数量
		mapping.put("countSelectStatement", tableInfo.getSelectCountStatement());
	}
	

	public String getModelPath()
	{
		return pkgName + PKG_SUFFIX_MODEL + clzName;
	}
	
	public String getModelQueryPath(){
		return pkgName + PKG_SUFFIX_MODEL + clzName+"Query";
	}

	public String getSqlMapFile()
	{
		return pkgName + PKG_SUFFIX_MODEL + clzName + ".xml";
	}
	

	public String getDaoPath()
	{
		return pkgName + PKG_SUFFIX_DAO + clzName + "DAO";
	}
	

	public String getDaoImplPath()
	{
		return pkgName + PKG_SUFFIX_DAO + PKG_IMPL + StringUtil.DOT + clzName + "DAO"
				+ StringUtils.capitalize(PKG_IMPL);
	}
	

	public String getManagerPath()
	{
		return pkgName + PKG_SUFFIX_MANAGER + clzName + "Manager";
	}
	

	public String getManagerImplPath()
	{
		return pkgName + PKG_SUFFIX_MANAGER + PKG_IMPL + StringUtil.DOT + clzName + "Manager"
				+ StringUtils.capitalize(PKG_IMPL);
	}
	

	public List<FileGenerator> buildGenerators()
	{
		List<FileGenerator> list = new ArrayList<FileGenerator>();
		// model
		list.add(new FileGenerator(pkgName + ".model", clzName, "Model.txt", mapping));
		list.add(new FileGenerator(pkgName + ".model", clzName+"Query", "QueryModel.txt", mapping));
		
		list.add(new FileGenerator(pkgName + ".model", clzName, "SqlMap.txt", mapping, "xml"));
		// list.add(new FileGenerator(pkgName + ".model.validator", clzName +
		// "Validator", "Validator.txt", mapping));
		
		// dao
		list.add(new FileGenerator(pkgName + ".dao", clzName + "DAO", "DAO.txt", mapping));
		list.add(new FileGenerator(pkgName + ".dao.impl", clzName + "DAOImpl", "DAOImpl.txt", mapping));
		
		// manager
		list.add(new FileGenerator(pkgName + ".service", clzName + "Manager", "Manager.txt", mapping));
		list.add(new FileGenerator(pkgName + ".service.impl", clzName + "ManagerImpl", "ManagerImpl.txt", mapping));
		list.add(new FileGenerator(pkgName + ".service", clzName + "ManagerTest", "ManagerTest.txt", mapping));
		// controller
		list.add(new FileGenerator(pkgName + ".web", clzName + "Controller", "Controller.txt", mapping));
		return list;
	}
	
}
