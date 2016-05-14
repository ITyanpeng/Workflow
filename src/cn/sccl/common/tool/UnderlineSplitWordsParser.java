package cn.sccl.common.tool;

import org.springframework.util.StringUtils;

import cn.sccl.common.util.StringUtil;

public class UnderlineSplitWordsParser implements WordsParser
{
	
	public String parseWords(String orginalString)
	{
		String[] items = orginalString.split(StringUtil.UNDER_LINE);
		String result = "";
		for (int i = 0; i < items.length; i++)
		{
			items[i] = items[i].toLowerCase();
			if (i > 0)
			{
				result = result + StringUtils.capitalize(items[i]);
			}
			else
			{
				result = result + items[i];
			}
		}
		return result;
	}
	
}

