package cn.sccl.common.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.sccl.common.util.DevLog;
import cn.sccl.common.util.StringUtil;

public class FileGenerator
{
	private final static String			TEMPLATE_PATH	= "cn/sccl/common/tool/scaffold/template/";
	private final static String			SRC_PATH		= "src" + StringUtil.FILE_SEPARATOR;
	private final static String			TEST_PATH		= "test" + StringUtil.FILE_SEPARATOR;
	protected String					pkgPath;
	protected String					clzName;
	protected String					template;
	protected String					suffix;
	private final Map<String, String>	mapping;
	
	
	public FileGenerator(String pkgPath, String clzName, String template, Map<String, String> mapping)
	{
		this(pkgPath, clzName, template, mapping, "java");
	}
	

	public FileGenerator(String pkgPath, String clzName, String template, Map<String, String> mapping, String fileSuffix)
	{
		this.pkgPath = pkgPath;
		this.clzName = clzName;
		this.template = template;
		this.mapping = mapping;
		this.suffix = fileSuffix;
	}
	

	public String getTargetFilePath()
	{
		String result = pkgPath.replace(StringUtil.DOT, StringUtil.FILE_SEPARATOR);
		result = result + StringUtil.FILE_SEPARATOR;
		result = result + clzName + StringUtil.DOT + suffix;
		if (clzName.endsWith("Test"))
		{// 单元测试类
			result = TEST_PATH + result;
		}
		else
		{
			result = SRC_PATH + result;// 普通类
		}
		return result;
	}
	

	public void execute()
	{
		execute(true);
	}
	

	public void execute(boolean debug)
	{
		String tmplFile = TEMPLATE_PATH + template;
		InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream(tmplFile);
		
		if (templateInputStream == null)
		{
			DevLog.debug("[WARN] " + tmplFile + " not exists.");
			return;
		}
		
		File f = new File(getTargetFilePath());
		if (f.exists())
		{
			DevLog.debug(f.getAbsoluteFile() + " aleardy exists.");
			return;
		}
		try
		{
			DevLog.debug(f.getAbsoluteFile() + " created !");
			if (debug)
			{
				writeContentWithTemplate(System.out, templateInputStream, mapping);
			}
			else
			{
				createFileWithDirs(f);
				writeContentWithTemplate(new PrintStream(f), templateInputStream, mapping);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			DevLog.debug(f.getAbsoluteFile() + " create failed.");
		}
	}
	

	private void writeContentWithTemplate(PrintStream ps, InputStream templateInputStream, Map<String, String> mapping)
			throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(templateInputStream));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			line = replaceWithMapping(line, mapping);
			ps.println(line);
		}
		br.close();
	}
	

	private String replaceWithMapping(String srcLine, Map<String, String> mapping)
	{
		final String TAG_BEGI = "${";
		final String TAG_END = "}";
		String result = srcLine;
		for (String key : mapping.keySet())
		{// 替换mapping集合中的各个标签对应值
			String value = mapping.get(key);
			result = StringUtils.replace(result, TAG_BEGI + key + TAG_END, value);
		}
		result = StringUtils.replace(result, TAG_BEGI + "pkgPath" + TAG_END, pkgPath);// 替换当前JAVA文件的包路径
		return result;
	}
	

	public boolean createFileWithDirs(File f) throws IOException
	{
		File parentDir = f.getParentFile();
		boolean parentCreated = false;
		if (!parentDir.exists())
		{
			parentCreated = parentDir.mkdirs();
		}
		if (parentCreated)
		{
			return f.createNewFile();
		}
		return false;
	}
	

	public static void main(String[] args)
	{
		FileGenerator gen = new FileGenerator("cn.sccl.scm.dao", "UserDAO", "DAO.txt", new HashMap<String, String>());
		gen.execute();
	}
}
