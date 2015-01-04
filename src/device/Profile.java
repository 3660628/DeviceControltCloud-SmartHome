package device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created��2014��12��15�� ����3:03:30 
 */

import java.util.*;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import control.LogicControl;
import util.MySqlClass;


/***
 * һ��������龰ģʽ�����ܰ�������龰����(�ҵ硢������)
 * */
public class Profile {
	
	public int profileID;
	String profileName;
	int CtrolID;
	public int roomID;
	int roomType;
	int profileTemplateID;
	int profileSetID;
	List<Factor> factorList;
	Date createTime;
	public Date modifyTime;
	
	static final String  profileDetailTable="info_user_room_st_factor";
	static final String  profileIndexTable="info_user_room_st";
	

	public Profile (){}
	public Profile (Profile pf){
		this.profileID=pf.profileID;
		this.profileName=pf.profileName;
		this.profileID=pf.profileID;
		this.CtrolID=pf.CtrolID;
		this.roomID=pf.roomID;
		this.roomType=pf.roomType;
		this.profileTemplateID=pf.profileTemplateID;
		this.profileSetID=pf.profileSetID;
		this.factorList=pf.factorList;
		this.createTime=pf.createTime;
		this.modifyTime=pf.modifyTime;		
	}
	

	public Profile (JSONObject profileJson){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			this.profileID=profileJson.getInt("profileID");
			this.profileName=profileJson.getString("profileName");
			this.profileID=profileJson.getInt("profileID");
			this.CtrolID=profileJson.getInt("CtrolID");
			this.roomID=profileJson.getInt("roomID");
			this.roomType=profileJson.getInt("roomType");
			this.profileTemplateID=profileJson.getInt("profileTemplateID");
			this.profileSetID=profileJson.getInt("profileSetID");
			JSONArray factorListJSON= profileJson.getJSONArray("factorList");
			List<Factor> factorList = new ArrayList<Factor>() ;
			for(int i=0;i<factorListJSON.length();i++){
				JSONObject factorJson=factorListJSON.getJSONObject(i);
				Factor factor= new Factor();
				factor.factorID=factorJson.getInt("factorID");
				factor.minValue=factorJson.getInt("minValue");
				factor.maxValue=factorJson.getInt("maxValue");
				factor.compareWay=factorJson.getInt("compareWay");
				factor.validFlag=factorJson.getInt("validFlag");
				factor.createTime=sdf.parse(factorJson.getString("createTime"));
				factor.modifyTime=sdf.parse(factorJson.getString("modifyTime"));	
				factorList.add(factor);		
			}		
			this.factorList=factorList;
			this.createTime=sdf.parse(profileJson.getString("createTime"));
			this.modifyTime=sdf.parse(profileJson.getString("createTime"));	
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * ���龰ģʽ���� ת��Ϊһ��JSONObject �洢
	 * @return JSONObject
	 * */
	public JSONObject toJsonObj(){	
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject profileJson = new JSONObject();  
        JSONObject factorJson ; //= new JSONObject();  
        try {
		    profileJson.put("profileID",this.profileID);
		    profileJson.put("profileName",this.profileName);
		    profileJson.put("CtrolID",this.CtrolID); 
		    profileJson.put("roomID",this.roomID);
		    profileJson.put("roomType",this.roomType);
		    profileJson.put("profileTemplateID",this.profileTemplateID);
		    profileJson.put("profileSetID",this.profileSetID);
		    for(Factor factor: this.factorList){
		    	factorJson= new JSONObject(); 
		    	factorJson.put("factorID", factor.factorID);
		    	factorJson.put("minValue", factor.minValue);
		    	factorJson.put("maxValue", factor.maxValue);
		    	factorJson.put("compareWay", factor.compareWay);
		    	factorJson.put("validFlag", factor.validFlag);
		    	factorJson.put("createTime", sdf.format(factor.createTime));
		    	factorJson.put("modifyTime", sdf.format(factor.modifyTime));
		    	profileJson.accumulate("factorList",factorJson); 
		    }
		    
		    profileJson.put("createTime",sdf.format(this.createTime));
		    profileJson.put("modifyTime",sdf.format(this.createTime));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  		
		return profileJson;
	}
	
	public Factor getFactor(int factorID){		
		for (int i = 0; i < this.factorList.size(); i++) {
			if(this.factorList.get(i).factorID==factorID){
				return this.factorList.get(i);
			}			
		}
		return null;
	}
	
	
	public boolean isEmpty(){
		if(this.factorList==null||this.createTime==null ||this.modifyTime==null){			
			return true;
		}		
		return false;		
	}

	
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:				MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * @table profileDetailTable :  info_user_room_st_factor
	 * @table profileIndexTable  :	info_user_room_st
	 * @throws SQLException
	 * @returns 0 :profileΪ�գ�
	 * 			1   ������ɹ�
	 * */
	public int saveProfileToDB(MySqlClass mysql) throws SQLException{
		if(this.isEmpty()){
			System.out.println("ERROR:object is empty,can't save to mysql");
			return 0;
		}
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mysql.conn.setAutoCommit(false);
		for (Factor ft:this.factorList) {
			String sql="insert into "+profileDetailTable
					+" (userroomstid  ,"     
					+"ctr_id ,"
					+"factorid ,"
					+"lower ,"
					+"upper ,"
					+"cmpalg ,"
					+"valid_flag ,"
					+"createtime   ,"
					+"modifytime   "
					+ ")"				
					+"values "
					+ "("
					+this.profileID+","
					+this.CtrolID+","
					+ft.factorID+","
					+ft.minValue+","
					+ft.maxValue+","
					+ft.compareWay+","
					+ft.validFlag+",'"
					+sdf.format(ft.createTime)+"','"
					+sdf.format(ft.modifyTime)
					+"')";
			System.out.println(sql);
			int count=mysql.query(sql);
			if(count>0) System.out.println("insert success"); 	
		}		
		
		
		String sql="insert into "+profileIndexTable
				+" (userroomstid ," 
				+" userroomstname ," 
				+"ctr_id ,"
				+"roomid ,"
				+"roomtype ,"
				+"sttemplateid ,"
				+"stsetid  ,"
				+"createtime ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+this.profileID+",'"	
				+this.profileName+"',"	
				+this.CtrolID+","
				+this.roomID+","
				+this.roomType+","
				+0+","
				+this.profileSetID+",'"
				+sdf.format(this.createTime)+"','"
				+sdf.format(this.modifyTime)
				+"')";
		System.out.println(sql);		
		mysql.conn.commit();
		
		
		return 1;	
	}

   /*** 
   * ����MYSQL��ȡprofile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static Profile getOneProfileFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
		{
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mysql.conn.setAutoCommit(false);
			String sql="select "
					+"userroomstid," 
					+"ctr_id,"
					+"factorid,"
					+"lower,"
					+"upper,"
					+"cmpalg,"
					+"valid_flag,"
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+profileDetailTable
					+" where ctr_id="+CtrolID
					+" and userroomstid="+profileID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null || res=="" ) {
				System.err.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			Profile profile=new Profile();
			List<Factor> factorList=new ArrayList<Factor>();
			Factor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length>0){				
					ft=new Factor();			
					ft.factorID=Integer.parseInt(cells[2]);
					ft.minValue=Integer.parseInt(cells[3]);
					ft.maxValue=Integer.parseInt(cells[4]);
					ft.compareWay=Integer.parseInt(cells[5]);
					ft.validFlag=Integer.parseInt(cells[6]);
					try {
						ft.createTime=sdf.parse(cells[7]);
						ft.modifyTime=sdf.parse(cells[8]);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					factorList.add(ft);
					profile.factorList=factorList;
					profile.profileID=Integer.parseInt(cells[0]);
					profile.CtrolID=Integer.parseInt(cells[1]);		
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ profileDetailTable);
					return null;				
				}
			}
			
			String sql2="select  "
			+" userroomstid       ,"
			+"userroomstname,"
			+"ctr_id        ,"
			+"roomid        ,"
			+"roomtype      ,"
			+"sttemplateid  ,"
			+"stsetid  ,"			
			+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
			+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
			+ "  from "				
			+profileIndexTable
			+" where ctr_id="+CtrolID
			+" and userroomstid="+profileID
			+ ";";
			System.out.println("query:"+sql2);
			String res2=mysql.select(sql2);
			System.out.println("get from mysql:\n"+res2);
			if(res2==null|| res2==""){
				System.out.println("ERROR:empty query by : "+sql2);
				return null;
			} else if(res2.split("\n").length!=1){
				System.out.println("ERROR:Multi profile retrieved from mysql. ");
				return null;
			}else{
				String[] index=res2.split(",");
				profile.profileName=index[1];	
				profile.roomID=Integer.parseInt(index[3]);	
				profile.roomType=Integer.parseInt(index[4]);	
				profile.profileTemplateID=Integer.parseInt(index[5]); 
				profile.profileSetID=Integer.parseInt(index[6]);
				try {
					profile.createTime=sdf.parse(index[7]);
					profile.modifyTime=sdf.parse(index[8]);
				} catch (ParseException e) {
					e.printStackTrace();
				}				
			}		
	mysql.conn.commit();			
	return profile;			
	}
	
   /*** 
   * ����MYSQL��ȡprofile
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static int deleteProfileFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
		{
			mysql.conn.setAutoCommit(false);
			String sql="delete * "
					+ "  from  "				
					+profileDetailTable
					+" where ctr_id="+CtrolID
					+" and userroomstid="+profileID
					+ ";";
			System.out.println("query:"+sql);
			int res=mysql.query(sql);
			System.out.println("deleted "+ res + " rows of records from table:"+profileDetailTable);
			if(res<=0 ) {
				System.out.println("ERROR: empty result: "+sql);
				return 0;
			}
			
			String sql2="delete *  "
			+ "  from "				
			+profileIndexTable
			+" where ctr_id="+CtrolID
			+" and userroomstid="+profileID
			+ ";";
			System.out.println("query:"+sql2);
			int res2=mysql.query(sql2);
			System.out.println("deleted "+ res + " rows of records from table:"+profileIndexTable);
			if(res2<0){
				System.out.println("ERROR:exception happened: "+sql2);
				return 0;
			} 
		mysql.conn.commit();			
		return 1;			
	}
	
	
   /*** 
   * ����MYSQL��ȡprofile�� �龰����
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public	static List<Factor>  getProFactorsFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
		{
		    String tablename="info_user_room_st_factor";
			DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mysql.conn.setAutoCommit(false);
			String sql="select "
					+"userroomstid," 
					+"ctr_id,"
					+"factorid,"
					+"lower,"
					+"upper,"
					+"cmpalg,"
					+"valid_flag,"
					+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
					+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
					+ "  from  "				
					+tablename
					+" where ctr_id="+CtrolID
					+" and userroomstid="+profileID
					+ ";";
			System.out.println("query:"+sql);
			String res=mysql.select(sql);
			System.out.println("get from mysql:\n"+res);
			if(res==null ) {
				System.out.println("ERROR:exception happened: "+sql);
				return null;
			}else if(res=="") {
				System.out.println("ERROR:query result is empty: "+sql);
				return null;
			}
			String[] resArray=res.split("\n");
			List<Factor> factorList=null;//new ArrayList<Factor>();
			Factor ft=null;
			String[] cells=null;
			for(String line:resArray){
				cells=line.split(",");
				if(cells.length==9){				
					ft=new Factor();			
					ft.factorID=Integer.parseInt(cells[3]);
					//ft.factorType=Integer.parseInt(cells[1]);
					//ft.factorName=cells[2];
					ft.minValue=Integer.parseInt(cells[3]);
					ft.maxValue=Integer.parseInt(cells[4]);
					ft.compareWay=Integer.parseInt(cells[5]);
					ft.validFlag=Integer.parseInt(cells[6]);
					try {
						ft.createTime=sdf.parse(cells[7]);
						ft.modifyTime=sdf.parse(cells[8]);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					factorList=new ArrayList<Factor>();
					factorList.add(ft);					
				}else {
					System.out.println("ERROR:Columns mismatch between class Profile  and table  "+ tablename);
					return null;				
				}
			}		
		
			mysql.conn.commit();			
			return factorList;			
		}
	
	   /*** 
	   * ����MYSQL��ȡprofile�� �������
	   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	   * @table  info_user_room_st
	   * @throws SQLException 
	    */
	public	static Profile  getProfileHeadFromDB(MySqlClass mysql,int CtrolID,int profileID) throws SQLException
		{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Profile profile =new Profile();
		String sql2="select  "
				+" userroomstid       ,"
				+"userroomstname,"
				+"ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"sttemplateid  ,"
				+"stsetid  ,"					
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from "				
				+profileIndexTable
				+" where ctr_id="+CtrolID
				+" and userroomstid="+profileID
				+ ";";
		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql2);
			return null;
		} else if(res2.split("\n").length!=1){
			System.out.println("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			profile.profileName=index[1];	
			profile.roomID=Integer.parseInt(index[3]);	
			profile.roomType=Integer.parseInt(index[4]);	
			profile.profileTemplateID=Integer.parseInt(index[5]); 
			profile.profileSetID=Integer.parseInt(index[6]);
			try {
				profile.createTime=sdf.parse(index[7]);
				profile.modifyTime=sdf.parse(index[8]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}		
		
			return profile;		
		}
	
	
	/*public boolean SynProfile(MySqlClass mysql,Profile srcProfile) throws SQLException{
		
		Profile target=Profile.getProfileHeadFromDB(mysql, srcProfile.CtrolID, srcProfile.profileID);
		
		if(target.modifyTime.before(srcProfile.modifyTime)){ //mysql���ʱ��ȽϾɣ��򱣴��ϱ���profile
			if(srcProfile.saveProfileToDB(mysql)>0){
				return true;
			}else
				return false;
		}else { //mysql���ʱ���ʱ��Ƚ��£����·�mysql��profile                                              
			
		}
		
		
		
	}*/

	
	
	
	public static void main(String[] args) throws SQLException, JSONException {
		// TODO Auto-generated method stub
//		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
//		Profile p =new Profile();
//		p=Profile.getOneProfileFromDB(mysql, 12345677, 123456789);
//	    JSONObject jo=p.toJsonObj();
//		
//		String str = "[{\"id\":\"\",\"num\":\"\",\"dt\":\"2010-07-21T17:29:28\",\"consignee\":\"aaaa\",\"bank\":\"001\",\"ems\":\"0\"}]";
		//String str2="{\"student\":[{\"name\":\"leilei\",\"age\":23},{\"name\":\"leilei02\",\"age\":23}]}";

//		p.profileID++;
//		
//		try {
//			p.saveProfileToDB(mysql);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		String str2="{ \"people\":[{\"firstName\":\"Brett\",\"lastName\":\"McLaughlin\",\"email\":\"aaaa\"},{\"firstName\":\"Jason\",\"lastName\":\"Hunter\",\"email\":\"bbbb\"},{\"firstName\":\"Elliotte\",\"lastName\":\"Harold\",\"email\":\"cccc\"},{\"INT\":\"123\",\"BOOL\":\"false\",\"DOUBLE\":\"456.789\"}]}";
		System.out.println(new Date());
		for (int i=0;i<10000000;i++)	{
			JSONObject jo = new JSONObject(str2);
			Object ja=jo.get("people");		
		}
		System.out.println(new Date());
	}

}
