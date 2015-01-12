package control;
/**
 * Copyright 2014 Cooxm.com
 * All right reserved.
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * Created��2014��12��15�� ����4:48:54 
 */


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import device.Device;
import device.DeviceMap;
import device.Profile;
import device.ProfileMap;
import device.ProfileSet;
import device.ProfileSetMap;
import device.RoomMap;
import redis.clients.jedis.Jedis;
import socket.CtrolSocketServer;
import socket.Message;
import util.MySqlClass;

public class LogicControl {	
	
	public static final short COMMAND_START            		   =  0x1600;
	public static final short COMMAND_ACK_OFFSET       		   =  0x4000; 
	
    /*** ���� �龰ģʽ����    @see get_room_profile() */
	private static final short GET_ROOM_PROFILE					=	COMMAND_START+1;	
    /*** ���� �龰ģʽ�Ļظ�    @see get_room_profile_ack() */
	private static final short GET_ROOM_PROFILE_ACK     		=   COMMAND_START+1 + COMMAND_ACK_OFFSET;	
	
    /*** ���� �龰ģʽ   @see set_room_profile()  */
	private static final short SET_ROOM_PROFILE					=	COMMAND_START+2;	
    /*** ���� �龰ģʽ�Ļظ�   @see set_room_profile_ack()  */
	private static final short SET_ROOM_PROFILE_ACK	    		=	COMMAND_START+2+COMMAND_ACK_OFFSET;

    /*** ɾ�� �龰ģʽ   @see delete_room_profile()  */
	private static final short DELETE_ROOM_PROFILE				=	COMMAND_START+3;	
    /*** ɾ�� �龰ģʽ�Ļظ�   @see set_room_profile_ack()  */
	private static final short DELETE_ROOM_PROFILE_ACK	    	=	COMMAND_START+3+COMMAND_ACK_OFFSET;
	
	/*** �п��л��龰ģʽ���� */
	private static final short SWITCH_ROOM_PROFILE				=	COMMAND_START+4;
	/*** �п��л��龰ģʽ���� �Ļظ� */
	private static final short SWITCH_ROOM_PROFILE_ACK			=	COMMAND_START+4+COMMAND_ACK_OFFSET;
	
	/*** ���� �龰ģʽ�� */	
	private static final short GET_RROFILE_SET					=	COMMAND_START+21;
	/*** ���� �龰ģʽ�� �Ļظ�*/	
	private static final short GET_RROFILE_SET_ACK				=	COMMAND_START+21+COMMAND_ACK_OFFSET;
	
	/*** ���� �龰ģʽ��*/
	private static final short SET_RROFILE_SET					=	COMMAND_START+22;
	/*** ���� �龰ģʽ�� �Ļظ�*/
	private static final short SET_RROFILE_SET_ACK				=	COMMAND_START+22+COMMAND_ACK_OFFSET;

	/*** ɾ�� �龰ģʽ��*/
	private static final short DELETE_RROFILE_SET				=	COMMAND_START+23;
	/*** ɾ�� �龰ģʽ�� �Ļظ�*/
	private static final short DELETE_RROFILE_SET_ACK			=	COMMAND_START+23+COMMAND_ACK_OFFSET;
	
	/*** �龰ģʽ���л� */
	private static final short SWITCH_RROFILE_SET				=	COMMAND_START+24;	
	/*** �龰ģʽ���л� �Ļظ�*/
	private static final short SWITCH_RROFILE_SET_ACK			=	COMMAND_START+24+COMMAND_ACK_OFFSET;

	
	/*** ����ҵ��б�*/
	private static final short GET_ONE_DEVICE				=	COMMAND_START+41;
	/*** ����ҵ��б� �Ļظ�*/
	private static final short GET_ONE_DEVICE_ACK			=	COMMAND_START+41+COMMAND_ACK_OFFSET;	
	
