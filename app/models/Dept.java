package models;

import play.*;
import play.data.validation.MaxSize;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

/**
 * Dept entity 
 * type: 职能科室，医德办公室
 */
@Entity 
@Table(name="YDDA_Dept")
public class Dept extends Model {

    public String name;
    
    public Integer type;

    public String remark;
  
    public String toString() {
        return name;
    }
    
    public String director;  //主任
}

