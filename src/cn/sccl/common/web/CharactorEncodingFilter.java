package cn.sccl.common.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CharactorEncodingFilter implements Filter {
	private final static String defaultEncoding = "GBK";
	private final static String ajaxEncoding = "UTF-8";
	protected String commonEncoding;
	protected FilterConfig filterConfig;
	protected boolean ignore = true;

	public CharactorEncodingFilter() {
		super();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		commonEncoding = filterConfig.getInitParameter("encoding");
		String value = filterConfig.getInitParameter("ignore");
		if (value == null)
			this.ignore = true;
		else if (value.equalsIgnoreCase("true"))
			this.ignore = true;
		else if (value.equalsIgnoreCase("yes"))
			this.ignore = true;
		else
			this.ignore = false;
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain filterChain) {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			// 接受第三方cookie
			//response.addHeader("P3P","CP=CAO PSA OUR");
			//response.setHeader("P3P", "CP=CAO PSA OUR");
			if (ignore || (request.getCharacterEncoding() == null)) {
				if (request.getHeader("RequestType") != null
						&& request.getHeader("RequestType").equalsIgnoreCase(
								"ajax")) {
					response.setCharacterEncoding(ajaxEncoding);
					request.setCharacterEncoding(ajaxEncoding);
					if (!request.getCharacterEncoding().equalsIgnoreCase(
							ajaxEncoding)) {
						response.setCharacterEncoding(ajaxEncoding);
						request.setCharacterEncoding(ajaxEncoding);
					}
				} else if (commonEncoding != null) {
					request.setCharacterEncoding(commonEncoding);
				} else {
					request.setCharacterEncoding(defaultEncoding);
				}
			}
			filterChain.doFilter(req, res);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		this.commonEncoding = null;
		this.filterConfig = null;
	}
}