	/*** ���� �ҵ��б�*/
	private static final short SET_ONE_DEVICE				=	COMMAND_START+42;
	/*** ���� �ҵ��б� �Ļظ�*/
	private static final short SET_ONE_DEVICE_ACK			=	COMMAND_START+42+COMMAND_ACK_OFFSET;	

	/*** ɾ��ĳһ�� �ҵ�*/
	private static final short DELETE_ONE_DEVICE			=	COMMAND_START+43;
	/*** ɾ��ĳһ�� �ҵ�*/
	private static final short DELETE_ONE_DEVICE_ACK		=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
	/*** �л�ĳ���ҵ�״̬*/
	private static final short SWITCH_DEVICE_STATE		    =	COMMAND_START+44;
	/*** �л�ĳ���ҵ�״̬ �Ļظ�*/
	private static final short SWITCH_DEVICE_STATE_ACK		=	COMMAND_START+44+COMMAND_ACK_OFFSET;
		
    /*** �澯��Ϣ   */
	private static final short WARNING_MSG				 	=	COMMAND_START+61;
    /*** �澯��Ϣ  �Ļظ�  */
	private static final short WARNING_MSG_ACK				=	COMMAND_START+61+COMMAND_ACK_OFFSET;	
	
	
	/***********************  ERROR CODE :-50000  :  -59999 ************/
	private static final int SUCCESS                  =	0;
	private static final int RECEIVED                 = -50000;
	
	/** �龰ģʽ�¾�*/
	private static final int PROFILE_OBSOLETE         =	-50001;	
	/** �龰ģʽ������*/
	private static final int PROFILE_NOT_EXIST        = -50002;		
	private static final int PROFILE_SET_OBSOLETE     =	-50003;	
	private static final int PROFILE_SET_NOT_EXIST    = -50004;	
	
	private static final int DEVICE_OBSOLETE   	  	  = -50011;
	private static final int DEVICE_NOT_EXIST   	  = -50012;
	
	/*** ��Ϣ����ʶ�𣬵����ռ��˴��������յ��Լ����͵���Ϣ*/
	private static final int COMMAND_NOT_ENCODED   	  = -50021;
	/** ���ʱû����Ӧ*/
	public static final int TIME_OUT		   	      = -50022;
	/**�������β���*/
	public static final int WRONG_COMMAND		   	  = -50023;
	

	

	/***********************   resource needed   ************************/	
	static Logger log= Logger.getLogger(LogicControl.class);
	static MySqlClass mysql=null;
	Jedis jedis=null;// new Jedis("172.16.35.170", 6379,200);
	ProfileMap profileMap =null;
	ProfileSetMap profileSetMap =null;
	DeviceMap deviceMap=null;
	RoomMap roomMap=null;
	private final static String currentProfile= "currentProfile";
	private final static String currentProfileSet= "currentProfileSet";
	private final static String commandQueue= "commandQueue";
	
    public LogicControl() {}
    
