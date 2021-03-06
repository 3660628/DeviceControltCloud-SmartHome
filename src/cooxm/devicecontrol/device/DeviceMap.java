﻿/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created：17 Dec 2014 17:51:32 
 */
package cooxm.devicecontrol.device;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cooxm.devicecontrol.control.LogicControl;
import cooxm.devicecontrol.util.MySqlClass;

/*** Map< ctrolID_deviceID,Device >*/
public class DeviceMap extends HashMap<String, Device> {
	static Logger log= Logger.getLogger(DeviceMap.class);
	/*** Map<ctrolID_deviceID,Device>*/
	//static Map<String, Device> deviceMap=new HashMap<String, Device>(); 	

	private static final long serialVersionUID = 1L;
	private MySqlClass mysql;
	
	public DeviceMap(){}
	public DeviceMap(Map<String, Device> profileSetMap){
		super(profileSetMap);	
	}
	
	public DeviceMap(MySqlClass mysql) throws SQLException{
		super(getDeviceMapFromDB(mysql));
	}

/*** 
   * 从入MYSQL读取device列表
   * @param  MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
   * @table  info_user_room_st_factor
   * @throws SQLException 
   */
	public static HashMap<String, Device> getDeviceMapFromDB(MySqlClass mysql )throws SQLException	{
		log.info("Start to initialize deviceMap....");
		HashMap<String, Device> deviceMap=new HashMap<String, Device>();
		DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
				//+" where ctr_id="+ctrolID
				//+" and deviceid="+deviceID
				+ ";";
		//System.out.println("query:"+sql);
		String res=mysql.select(sql);
		//System.out.println("get from mysql:\n"+res);
		if(res==null|| res==""){
			System.err.println("ERROR:empty query by : "+sql);
			return null ;
		} 
		
		String[] resArray=res.split("\n");
		//List<Device> deviceList=null;//new ArrayList<Factor>();
		Device device=new Device();
		for(String line:resArray){ 
			String[] index=line.split(",");
			device.ctrolID=Integer.parseInt(index[0]);	
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
				e.printStackTrace();
			}
			deviceMap.put(device.ctrolID+"_"+device.deviceID, device);
		}
		log.info("Initialize deviceMap finished !");
		return deviceMap;
	}
	
	/*** 获取 该家庭所有设备，包含加电和 传感器
	 * 
	 * */
	public List<Device> getDevicesByctrolID(int ctrolID){
		List<Device> deviceList= new ArrayList<Device>();
		for (Entry<String, Device> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==ctrolID){
				deviceList.add(entry.getValue());
			}			
		}
		return deviceList;
	}
	
	/*** 获取 该家庭所有设备，只包含加电
	 *   <pre> device.type=0;
	 **/
	//@SuppressWarnings("null")
	public List<Device> getApplianceByctrolID(int ctrolID){
		List<Device> deviceList=new ArrayList<Device>();
		for (Entry<String, Device> entry : this.entrySet()) {
			if(Integer.parseInt(entry.getKey().split("_")[0])==ctrolID  && entry.getValue().type==0){
				deviceList.add(entry.getValue());
			}			
		}
		return deviceList;
	}
	
	/**
	 *重写父类的方法，当向这个map添加一个情景模式时，自动把这个情景模式写入数据库
	 *  */
	@Override
	public Device put(String key,Device device) {
		if(null==this.mysql)
			return null;
		device.saveToDB(this.mysql)	;
		return super.put(key, device);
	}	
	
	/**
	 *重写父类的方法，当向这个map删除一个情景模式时，自动把这个情景模式从数据库删除
	 *  */
	@Override
	public Device remove(Object ctrolID_deviceID) {		
		if(null==this.mysql)
			return null;
		Device device = super.get(ctrolID_deviceID);
		Device.DeleteOneDeviceFromDB(mysql, device.ctrolID, device.deviceID);
		return super.remove(ctrolID_deviceID);
	}

	/**
	 * @throws SQLException * 
	 * @Title: main 
	 * @Description: TODO
	 * @param  args    
	 * @return void    
	 * @throws 
	 */
	public static void main(String[] args) throws SQLException {
//		MySqlClass mysql=new MySqlClass("172.16.35.170","3306","cooxm_device_control", "root", "cooxm");
//		DeviceMap dm=new DeviceMap(mysql);
//		System.out.println(dm.size());
		

		

	}

}
