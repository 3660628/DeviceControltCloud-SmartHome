package device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import util.*;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：2014年12月15日 下午3:03:30 
 */

public class Device {	
	int deviceID;
	String deviceSN;
	int CtrolID;
	int roomID;
	/***deviceType
	0-10：保留
	10：灯
	20：电视
	40: 空调
	60：窗户
	80：窗帘
	90：暖器

	1010:声感器
	1020:光感器
	1030：温感器
	1040：湿感器
	1050：声光温湿四合一传感器
	1051:四合一声感
	1052：四合一光感
	1053：四合一温感
	1054：四合一湿感
	1060:PM2.5检测器
	1070:有害气体检测器
	1080:智能插座

	2040:射频发射器
	2050:红外发射器
	*/
	int deviceType;
	
	/***0：家电   ;	1：传感器*/
	int type;
	/***
	 （顺时针方向）
	1：黄
	2:黄蓝
	3：蓝
	4：蓝绿
	5: 绿
	6：红绿
	7：红
	8：洪荒
	9：中间,与墙壁无关
	 */
	int wall; 
	
	/***relatedDevType定义参见deviceType, 代表没有关联*/
	int relatedDevType; 
	Date createTime;
	Date modifyTime;
	
	static final String deviceBindTable="info_user_room_bind";
	
	public Device(){}
	public Device(
			int CtrolID,
			int deviceID,
			String deviceSN,
			int deviceType,
			int type,
			int roomID,  
			int wall,
			int relatedDevType, 
			Date createTime,
			Date modifyTime
			) {		
		this.deviceID              =   deviceID      ;    
		this.deviceSN              = deviceSN    ;
		this.CtrolID              =    CtrolID       ;
		this.roomID              =     roomID        ;
		this.deviceType              = deviceType    ;
		this.type              =       type          ;
		this.wall              =       wall          ;
		this.relatedDevType          = relatedDevType;
		this.createTime              = createTime    ;
		this.modifyTime              = modifyTime    ;
	}
	
