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

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import device.Profile;
import device.ProfileMap;
import socket.CtrolSocketServer;
import socket.Message;
import util.MySqlClass;

public class LogicControl {	
	
	private static final short COMMAND_START            =  0x1600;
	private static final short COMMAND_ACK_OFFSET       =  0x4000; 
	
    /*** ���� �龰ģʽ����    @see get_room_profile() */
	private static final short GET_ROOM_PROFILE					=	COMMAND_START+1;	
    /*** ���� �龰ģʽ�Ļظ�    @see get_room_profile_ack() */
	private static final short GET_ROOM_PROFILE_ACK     		=   COMMAND_START+1 + COMMAND_ACK_OFFSET;
	
    /*** ���� �龰ģʽ   @see set_room_profile()  */
	private static final short SET_ROOM_PROFILE					=	COMMAND_START+2;	
    /*** ���� �龰ģʽ�Ļظ�   @see set_room_profile_ack()  */
	private static final short SET_ROOM_PROFILE_ACK	    		=	COMMAND_START+2+COMMAND_ACK_OFFSET;
	
	/*** �п��л��龰ģʽ���� */
	private static final short CONTROL_SWITCH_ROOM_PROFILE		=	COMMAND_START+3;
	/*** �п��л��龰ģʽ���� �Ļظ� */
	private static final short CONTROL_SWITCH_ROOM_PROFILE_ACK	=	COMMAND_START+3+COMMAND_ACK_OFFSET;

	
	/*** �п��л��龰ģʽ���� */
	private static final short MOBILE_SWITCH_ROOM_PROFILE		=	COMMAND_START+4;
	/*** �ֻ��龰ģʽ���� �Ļظ� */
	private static final short MOBILE_SWITCH_ROOM_PROFILE_ACK	=	COMMAND_START+4+COMMAND_ACK_OFFSET;
	
	
	/*** ���� �龰ģʽ�� */	
	private static final short GET_RROFILE_SET					=	COMMAND_START+21;
	/*** ���� �龰ģʽ�� �Ļظ�*/	
	private static final short GET_RROFILE_SET_ACK				=	COMMAND_START+21+COMMAND_ACK_OFFSET;
	
	/*** ���� �龰ģʽ��*/
	private static final short SET_RROFILE_SET					=	COMMAND_START+22;
	/*** ���� �龰ģʽ�� �Ļظ�*/
	private static final short SET_RROFILE_SET_ACK				=	COMMAND_START+22+COMMAND_ACK_OFFSET;
	
	/*** �龰ģʽ���л� */
	private static final short CONTROL_SWITCH_RROFILE_SET		=	COMMAND_START+23;	
	/*** �龰ģʽ���л� �Ļظ�*/
	private static final short CONTROL_SWITCH_RROFILE_SET_ACK	=	COMMAND_START+23+COMMAND_ACK_OFFSET;
	
	/*** �龰ģʽ���л� */
	private static final short MOBILE_SWITCH_RROFILE_SET		=	COMMAND_START+24;	
	/*** �龰ģʽ���л� �Ļظ�*/
	private static final short MOBILE_SWITCH_RROFILE_SET_ACK	=	COMMAND_START+24+COMMAND_ACK_OFFSET;
	
	/*** ����ҵ��б�*/
	private static final short GET_APP_LIST						=	COMMAND_START+41;
	/*** ����ҵ��б� �Ļظ�*/
	private static final short GET_APP_LIST_ACK					=	COMMAND_START+41+COMMAND_ACK_OFFSET;	
	
	/*** ���� �ҵ��б�*/
	private static final short SET_APP_LIST						=	COMMAND_START+42;
	/*** ���� �ҵ��б� �Ļظ�*/
	private static final short SET_APP_LIST_ACK					=	COMMAND_START+42+COMMAND_ACK_OFFSET;	
	
	/*** �л�ĳ���ҵ�״̬*/
	private static final short CONTROL_SWITCH_APP_STATE		    		 =	COMMAND_START+43;
	/*** �л�ĳ���ҵ�״̬ �Ļظ�*/
	private static final short CONTROL_SWITCH_APP_STATE_ACK		    	=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
	/*** �л�ĳ���ҵ�״̬*/
	private static final short MOBILE_SWITCH_APP_STATE		    		=	COMMAND_START+43;
	/*** �л�ĳ���ҵ�״̬ �Ļظ�*/
	private static final short MOBILE_SWITCH_APP_STATE_ACK		    	=	COMMAND_START+43+COMMAND_ACK_OFFSET;
	
    /*** �澯��Ϣ   */
	private static final short WARNING_MSG				 		=	COMMAND_START+61;
    /*** �澯��Ϣ  �Ļظ�  */
	private static final short WARNING_MSG_ACK				 	=	COMMAND_START+61+COMMAND_ACK_OFFSET;	
	
	
	/***********************  ERROR CODE :-50000  :  -59999 ************************/
	private static final int SUCCESS                  =	0;
	private static final int PROFILE_OBSOLETE         =	-50001;	
	private static final int PROFILE_NOT_EXIST        = -50002;		

	

	
	static Logger log= Logger.getLogger(LogicControl.class);
	MySqlClass mysql=null;
	ProfileMap profileMap =null;
	
    public LogicControl() {
		// TODO Auto-generated constructor stub
	}
    
