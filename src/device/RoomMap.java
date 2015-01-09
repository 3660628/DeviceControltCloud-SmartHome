package device;
/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created��2014��12��15�� ����4:32:50 
 */

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import util.MySqlClass;


/*** Map< ctrolID_RoomID,Room >*/
public class RoomMap  extends HashMap<String, Room>{

	private static final long serialVersionUID = 1L;
	MySqlClass mysql;

	RoomMap(){}
	RoomMap(Map<String, Room> roomMap){
		super(roomMap);		
	}
	
	public RoomMap(MySqlClass mysql) throws SQLException{
		super(getRoomMapFromDB(mysql));
		this.mysql=mysql;
	}
	
	public List<Room> getRoomsByCtrolID(int CtrolID){
		List<Room> roomList= new ArrayList<Room>();
		for (Entry<String, Room> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==CtrolID){
				roomList.add(entry.getValue());
			}			
		}
		return roomList;
	}
	
   /*** 
   * ����MYSQL��ȡ�����б�
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static HashMap<String, Room> getRoomMapFromDB(MySqlClass mysql) throws SQLException	
	{   System.out.println("Start to initialize roomMap....");
	    HashMap<String, Room> roomMap=new HashMap<String, Room>(); 
	    Room room= null;//new Room();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql2="select  "
		+"ctr_id        ,"
		+"roomid        ,"
		+"roomtype      ,"
		+"description  ,"
		+"date_format(createtime,'%Y-%m-%d %H:%i:%S'),"
		+"date_format(modifytime,'%Y-%m-%d %H:%i:%S')"
		+ "  from "				
		+Room.roomIndexTable
		+ ";";
//		System.out.println("query:"+sql2);
		String res2=mysql.select(sql2);
//		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql2);
			return null;
		} 
		String[] records=res2.split("\n");
		for(String line:records){			
			room =new Room();
			String[] index=line.split(",");
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
			}			
			roomMap.put(room.CtrolID+"_"+room.roomID, room);		
		}	
		System.out.println("Initialize deviceMap finished !");
		return roomMap;
	}
	
	/**
	 *��д����ķ������������map���һ���龰ģʽʱ���Զ�������龰ģʽд�����ݿ�
	 *  */
	public Room put(String key,Room room) {
		if(null==this.mysql)
			return null;
		try {
			room.saveRoomIndexToDB(this.mysql)	;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		super.put(key, room);
		return room;		
	}	
	
	/**
	 *��д����ķ������������mapɾ��һ���龰ģʽʱ���Զ�������龰ģʽ�����ݿ�ɾ��
	 *  */
	public Room remove(String key,Room room) {
		if(null==this.mysql)
			return null;
		Room.deleteRoomFromDB(mysql, room.CtrolID, room.roomID);
		this.remove(key);
		return room;
	}
		
	
}
