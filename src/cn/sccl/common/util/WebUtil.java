package cn.sccl.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * WebUtil 
 * web功能类，主要用于封装并向response返回数据
 * @author Gun
 * 
 */

public class WebUtil {
	
	private static Logger	log	= LoggerFactory.getLogger(WebUtil.class);
	private static final int SUCCESSFUL=0;
	private static final int FAILED=-1;
	
	public static void returnString( HttpServletResponse response, String str) throws IOException{
		PrintWriter out = null;
		try
		{
			JSONObject jo=new JSONObject();
			jo.put("returncode", WebUtil.SUCCESSFUL);
			jo.put("data", str);
			response.setHeader("Content-type", "text/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");  
			out = response.getWriter();
			out.write(jo.toString());
		}
		finally{
			if(out != null ){
				out.flush();
				out.close();
			}
		}	
		
	}
	
	public static void returnJson( HttpServletResponse response, JSONArray json) throws IOException{
		PrintWriter out = null;
		try
		{
			JSONObject jo=new JSONObject();
			jo.put("returncode", WebUtil.SUCCESSFUL);
			jo.put("data", json);
			response.setHeader("Content-type", "text/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");  
			out = response.getWriter();
			out.write(jo.toString());
		}
		finally{
			if(out != null ){
				out.flush();
				out.close();
			}
		}	
	}
	
	/**
	 * 返回流数据
	 * @param response
	 * @param json
	 * @throws IOException
	 */
	public static void returnStream( HttpServletResponse response, InputStream in, String contextType) throws IOException{
		OutputStream out=null;
		try
		{
			response.setHeader("Content-type", contextType);
			response.setContentType(contextType);
			out = response.getOutputStream();
			  // 把图片的输入流程写入response的输出流中
			  byte[] b = new byte[1024];
			  for (int len = -1; (len= in.read(b))!=-1; ) {
				  out.write(b, 0, len);
			  }
			  // 关闭流
		} catch (Exception e) {
		  e.printStackTrace();
		}
		finally{
			if(out != null ){
				out.flush();
				out.close();
			}
			if(in != null)
				in.close();
		}	
		
	}
	
	
	public static void returnError( HttpServletResponse response, Exception e) {
		PrintWriter out = null;
		try
		{
			JSONObject jo=new JSONObject();
			jo.put("returncode", WebUtil.FAILED);
			jo.put("errormessage", e.getMessage());
			response.setHeader("Content-type", "text/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");  
			out = response.getWriter();
			out.write(jo.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error(e1.getMessage());
		}
		finally{
			if(out != null ){
				out.flush();
				out.close();
			}
		}	
		
	}
	
	public static void returnOK( HttpServletResponse response) {
		PrintWriter out = null;
		try
		{
			JSONObject jo=new JSONObject();
			jo.put("returncode", WebUtil.SUCCESSFUL);
			jo.put("message", "OK");
			response.setHeader("Content-type", "text/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");  
			out = response.getWriter();
			out.write(jo.toString());
		} catch (Exception e1) {
			e1.printStackTrace();
			log.error(e1.getMessage());
		}
		finally{
			if(out != null ){
				out.flush();
				out.close();
			}
		}	
		
	}

}
