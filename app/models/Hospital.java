package models;

import flexjson.JSONSerializer;
import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

/**
 * Project entity managed
 */
@Entity 
@Table(name="YDDA_Hospital")
public class Hospital extends Model {

    public String name;
    
    public String remark;

    public String toString() {
        return name;
    }
    
    public static String toCheckJson(Hospital hospital) {
    	return new JSONSerializer()    	
    	  .include("id", "name", "remark")
    	  .exclude("*")
    	  .serialize(hospital);
    }
}

