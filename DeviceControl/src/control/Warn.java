/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created��17 Dec 2014 19:38:07 
 */
package control;

import java.util.Date;

public class Warn {	
	

	int  warnID       ;  
	String  warnName     ; 
	String warnContent;
	
	int  type       ; 
	/*** 
	 * �澯������ 0�����磻 1��SMS; 2 :both
	 * */
	int  channel    ;  
	Date  createtime ;  
	Date  modifytime ;   

	Warn(
		int  warnID       ,  
		String  warnName       ,  
		String  warnContent,  
		int  type       ,  
		int  channel    ,  
		Date  createtime ,  
		Date  modifytime )
	{
		this.warnID     = warnID       ;  
		this.warnName   = warnName       ;  
		this.warnContent= warnContent;  
		this.type       = type       ;  
		this.channel    = channel    ;  
		this.createtime = createtime ;  
		this.modifytime = modifytime ;		
	}
	

	/*** 
	 * @Title: main 
	 * @Description: TODO
	 * @param @param args    
	 * @return void    
	 * @throws 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