    public LogicControl(Config cf) {
    	log.info("Starting logic control module ... ");
    	
		String mysql_ip			=cf.getValue("mysql_ip");
		String mysql_port		=cf.getValue("mysql_port");
		String mysql_user		=cf.getValue("mysql_user");
		String mysql_password	=cf.getValue("mysql_password");
		String mysql_database	=cf.getValue("mysql_database");
		
		String redis_ip         =cf.getValue("redis_ip");
		int redis_port       	=Integer.parseInt(cf.getValue("redis_port"));		
		
		mysql=new MySqlClass(mysql_ip, mysql_port, mysql_database, mysql_user, mysql_password);
		this.jedis= new Jedis(redis_ip, redis_port,200);
		try {
			this.profileMap= new ProfileMap(mysql);
			this.profileSetMap= new ProfileSetMap(mysql);
			this.deviceMap=new DeviceMap(mysql);
			this.roomMap=new RoomMap(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info("Initialization of map successful :  profileMap,size="+profileMap.size()
				+";profileSetMap size="+profileMap.size()
				+"; deviceMap, size="+deviceMap.size()
				+"; roomMap, size="+roomMap.size()
				);
		log.info("Initialization of Logic control module finished. ");
	}
    
  public static MySqlClass  getMysql(){
	  return mysql;    	
    }
	
	public void decodeCommand(Message msg){		
		int commandID=msg.header.commandID;
			
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
			try {
				get_room_profile(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ROOM_PROFILE:
			try {
				set_room_profile(msg);
			} catch (JSONException | SQLException | ParseException e) {
				e.printStackTrace();
			} 
			break;	
		case DELETE_ROOM_PROFILE:
			try {
				delete_room_profile(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;
		case SWITCH_ROOM_PROFILE:	
			try {
				switch_room_profile(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case GET_RROFILE_SET:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_RROFILE_SET:	
			try {
				set_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;	
		case DELETE_RROFILE_SET:
			try {
				delete_room_profile(msg);
			} catch (JSONException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case SWITCH_RROFILE_SET:	
			try {
				switch_profile_set(msg);
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			break;
		case GET_ONE_DEVICE:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ONE_DEVICE:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;	
		case DELETE_ONE_DEVICE:
			try {
				delete_one_device(msg);
			} catch (JSONException | SQLException e) {
				e.printStackTrace();
			}
			break;			
		case SWITCH_DEVICE_STATE:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case WARNING_MSG:	
			try {
				get_profile_set(msg);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		default:
			int sender=0;
			if(msg.json.has("sender")){
				   try {
					sender=msg.json.getInt("sender");
	    			msg.json.put("sender",2);
	    			msg.json.put("receiver",sender); 
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if(msg.isValid()){
				log.info("Valid command receive,but commandID not encoded.SequeeceID:"+msg.cookie+" command ID :"+msg.header.commandID);
				try {
					msg.json.put("errorCode", LogicControl.COMMAND_NOT_ENCODED);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            }else{
            	log.info("Invalid command receive. SequeeceID:"+msg.cookie+" command ID :"+msg.header.commandID);
            	try {
					msg.json.put("errorCode", LogicControl.WRONG_COMMAND);
				} catch (JSONException  e) {
					e.printStackTrace();
				}            
            }
			msg.header.commandID+= LogicControl.COMMAND_ACK_OFFSET;
			try {
				CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}		
	}
	
    /*** �����ѯ�龰ģʽ
     * <pre>�����json��ʽΪ��
     * { 
     *   sender:    �п�:0 ; �ֻ�:1 ; ��:2; 3:������; 4 ��Ϣ����; ...
     *   receiver:  �п�:0 ; �ֻ�:1 ; ��:2; 3:������; 4 ��Ϣ����; ...
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message ��json��ʽ��
     *   ��1�������ѯ���龰ģʽ�����ڣ�����jason�� {"errorCode": XXXX}
     *   ��2�������ѯ���龰ģʽ���ڣ��򷵻�:
     *  { 
     *  errorCode:SUCCESS,
     *   sender:    �п�:0 ; �ֻ�:1 ; ��:2; 3:������; 4 ��Ϣ����; ...
     *   receiver:  �п�:0 ; �ֻ�:1 ; ��:2; 3:������; 4 ��Ϣ����; ...
     *   CtrolID:1234567,
     *   profileID:7654321,
     *   profile: 
     *         {
     *          �龰ģʽ��json��ʽ 
     *         }
     * }
     *                      
     */
    public void get_room_profile(Message msg) throws JSONException, SQLException{
    	//JSONObject json=new jsson;
    	Profile profile=null;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileID=msg.json.getInt("profileID");
    	int sender=0;
    	String key=CtrolID+"_"+profileID;
    	if( (profile= profileMap.get(key))!=null  || (profile=Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){
    		msg.json.put("profile", profile.toJsonObj());
    		msg.json.put("errorCode",SUCCESS);
    	}else {
			log.warn("Can't get_room_profile CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			//msg.json=new JSONObject();
			msg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID= GET_ROOM_PROFILE_ACK;
		msg.json.put("sender",2);
		if(msg.json.has("sender")){
		   sender=msg.json.getInt("sender");
		}
		msg.json.put("receiver",sender);  
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
     
    /*** ��������ϴ�һ���龰ģʽ
     *<pre> @throws JSONException 
     * @throws SQLException 
     * @return message��json��ʽ:
     *  (1)����ƶ˲����ڸ��龰ģʽ��ֱ�ӱ��棬����json: {"errorCode":SUCCESS}��
     *  (2)����ϴ���profile���޸�ʱ�������ƶˣ����ϱ���profile���������ݿ⣬����{"errorCode":SUCCESS}��
     *  (2)����ϴ���profile���޸�ʱ�������ƶˣ�����Ҫ���ƶ˵��龰ģʽ�·��� �նˣ��ֻ����пأ�,����{"errorCode":OBSOLTE_PROFILE}  ��     *         
     *@param message �����json��ʽΪ�� ��Ҫ�ϴ����߱����prifile��json��ʽ��
     * {
     *  "senderRole":    �п�:0 ; �ֻ�:1 ; ��:2;
     *  "receiverRole":  �п�:0 ; �ֻ�:1 ; ��:2;
     *  profile:
     *   {
			"profileID":123456789,
			"CtrolID":12345677,
			"profileName":"δ֪�龰",
			"profileSetID":12345,
			"profileTemplateID":0,
			"roomID":203,
			"roomType":2,
			"factorList":
			[
				{"factorID":40,"minValue":20,"compareWay":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"compareWay":0,"modifyTime":"2014-12-13 14:15:17","validFlag":false,
				"createTime":"2014-12-13 14:15:17","maxValue":1
				}
			],
			"modifyTime":"2014-12-13 14:15:17",
			"createTime":"2014-12-13 14:15:17"
		}
	  }
     * @throws ParseException 
	*/
    public void set_room_profile( Message msg) throws JSONException, SQLException, ParseException{
    	//JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Profile msgProfile=new Profile(msg.json.getJSONObject("profile"));
    	Profile dbProfile;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileID=msg.json.getInt("profileID");
    	Date msgModifyTime=sdf.parse(msg.json.getString("modifyTime"));
    	String key=CtrolID+"_"+profileID;
    	int sender=0;
    	
    	if((dbProfile=this.profileMap.get(key))==null && (dbProfile=Profile.getOneProfileFromDB(mysql, CtrolID, profileID))==null){
			msg.json.put("errorCode",PROFILE_NOT_EXIST);    		
    	}else if(  dbProfile.modifyTime.after(msgModifyTime)){	//�ƶ˽���  
			msg.json.put("errorCode",PROFILE_OBSOLETE);    		
    	}else if(  dbProfile.modifyTime.before(msgModifyTime)){ //�ƶ˽Ͼɣ��򱣴�
    		this.profileMap.put(key, msgProfile);
			msg.json=new JSONObject();
			msg.json.put("errorCode",SUCCESS);   
			}    	
  		msg.header.commandID=SET_ROOM_PROFILE_ACK;
		msg.json.put("sender",2);
		if(msg.json.has("sender")){
		   sender=msg.json.getInt("sender");
		}
		msg.json.put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}     	
    }
    
    /*** ɾ���龰ģʽ
     * <pre>�����json��ʽΪ��
     * { 
     *   senderRole:    �п�:0 ; �ֻ�:1 ; ��:2;
     *   receiverRole:  �п�:0 ; �ֻ�:1 ; ��:2;
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message ��json��ʽ��
     *   ��1�������ѯ���龰ģʽ�����ڣ�����jason�� {"errorCode":-50002}
           
     */
    public void delete_room_profile(Message msg) throws JSONException, SQLException{
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileID=msg.json.getInt("profileID");
    	String key=CtrolID+"_"+profileID;
    	int sender=0;
		if(msg.json.has("sender")){
		   sender=msg.json.getInt("sender");
		}
    	if(profileMap.containsKey(key)){
    		profileMap.remove(key);
    		msg.json=new JSONObject();
    		msg.json.put("errorCode", SUCCESS);    		
    	}else if((Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){
    		Profile.deleteProfileFromDB(mysql, CtrolID, profileID);
    		msg.json=new JSONObject();
    		msg.json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profile not exist CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			//msg.json=new JSONObject();
			msg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID= DELETE_ROOM_PROFILE_ACK;

		msg.json.put("sender",2);
		msg.json.put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
    
    /*** �����л��龰ģʽ,��������ķ��ͷ��в�ͬ����Ӧ��ʽ
     * <pre>�����json��ʽΪ��
    * { 
     *   sender:    �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:4; ...
     *   receiver:  �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
    *   CtrolID:1234567
    *   roomID: 203
    *   profileID:7654321
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile(final Message msg)throws JSONException, SQLException, InterruptedException{
    	Message replyMsg=new Message(msg);
    	//JSONObject json=msg.json;
    	Profile profile=null;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileID=msg.json.getInt("profileID");
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	String key=CtrolID+"_"+profileID;
    	if((profile= profileMap.get(key))!=null || (profile=Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){
    		jedis.publish(commandQueue, profile.toJsonObj().toString());
    		jedis.hset(currentProfile, key, profile.toJsonObj().toString());
    		if(sender==0){
	    		replyMsg.json=new JSONObject();
	    		replyMsg.json.put("errorCode",SUCCESS);
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();   			
    		}
    	}else {
			log.warn("Can't switch room profile,profile doesn't exit. CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			replyMsg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID= SWITCH_ROOM_PROFILE_ACK;
		replyMsg.json.put("sender",2);
		replyMsg.json.put("receiver",0);
    	CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
    }
    
    /*** �����л��龰ģʽ,����ֵ
     * <pre>�����json��ʽΪ��
    * { 
     *   sender:    �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:4; ...
     *   receiver:  �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
         errorCode: SUCCESS/ PROFILE_NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(10,msg);
		to.start();
    }
    
    /*** ��ѯ�龰ģʽ��
     * <pre>�����json��ʽΪ��
     * { 
     *   sender:    �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:4; ...
     *   receiver:  �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message ��json��ʽ��
     *   ��1�������ѯ���龰ģʽ�����ڣ�����jason�� {"errorCode":-50004}
     *   ��2�������ѯ���龰ģʽ�����ڣ��򷵻�:
     *  { 
     *   errorCode:SUCCESS,
     *   sender:    �п�:0 ; �ֻ�:1 ; ��:2; 3:������; 4 ��Ϣ����; ...
     *   receiver:  �п�:0 ; �ֻ�:1 ; ��:2; 3:������; 4 ��Ϣ����; ...
     *   CtrolID:1234567,
     *   profileSetID:7654321,
     *   profile: 
     *         {
     *          �龰ģʽ����json��ʽ 
     *         }
     * }              
     */
    public void get_profile_set(Message msg) throws JSONException, SQLException{
    	//JSONObject json=msg.json;
    	ProfileSet profileSet=null;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileSetID=msg.json.getInt("profileSetID");
    	String key=CtrolID+"_"+profileSetID;
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	if((profileSet=profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, CtrolID, profileSetID))!=null){
    		msg.json.put("profileSet", profileSet.toJsonObj());
    		msg.json.put("errorCode",SUCCESS);   
    	}else {
			log.warn("Can't get_profile_set, CtrolID:"+CtrolID+" profileSetID:"+profileSetID+" from profileMap or Mysql.");
			msg.json.put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.header.commandID=  GET_RROFILE_SET_ACK;
		msg.json.put("sender",2);
		msg.json.put("receiver",sender);
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
    

	
	/*** ���� �龰ģʽ��

	 * <pre>Json��ʽ�� �����龰ģʽ �� {@link control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} ���ƣ�

     * { 
     *  "senderRole":    �п�:0 ; �ֻ�:1 ; ��:2;
     *  "receiverRole":  �п�:0 ; �ֻ�:1 ; ��:2; 
     *   profileSet:
     *     {  
     *      �龰ģʽ�� ��json��ʽ ��������龰ģʽ��ɵ�json����    
     *     }  
     * }
	 * */
	public void set_profile_set(Message msg) throws JSONException, SQLException, ParseException{
    	//JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	ProfileSet msgProfileSet=new ProfileSet(msg.json.getJSONObject("profileSet"));
    	ProfileSet dbProfileSet;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileSetID=msg.json.getInt("profileSetID");
    	Date msgModifyTime=sdf.parse(msg.json.getString("modifyTime"));
    	String key=CtrolID+"_"+profileSetID;
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	
    	if((dbProfileSet=profileSetMap.get(key))==null && (dbProfileSet=ProfileSet.getProfileSetFromDB(mysql, CtrolID, profileSetID))==null ){
			msg.json.put("errorCode",PROFILE_SET_NOT_EXIST);     		
    	}else if( dbProfileSet.modifyTime.after(msgModifyTime)){	//�ƶ˽���  
			msg.json.put("errorCode",PROFILE_SET_OBSOLETE);    		
    	}else if( dbProfileSet.modifyTime.before(msgModifyTime)){
    		profileSetMap.put(key, msgProfileSet);
			msg.json=new JSONObject();
			msg.json.put("errorCode",SUCCESS);   		
    	}    	
  		msg.header.commandID= SET_RROFILE_SET_ACK;
		msg.json.put("sender",2);
		msg.json.put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 	
	}
	
    /*** ɾ���龰ģʽ��
     * <pre>�����json��ʽΪ��
     * { 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
     * @throws JSONException 
     * @return message ��json��ʽ��
     *   ��1�������ѯ���龰ģʽ�����ڣ�����jason�� {"errorCode":-50002}
     *   ��2�������ѯ���龰ģʽ���ڣ��򷵻��龰ģʽ��json��ʽ                  
     */
    public void delete_profile_set(Message msg) throws JSONException, SQLException{
    	//JSONObject json=msg.json;
    	//Profile profile=null;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileSetID=msg.json.getInt("profileSetID");
    	String key=CtrolID+"_"+profileSetID;
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	if(profileSetMap.containsKey(key)){
    		profileSetMap.remove(key);
    		msg.json=new JSONObject();
    		msg.json.put("errorCode", SUCCESS);    		
    	}else if((ProfileSet.getProfileSetFromDB(mysql, CtrolID, profileSetID))!=null){
    		ProfileSet.deleteProfileSetFromDB(mysql, CtrolID, profileSetID);
    		msg.json=new JSONObject();
    		msg.json.put("errorCode", SUCCESS);
    	}else {
			log.warn("room_profileSet not exist CtrolID:"+CtrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			msg.json.put("errorCode",PROFILE_SET_NOT_EXIST);
    	}
    	msg.header.commandID= DELETE_RROFILE_SET_ACK;
		msg.json.put("sender",2);
		msg.json.put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	

	
	/*** �龰ģʽ���л� 
	 * 	 <pre>��Ӧjson��Ϣ��Ϊ��
	 *   {
	 *     senderRole:"control"/"mobile"/"cloud"
	 *     CtrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	public void switch_profile_set(Message msg)throws JSONException, SQLException, InterruptedException{
    	Message replyMsg=new Message(msg);
    	//Message sendMsg=new Message(msg);
    	JSONObject json=msg.json;
    	ProfileSet profileSet=null;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int profileSetID=msg.json.getInt("profileSetID"); 
    	int sender=0;
    	if(json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}     	
    	String key=CtrolID+"_"+profileSetID;
    	if((profileSet= profileSetMap.get(key))!=null || (profileSet=ProfileSet.getProfileSetFromDB(mysql, CtrolID, profileSetID))!=null){
    		jedis.publish(commandQueue, profileSet.toJsonObj().toString());
    		jedis.hset(currentProfileSet, key, profileSet.toJsonObj().toString());
    		if(sender==0){
	    		replyMsg.json=new JSONObject();
	    		replyMsg.json.put("errorCode",SUCCESS);
  	    		
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();  				
    		}
    	}else {
			log.warn("Can't switch room profileSet,profileSet doesn't exit. CtrolID:"+CtrolID+" profileSetID:"+profileSetID+" from profileSetMap or Mysql.");
			replyMsg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID= SWITCH_RROFILE_SET_ACK;
		replyMsg.json.put("sender",2);
		replyMsg.json.put("receiver",sender);
		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		
	}
	
    /*** �����л��龰ģʽ��,����ֵ
     * <pre>�����json��ʽΪ��
    * { 
     *   sender:    �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:4; ...
     *   receiver:  �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_RECEIVER  /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_profile_set_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(10,msg);
		to.start();
    }
    
	/*** ��ȡһ���豸
	 * 	 <pre>��Ӧjson��Ϣ��Ϊ��
	 *   {
	 *     CtrolID:1234567
	 *     deviceID:
     *   }
     *   @return List< Device > �ӵ��б� ��json��ʽ
	 * @throws JSONException 
     *   */
	public void get_one_device(Message msg) throws JSONException{
    	//JSONObject json=msg.json;
    	Device device=new Device();
    	int CtrolID=msg.json.getInt("CtrolID");
    	int deviceID=msg.json.getInt("deviceID");
    	String key=CtrolID+"_"+deviceID;
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	if( (device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, CtrolID, deviceID))!=null){
    		msg.json.put("device", device.toJsonObj());
    		msg.json.put("errorCode",SUCCESS);   
    	}else {
			log.warn("Can't get_one_device, CtrolID:"+CtrolID+"deviceID: "+ deviceID+" from deviceMap or Mysql.");
			msg.json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.header.commandID=  GET_ONE_DEVICE_ACK;
		msg.json.put("sender",2);
		msg.json.put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	
	/*** ���� һ���ҵ�
	 * 	 <pre>��Ӧ��jsonArray:* 
	 *   {
	 *     ������ҵ��jsonObject��ʽ
     *   }
	 * @throws JSONException 
	 * @throws ParseException */
	public void set_one_device(Message msg,MySqlClass mysql) throws JSONException, ParseException{
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Device msgDevice=new Device(msg.json);
    	Device dbDevice;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int deviceID=msg.json.getInt("deviceID");
    	Date msgModifyTime=sdf.parse(msg.json.getString("modifyTime"));
    	String key=CtrolID+"_"+deviceID;
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	
    	if((dbDevice=this.deviceMap.get(key))==null  && (dbDevice=Device.getOneDeviceFromDB(mysql, CtrolID, deviceID))==null ){	
    		msg.json.put("errorCode",DEVICE_NOT_EXIST);    		
    	}else if(dbDevice.modifyTime.after(msgModifyTime)){ ////�ƶ˽���  
			msg.json.put("errorCode",DEVICE_OBSOLETE);   
		}else if (dbDevice.modifyTime.before(msgModifyTime)){ //�ƶ˽Ͼ�
    		this.deviceMap.put(key, msgDevice);
    		msg.json=new JSONObject();
			msg.json.put("errorCode",SUCCESS); 
		}
  		msg.header.commandID=SET_ONE_DEVICE_ACK;
		msg.json.put("sender",2);
		msg.json.put("receiver",sender); 
    	try {
    		CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 		
	}
	
    /*** ɾ���龰ģʽ��
     * <pre>�����json��ʽΪ��
     * { 
     *   CtrolID:1234567
     *   deviceID:7654321
     * }
     * @throws JSONException 
     * @return message ��json��ʽ��
     *   ��1�������ѯ���龰ģʽ�����ڣ�����jason�� {"errorCode":-50002}
     *   ��2�������ѯ���龰ģʽ���ڣ��򷵻��龰ģʽ��json��ʽ                  
     */
    public void delete_one_device(Message msg) throws JSONException, SQLException{
    	//JSONObject json=msg.json;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int deviceID=msg.json.getInt("deviceID");
    	String key=CtrolID+"_"+deviceID;
    	Device device=null;
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	if((device=deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, CtrolID, deviceID))!=null){
    		deviceMap.remove(key);
    		msg.json=new JSONObject();
    		msg.json.put("errorCode", SUCCESS);    		
    	}else {
			log.warn("room_device not exist CtrolID:"+CtrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			msg.json.put("errorCode",DEVICE_NOT_EXIST);
    	}
    	msg.header.commandID=  DELETE_ONE_DEVICE_ACK;
		msg.json.put("sender",2);
		msg.json.put("receiver",sender); 
    	try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}    	
    }
	
	/*** �л�ĳ���ҵ�״̬
	 * 	 <pre>�����Ӧjson��Ϣ�����¸�ʽ ��
	 *   {
         sender:    �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
     *   receiver:  �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
	 *     senderRole:"control"/"mobile"/"cloud"
	 *     CtrolID:1234567
	 *     deviceID:7654321
     *   }
	 * @throws JSONException 
	 * @throws SQLException */
	public void switch_app_state(Message msg) throws JSONException, SQLException{
	   	Message replyMsg=new Message(msg);
    	Device device=null;
    	int CtrolID=msg.json.getInt("CtrolID");
    	int deviceID=msg.json.getInt("deviceID");
    	int sender=0;
    	if(msg.json.has("sender")){
    		sender=msg.json.getInt("sender"); 
    	}
    	String key=CtrolID+"_"+deviceID;
    	if((device= deviceMap.get(key))!=null || (device=Device.getOneDeviceFromDB(mysql, CtrolID, deviceID))!=null){
    		jedis.publish(commandQueue, device.toJsonObj().toString());
    		jedis.hset(currentProfile, key, device.toJsonObj().toString());
    		if(sender==0){
	    		replyMsg.json=new JSONObject();
	    		replyMsg.json.put("errorCode",SUCCESS); 	    		
    		}else {
    			TimeOutTread to=new TimeOutTread(10,msg);
    			to.start();   			
    		}
    	}else {
			log.warn("Can't switch room device,device doesn't exit. CtrolID:"+CtrolID+" deviceID:"+deviceID+" from deviceMap or Mysql.");
			replyMsg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID=SWITCH_DEVICE_STATE_ACK;
    	replyMsg.json.put("sender",2);
    	replyMsg.json.put("receiver",sender); 
		try {
			CtrolSocketServer.sendCommandQueue.offer(msg, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
    /*** �����л�ĳ���ҵ�״̬,����ֵ
     * <pre>�����json��ʽΪ��
    * { 
     *   sender:    �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:4; ...
     *   receiver:  �п�:0;  �ֻ�:1;  ��:2;  web:3;  ������:4;  ��Ϣ����:5; ...
         errorCode: SUCCESS/ PROFILE_SET_NOT_EXIST /TIME_OUT /WRONG_COMMAND
    * }
     * @throws InterruptedException 
 	* */
    public void switch_room_profile_set_ack(final Message msg)throws JSONException, SQLException, InterruptedException{
		TimeOutTread to=new TimeOutTread(10,msg);
		to.start();
    }
	
  /*** �澯��Ϣ
  <pre>�����Ӧjson��Ϣ�����¸�ʽ ��
  {
    "warnID",  123456      
    "warnName","����©��"      
    "warnContent", "�����ĳ�����ȼ���峬�꣬���Զ�����򿪳����Ĵ���~"  
    "type",  1  	
    "channel",0      
    "createTime","2014-12-25 12:13:14"    
    "modifyTime","2014-12-25 12:13:14"  
  }
  */
	public void warn(){
		
	}

    

	public static void main(String[] args) {		
		LogicControl lc= new LogicControl();
	}	
	
}
