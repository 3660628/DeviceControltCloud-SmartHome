package device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import util.MySqlClass;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created��17 Dec 2014 18:30:11 
 */

public class FactorDictionary extends Factor {
	static final String FactorDictionaryTable = "dic_st_factor";
	static Map<Integer, FactorDictionary> factorDictMap=new HashMap<Integer, FactorDictionary>(); 
	
	/***
    10����
    20������
    40: �յ�
    41: �յ�����
    42���յ��¶�
    43���յ�����
    60������
    80������
    90��ů��
    201����
    301��PM2.5 
    401���к�����
    501��ʪ��
    601���¶�
    701��������Ԥ����
    901������'
    */
	int  factorid       ;
	
	/*** 0:�ҵ����أ���� �յ�;  
          1���������أ����ǿ��'
    */
	int  factortype     ;
	String  factorname     ;
	String   description    ;
	String   measurement    ;
	
	/***1������ֵ��2�����ֵ,*/
	int  mstype         ;
	String  createoperator ;
	String  modifyoperator ;
	int  createtime     ;
	int  modifytime		;
	
	

	public FactorDictionary() {	}
	public FactorDictionary(
			int  factorid       ,
			int  factortype     ,
			String  factorname     ,
			String  description    ,
			String  measurement    ,
			int  mstype         ,
			String  createoperator ,
			String  modifyoperator ,
			int  createtime     ,
			int  modifytime		)
	{	
		this.factorid       =  factorid       ;
		this.factortype     =  factortype     ;
		this.factorname     =  factorname     ;
		this.description    =  description    ;
		this.measurement    =  measurement    ;
		this.mstype         =  mstype         ;
		this.createoperator =  createoperator ;
		this.modifyoperator =  modifyoperator ;
		this.createtime     =  createtime     ;
		this.modifytime		=  modifytime	  ;
		
	}
	
	public FactorDictionary getOneFactor(MySqlClass mysql) throws SQLException	
	{
		FactorDictionary fd= null;//new FactorDictionary();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+"factorid        ,"
		+"factortype        ,"
		+"factorname      ,"
		+"description  ,"
		+"measurement  ,"
		+"mstype  ,"
		+"modifyoperator  ,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+FactorDictionary.FactorDictionaryTable
		+" where factorid="+factorid
		+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql2);
			return null;
		} 
//		String[] records=res2.split("\n");
//		for(String line:records){			
			fd =new FactorDictionary();
			String[] index=res2.split(",");
			fd.factorid=Integer.parseInt(index[0]);	
			fd.factortype=Integer.parseInt(index[1]);
			fd.factorname=index[2];	
			fd.description=index[3];	
			fd.measurement=index[4]; 
			fd.mstype=Integer.parseInt(index[5]);	
			fd.createoperator=index[6];	
			fd.modifyoperator=index[7]; 
			try {
				fd.createTime=sdf.parse(index[8]);
				fd.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
				
		//}
		return fd;
	}
	
	
	public void InitializeFactorDictMap(MySqlClass mysql) throws SQLException	
	{
		FactorDictionary fd= null;//new FactorDictionary();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+"factorid        ,"
		+"factortype        ,"
		+"factorname      ,"
		+"description  ,"
		+"measurement  ,"
		+"mstype  ,"
		+"modifyoperator  ,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+FactorDictionary.FactorDictionaryTable
		//+" where factorid="+factorid
		+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql2);
			return ;
		} 
		String[] records=res2.split("\n");
		for(String line:records){			
			fd =new FactorDictionary();
			String[] index=line.split(",");
			fd.factorid=Integer.parseInt(index[0]);	
			fd.factortype=Integer.parseInt(index[1]);
			fd.factorname=index[2];	
			fd.description=index[3];	
			fd.measurement=index[4]; 
			fd.mstype=Integer.parseInt(index[5]);	
			fd.createoperator=index[6];	
			fd.modifyoperator=index[7]; 
			try {
				fd.createTime=sdf.parse(index[8]);
				fd.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			FactorDictionary.factorDictMap.put(fd.factorid, fd)	;
		}
		
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
