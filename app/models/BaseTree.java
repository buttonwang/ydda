package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="YDDA_BaseTree")
public class BaseTree {

        @Id
        public String id;
    
	public String pid;
	       
	public String text;
        
        public String deptname;
        
        public String title;
        
        public String sex;
        
        public String category;
        
	public String job;
        
        public String jobInTime; 
        
        public String qcn;
        
        public String pcn; 
               
}
