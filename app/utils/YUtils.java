package utils;

public class YUtils {

	public static String ligerGridData(String rows, long total) {
		String pattern = "{\"Rows\":#rows#, \"Total\":#total#}";
		pattern = pattern.replace("#rows#", rows).replace("#total#", String.valueOf(total));
		return pattern;
	}
	
}

