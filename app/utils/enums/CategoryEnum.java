package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-11-20.
 */
public enum CategoryEnum {

    CATEGORY_ENUM_ONE ("1","减分项目"),
    CATEGORY_ENUM_TWO ("2","加分项目"),
    CATEGORY_ENUM_THREE ("3","一票否决项目"),;

    private String key;
    private String value;
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    CategoryEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Map<String,String> getCategoryEnumsMap(){
        return  categoryEnumsMap;
    }

    public  static  final Map categoryEnumsMap = new HashMap();

    static {
        for (CategoryEnum ce: CategoryEnum.values()) {
            categoryEnumsMap.put(ce.getKey(),ce.getValue());
        }
    }

}
