package org.ymm.constant;

import java.util.HashMap;
import java.util.Map;

public class MyConstant {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final Map<String, String> map = new HashMap(0);
	static {
		map.put("A001", AppConstant.DEFAULT_ERROR);
		
		map.put("A002", AppConstant.PARAM_ERROR);
		map.put("A003", AppConstant.NULL_ERROR);
		map.put("A004", AppConstant.PATCH_ERROR);
		map.put("A005", AppConstant.SYS_ERROR);
		map.put("A006", AppConstant.FLAG_ERROR);
		
		map.put("A007", AppConstant.DELETE_ERROR);
		map.put("A008", AppConstant.SAVE_ERROR);
		map.put("A009", AppConstant.UPDATE_ERROR);
		
		map.put("A010", AppConstant.LIST_ERROR);
		map.put("A011", AppConstant.EXSIS_ERROR);
		map.put("A012", AppConstant.ERROR_USERNAME);
		map.put("A013", AppConstant.IP_ERROR);
		map.put("A014", AppConstant.PWD_ERROR);
		map.put("A015", AppConstant.CORE_ERROR);
		map.put("A016", AppConstant.TIME_OUT);
	}
}