    public LogicControl(MySqlClass mysql) {
		this.mysql=mysql;
		try {
			this.profileMap= new ProfileMap(mysql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void decodeCommand(Message msg){		
		int commandID=msg.header.commandID;
			
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
			try {
				get_room_profile(msg,mysql);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			break;
		case SET_ROOM_PROFILE:
			try {
				set_room_profile(msg,mysql);
			} catch (JSONException | SQLException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case SWITCH_ROOM_PROFILE:	
			String plate2=new String("utf-8");
		case GET_RROFILE_SET:	
			String plate3=new String("utf-8");
		case SET_RROFILE_SET:	
			String plate12=new String("utf-8");			
		case SWITCH_RROFILE_SET:	
			String plate4=new String("utf-8");
		case GET_APP_LIST:	
			String plate5=new String("utf-8");
		case SET_APP_LIST:	
			String plate11=new String("utf-8");			
		case SWITCH_APP_STATE:	
			String plate6=new String("utf-8");
		case WARNING_MSG:	
			String plate7=new String("utf-8");
		case EMERGENCY:	
			String plate8=new String("utf-8");
			
		}		
	}
	
    /*** ��Map���� MYSQL��ѯ�龰ģʽ
     * <pre>�����json��ʽΪ��
     * { 
     *   comand:GET_ROOM_PROFILE 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     * @throws JSONException 
     * @return message ��json��ʽ��
     *   ��1�������ѯ���龰ģʽ�����ڣ�����jason�� {"errorCode":-50002}
     *   ��2�������ѯ���龰ģʽ���ڣ��򷵻��龰ģʽ��json��ʽ                  
     */
    public void get_room_profile(Message msg,MySqlClass mysql) throws JSONException, SQLException{
    	JSONObject json=msg.json;
    	Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	String key=CtrolID+"_"+profileID;
    	if(profileMap.containsKey(key)){
    		profile= profileMap.get(key);
    		msg.json=profile.toJsonObj();
    	}else if(( profile=Profile.getOneProfileFromDB(mysql, CtrolID, profileID))!=null){
    		msg.json=profile.toJsonObj();;
    	}else {
			log.warn("Can't get_room_profile CtrolID:"+CtrolID+" profileID:"+profileID+" from profileMap or Mysql.");
			msg.json=null;
			msg.json.put("errorCode",PROFILE_NOT_EXIST);
    	}
    	msg.header.commandID+= GET_ROOM_PROFILE_ACK;
    	try {
			CtrolSocketServer.sendCommandQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
     * @throws ParseException 
	*/
    public void set_room_profile(Message msg,MySqlClass mysql) throws JSONException, SQLException, ParseException{
    	JSONObject json=msg.json;
    	DateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Profile profile=null;
    	int CtrolID=json.getInt("CtrolID");
    	int profileID=json.getInt("profileID");
    	Date jsonModifyTime=sdf.parse(json.getString("modifyTime"));
    	String key=CtrolID+"_"+profileID;
    	if(!profileMap.containsKey(key)){
    		profile=new Profile(json);
    		profile.saveProfileToDB(mysql);
    		return true;
    	}else {
    		profile= profileMap.get(key);
    	}
    	
    	if(profile.modifyTime.after(jsonModifyTime)){
    		return false;    		
    	}else{
    		return true;
    	}
    }
	
    
   /***  �п������л��龰ģʽ
    * <pre>�����json��ʽΪ��
   * { 
   *   comand:SWITCH_ROOM_PROFILE 
   *   CtrolID:1234567
   *   profileID:7654321
   * }
	 * */
    public void control_switch_room_profile(){
    	
    }
    
    /*** �ֻ������л��龰ģʽ
     * <pre>�����json��ʽΪ��
    * { 
    *   comand:SWITCH_ROOM_PROFILE 
    *   CtrolID:1234567
    *   profileID:7654321
    * }
 	* */
    public void mobile_switch_room_profile(){
    	
    }
    
	/*** ���� �龰ģʽ�� 
     *json��ʽ������Ϊ�� <pre>
     * { 
     *   GET_RROFILE_SET 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
	 * */	
	public void GET_RROFILE_SET(){
		
	}
	
	/*** ���� �龰ģʽ��
	 * <pre>Json��ʽ�� �����龰ģʽ �� {@link control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} ���ƣ�
     * {  
     *   [  
     *   �龰ģʽ�� ��json��ʽ ��������龰ģʽ��ɵ�json����    
     *   ]  
     * }
	 * */
	public void SET_RROFILE_SET(){
		
	}
	
	/*** �龰ģʽ���л� 
	 * 	 <pre>��Ӧjson��Ϣ��Ϊ��
	 *   {
	 *     comand:SWITCH_RROFILE_SET 
	 *     CtrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	public void SWITCH_RROFILE_SET(){
		
	}
	
	/*** ����ҵ��б�
	 * 	 <pre>��Ӧjson��Ϣ��Ϊ��
	 *   {
	 *     comand:GET_APP_LIST 
	 *     CtrolID:1234567
     *   }*/
	public void GET_APP_LIST(){
		
	}
	
	
	/*** ���� �ҵ��б�
	 * 	 <pre>��Ӧ�ģ�
	 *   {
	 *     ��ÿһ���ҵ�תһ��json���󣬽��������ݶ���ҵ����һ��json����
     *   }*/
	public void SET_APP_LIST()
	
	
	/*** �л�ĳ���ҵ�״̬
	 * 	 <pre>�����Ӧjson��Ϣ�����¸�ʽ ��
	 *   {
	 *     comand:SWITCH_APP_STATE
	 *     CtrolID:1234567
	 *     deviceID:7654321
     *   }*/
	public void SWITCH_APP_STATE()		  
	
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
	public void warn()				=	COMMAND_START+61;

    

	public static void main(String[] args) {		
		System.out.println(0x32FF+"!");

		


	}	
	
}
