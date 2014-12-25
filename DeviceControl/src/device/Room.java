package device;

import java.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import util.MySqlClass;

import  org.apache.log4j.*;
import org.json.JSONException;
import org.json.JSONObject;


public class Room {
	int roomID;
	String roomName;
	int CtrolID;
	
	/***1��������	2������;	  3������;	 4��������*/
	int roomType;
	
	/***����������е��龰ģʽID �б� */
	List<Integer> profileList; 
	
	/***����������е��豸ID �б� */
	List<Integer> deviceList;
	Date createTime;
	Date modifyTime;
	
	Profile currProfile;
	private static final Logger logger = Logger.getLogger("global");
	
	static final String roomIndexTable = "info_user_room";

	public Room(){}
	public Room(Room room) {
		this.roomID       =     room.roomID      ;  
		this.roomName      =    room.roomName    ;
		this.CtrolID      =     room.CtrolID     ;
		this.roomType      =    room.roomType    ;
		//this.currProfile      = room.currProfile ;
		this.profileList      = room.profileList ;
		this.deviceList      =  room.deviceList  ;
		this.createTime      =  room.createTime  ;
		this.modifyTime      =  room.modifyTime  ;
	}
	
	public JSONObject toJsonObject(){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    JSONObject roomJson = new JSONObject(); 
        JSONObject profileJson ; 
        JSONObject deviceJson ; 
	    try {
		    roomJson.put("roomID",        this.roomID       );
			roomJson.put("roomName",    this.roomName      );
		    roomJson.put("CtrolID",        this.CtrolID      );
		    roomJson.put("roomType",      this.roomType        );
		    for(Integer profileID: this.profileList){
		    	profileJson= new JSONObject(); 		    	
		    	profileJson.put("profileID",profileID); 
		    	roomJson.accumulate("profileList", profileJson);
		    }
		    for(Integer deviceID: this.profileList){
		    	deviceJson= new JSONObject(); 
		    	deviceJson.put("deviceID",deviceID); 
		    	roomJson.accumulate("deviceList", deviceJson);
		    }
		    roomJson.put("createTime",sdf.format(this.createTime));
		    roomJson.put("modifyTime",sdf.format(this.createTime));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return roomJson;
	}

	/*** �ж���������Ƿ����ĳ���豸 */
	public boolean isDeviceExist(int deviceID){
		for (int i = 0; i < this.deviceList.size(); i++) {
			if(this.deviceList.get(i)==deviceID){
				return true;
			}			
		}		
		return false;		
	}
	
	/*** �ж���������Ƿ����ĳ���龰ģʽ */
	public boolean isProfileExist(int profileID){
		for (int i = 0; i < this.profileList.size(); i++) {
			if(this.profileList.get(i)==profileID){
				return true;
			}			
		}
		return false;		
	}
	
	
	
/*	public Profile getProfileByProfileID(int profileID){		
		for (int i = 0; i < this.profileList.size(); i++) {
			if(this.profileList.get(i).profileID==profileID){
				return this.profileList.get(i);
			}			
		}
		return null;
	}
	
	public Device getDeviceByDeviceID(int deviceID){		
		for (int i = 0; i < this.deviceList.size(); i++) {
			if(this.deviceList.get(i).deviceID==deviceID){
				return this.deviceList.get(i);
			}			
		}
		return null;
	}*/
	
	/***@deviceType
	0-10������
	10����
	20������
	40: �յ�
	60������
	80������
	90��ů��

	1010:������
	1020:�����
	1030���¸���
	1040��ʪ����
	1050��������ʪ�ĺ�һ������
	1051:�ĺ�һ����
	1052���ĺ�һ���
	1053���ĺ�һ�¸�
	1054���ĺ�һʪ��
	1060:PM2.5�����
	1070:�к���������
	1080:���ܲ���

	2040:��Ƶ������
	2050:���ⷢ����
	*/
	public List<Device> getDevicesByDeviceType(int deviceType){	
		List<Device> deviceList =new ArrayList<Device> ();
		for (Device d:deviceList) {
			if(d.deviceType==deviceType){
				deviceList.add(d);
			}			
		}
		return deviceList;
	}
	
	/*** @param�� type
	 * 0���ҵ�   ;	1��������*/
	public List<Device> getDevicesByType(int type){	
		List<Device> deviceList =new ArrayList<Device> ();
		for (Device d:deviceList)  {
			if(d.type==type){
				deviceList.add(d);
			}			
		}
		return deviceList;
	}
	
	
	
	
	public void switchToProfile(Profile profile){
		this.currProfile=profile;		
	}
	
	/*** 
	 * Save Profile info to Mysql:
	 * @param  Mysql:		    MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * @table roomIndexTable :  info_user_room
	 * @throws SQLException 
	 * */
	public boolean saveRoomIndexToDB(MySqlClass mysql) throws SQLException{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String sql="insert into "+roomIndexTable
				+" (ctr_id       ," 
				+" roomid       ," 
				+"roomtype        ,"
				+"description        ,"
				+"createtime   ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+this.CtrolID+","	
				+this.roomID+","	
				+this.roomType+",'"
				+this.roomName+"','"
				+sdf.format(this.createTime)+"','"
				+sdf.format(this.modifyTime)
				+"');";
		logger.info(sql);
		if(mysql.query(sql)!=-1){
		  logger.info("insert success!");
		  return true;
		}
		return false;
	}
	
   /*** 
   * ����MYSQL��ȡroom�� �������
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st
   * @throws SQLException 
   * @throws IOException 
   */
	public static Room  getRoomHeadFromDB(MySqlClass mysql,int CtrolID,int roomID) throws SQLException, IOException
	{
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Room room =new Room();
		String sql2="select  "
				+" ctr_id        ,"
				+"roomid        ,"
				+"roomtype      ,"
				+"description  ,"
				+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
				+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
				+ "  from "				
				+roomIndexTable
				+" where ctr_id="+CtrolID
				+" and roomid="+roomID
				+ ";";
		//logger.info("query:"+sql2);
		logger.info("query:"+sql2);
		String res2=mysql.select(sql2);
		logger.info("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			logger.info("ERROR:empty query by : "+sql2);
			return null;
		} else if(res2.split("\n").length!=1){
			logger.info("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			room.CtrolID=Integer.parseInt(index[0]);	
			room.roomID=Integer.parseInt(index[1]);	
			room.roomType=Integer.parseInt(index[2]);
			room.roomName=index[3];
			try {
				room.createTime=sdf.parse(index[4]);
				room.modifyTime=sdf.parse(index[5]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//logger.log.error(e,e.printStackTrace());
				//logger.error(e.getMessage(),e); 
			}
		}		
	
		return room;		
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		Room room =new Room();
		room=Room.getRoomHeadFromDB(mysql, 12345677, 201);
		room.roomID++;
		
		try {
			room.saveRoomIndexToDB(mysql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//logger.log.error(e.printStackTrace());
			//logger.error(e.getMessage(),e); 
		}
	}

}
