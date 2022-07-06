package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-11-20.
 */
public enum ApproveLevelEnum {
    APPROVE_LEVEL_ENUM_ZERO("0","新建"),
    APPROVE_LEVEL_ENUM_ONE("1","科室已审核"),
    APPROVE_LEVEL_ENUM_TWO("2","医院已审核"),
    APPROVE_LEVEL_ENUM_SEVEN("7","退回"),
    APPROVE_LEVEL_ENUM_EIGHT("8","退回");

    private  String key;
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

    ApproveLevelEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public  static  Map getApproveLevelEnumsMap(){
        return  approveLevelEnumsMap;
    }

    public static final Map approveLevelEnumsMap = new HashMap();
    static {
        for (ApproveLevelEnum ale:ApproveLevelEnum.values()) {
            approveLevelEnumsMap.put(ale.getKey(),ale.getValue());
        }
    }

}