	public Device(Device dev) {		
		this.deviceID              =   dev.deviceID      ;    
		this.deviceSN              = dev.deviceSN    ;
		this.CtrolID              =    dev.CtrolID       ;
		this.roomID              =     dev.roomID        ;
		this.deviceType              = dev.deviceType    ;
		this.type              =       dev.type          ;
		this.wall              =       dev.wall          ;
		this.relatedDevType          = dev.relatedDevType;
		this.createTime              = dev.createTime    ;
		this.modifyTime              = dev.modifyTime    ;
	}
	
	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * table Name:info_user_room_bind
	 * */
	public int saveToDB(MySqlClass mysql){
		String tablename="info_user_room_bind";
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="insert into "+tablename
				+" (ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ ")"				
				+"values "
				+ "("
				+CtrolID+","
				+deviceID+",'"
				+deviceSN+"',"
				+deviceType+","
				+type+","
				+roomID+","
				+wall+","
				+relatedDevType+",'"
				+sdf.format(createTime)+"','"
				+sdf.format(createTime)
				+"')";
		System.out.println(sql);
		return mysql.query(sql);		
	}
	

	/*** 
	 * get device name:
	 * @return deviceName : a string name of a device depends on device ID
	 * <br> deviceType: <br>	
	0-10：保留
	10：灯
	20：电视
	40: 空调
	60：窗户
	80：窗帘
	90：暖器

	1010:声感器
	1020:光感器
	1030：温感器
	1040：湿感器
	1050：声光温湿四合一传感器
	1051:四合一声感
	1052：四合一光感
	1053：四合一温感
	1054：四合一湿感
	1060:PM2.5检测器
	1070:有害气体检测器
	1080:智能插座

	2040:射频发射器
	2050:红外发射器	 
	 */
	public String getDeviceName(){
		switch (this.deviceID) {
		case 10:
			return "light";
		case 20:
			return "tv";
		case 40:
			return "aircon";
		case 60:
			return "window";
		case 80:
			return "curtain";
		case 90:
			return "heating";
		case 1010:
			return "noicesensor";
		case 1020:
			return "lightsensor";
		case 1030:
			return "thermometer";
		case 1040:
			return "humidity";
		case 1050:
			return "fourinone";
		case 1051:
			return "fourinone-noice";
		case 1052:
			return "fourinone-light";
		case 1053:
			return "fourinone-thermometer";
		case 1054:
			return "fourinone-humidity";
		case 1060:
			return "pm25";
		case 1070:
			return "poisongas";
		case 1080:
			return "powpoint";
		case 2040:
			return "rfid";
		case 2050:
			return "infrared";
		default:
			return  "unknown";
		} 
		
	}
	
	/*** 
	 * Save device info to Mysql:
	 * Mysql:MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
	 * table Name:info_user_room_bind
	 * */
	public Device getOneDeviceFromDB(MySqlClass mysql,int CtrolID,int deviceID ){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Device device=new Device();
		String sql="select  "
				+" ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+CtrolID
				+" and deviceid="+deviceID
				+ ";";
		System.out.println("query:"+sql);
		String res2=mysql.select(sql);
		System.out.println("get from mysql:\n"+res2);
		if(res2==null|| res2==""){
			System.out.println("ERROR:empty query by : "+sql);
			return null;
		} else if(res2.split("\n").length!=1){
			System.out.println("ERROR:Multi profile retrieved from mysql. ");
			return null;
		}else{
			String[] index=res2.split(",");
			device.CtrolID=Integer.parseInt(index[0]);	
			device.deviceID=Integer.parseInt(index[1]);	
			device.deviceSN=index[2];
			device.deviceType=Integer.parseInt(index[3]);
			device.type=Integer.parseInt(index[4]);
			device.roomID=Integer.parseInt(index[5]);
			device.wall=Integer.parseInt(index[6]);
			device.relatedDevType=Integer.parseInt(index[7]);
			try {
				device.createTime=sdf.parse(index[8]);
				device.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return device;
	}
	
	
	public List<Device> getDevicesByRoomIDFromDB(MySqlClass mysql,int CtrolID,int deviceID ){
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Device device=new Device();
		String sql="select  "
				+" ctr_id       ,"     
				+"devid        ,"
				+"devsn        ,"
				+"devtype      ,"
				+"type         ,"
				+"roomid       ,"
				+"wall         ,"
				+"relateddevid ,"
				+"createtime   ,"
				+"modifytime   "
				+ " from "				
				+Device.deviceBindTable
				+" where ctr_id="+CtrolID
				+" and deviceid="+deviceID
				+ ";";
		System.out.println("query:"+sql);
		String res=mysql.select(sql);
		System.out.println("get from mysql:\n"+res);
		if(res==null|| res==""){
			System.out.println("ERROR:empty query by : "+sql);
			return null;
		} 
		
		String[] resArray=res.split("\n");
		List<Device> deviceList=null;//new ArrayList<Factor>();
		Device device=new Device();
		for(String line:resArray){
			String[] index=line.split(",");
			device.CtrolID=Integer.parseInt(index[0]);	
			device.deviceID=Integer.parseInt(index[1]);	
			device.deviceSN=index[2];
			device.deviceType=Integer.parseInt(index[3]);
			device.type=Integer.parseInt(index[4]);
			device.roomID=Integer.parseInt(index[5]);
			device.wall=Integer.parseInt(index[6]);
			device.relatedDevType=Integer.parseInt(index[7]);
			try {
				device.createTime=sdf.parse(index[8]);
				device.modifyTime=sdf.parse(index[9]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			deviceList=new ArrayList<Device>();
			deviceList.add(device);
		}

		return deviceList;
	}
	
	
	
	
	  public static void main(String[] args) throws SQLException{
		  MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
		  Date date=new Date();
		  Device dev=new Device(123456789 , 1234567891 , "XJFGOD847X" ,      40 ,    0 ,    203 ,    5 , 0,date,date);
		  
		  int count=dev.saveToDB(mysql);		
		  System.out.println("Query OK,  "+count+" row affected.");
	  }
	

}
