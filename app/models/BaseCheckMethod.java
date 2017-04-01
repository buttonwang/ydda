/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author Administrator
 * 
 */
import javax.persistence.*;
import java.util.*;
import play.db.jpa.Model;

@Entity
@Table(name="YDDA_BaseCheckMethod")
public class BaseCheckMethod extends Model {   
    
    public String title;   
        
    public String score;
               
}
