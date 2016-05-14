package cn.sccl.common.tool;

import cn.sccl.common.util.BaseTestCase;


public class Create extends BaseTestCase
{
	
	public static void main(String arg[]) throws Exception
	{

		ScaffoldGen sg = new ScaffoldGen("mss.contract", "ContractItem", "Contract_Item");


		
		sg.execute();
	}
	
}
