package control;

import org.json.JSONException;
import org.json.JSONObject;

import device.Profile;
import socket.Message;
import socket.ServerThread;

public class LogicControl {	
    /*** ���� �龰ģʽ����
     *<pre>json��ʽΪ��
     * { 
     *   comand:GET_ROOM_PROFILE 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
     *   */
	private static final short GET_ROOM_PROFILE			=	0x1601;
	
	
    /*** ���� �龰ģʽ
     *<PRE>Json��ʽΪ��Ҫ�ϴ����߱�����龰ģʽ�� json��ʽ��
     *  {
			"profileID":123456789,
			"CtrolID":12345677,
			"profileName":"δ֪�龰",
			"profileSetID":12345,
			"profileTemplateID":0,
			"roomID":203,
			"roomType":2
			"factorList":
			[
				{"factorID":40,"minValue":20,"compareWay":0,"modifyTime":"Fri Dec 12 12:30:00 CST 2014","validFlag":false,
				"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":30
				},
	
				{"factorID":60,"minValue":1,"compareWay":0,
				"modifyTime":"Fri Dec 12 12:30:00 CST 2014","validFlag":false,"createTime":"Fri Dec 12 12:30:00 CST 2014","maxValue":1
				}
			],
			"modifyTime":"Sat Dec 13 14:15:17 CST 2014",
			"createTime":"Sat Dec 13 14:15:16 CST 2014",
		}
     */
	private static final short SET_ROOM_PROFILE			=	0x1602;
	
	/*** �л��龰ģʽ���� 
      <pre>json��ʽ������Ϊ��
     * { 
     *   comand:SWITCH_ROOM_PROFILE 
     *   CtrolID:1234567
     *   profileID:7654321
     * }
	 * */
	private static final short SWITCH_ROOM_PROFILE		=	0x1603;
	
	/*** ���� �龰ģʽ�� 
     *json��ʽ������Ϊ�� <pre>
     * { 
     *   GET_RROFILE_SET 
     *   CtrolID:1234567
     *   profileSetID:7654321
     * }
	 * */	
	private static final short GET_RROFILE_SET			=	0x1701;
	
	/*** ���� �龰ģʽ��
	 * <pre>Json��ʽ�� �����龰ģʽ �� {@link control.LogicControl#SET_ROOM_RROFILE SET_ROOM_RROFILE} ���ƣ�
     * {  
     *   [  
     *   �龰ģʽ�� ��json��ʽ ��������龰ģʽ��ɵ�json����    
     *   ]  
     * }
	 * */
	private static final short SET_RROFILE_SET			=	0x1702;
	
	/*** �龰ģʽ���л� 
	 * 	 <pre>��Ӧjson��Ϣ��Ϊ��
	 *   {
	 *     comand:SWITCH_RROFILE_SET 
	 *     CtrolID:1234567
	 *     profileSetID:7654321
     *   }*/
	private static final short SWITCH_RROFILE_SET		=	0x1703;	
	
	/*** ����ҵ��б�
	 * 	 <pre>��Ӧjson��Ϣ��Ϊ��
	 *   {
	 *     comand:GET_APP_LIST 
	 *     CtrolID:1234567
     *   }*/
	private static final short GET_APP_LIST				=	0x1801;
	
	
	/*** ���� �ҵ��б�
	 * 	 <pre>��Ӧ�ģ�
	 *   {
	 *     ��ÿһ���ҵ�תһ��json���󣬽��������ݶ���ҵ����һ��json����
     *   }*/
	private static final short SET_APP_LIST				=	0x1802;
	
	
	/*** �л�ĳ���ҵ�״̬
	 * 	 <pre>�����Ӧjson��Ϣ�����¸�ʽ ��
	 *   {
	 *     comand:SWITCH_APP_STATE
	 *     CtrolID:1234567
	 *     deviceID:7654321
     *   }*/
	private static final short SWITCH_APP_STATE		    =	0x1803;
	
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
	private static final short WARNING_MSG				=	0x1901;
	private static final short EMERGENCY				=	0x1902;
	

	
	public void decodeCommand(Message msg){		
		int commandID=msg.header.commandID;
		//JSONObject json=msg.json;
		
		switch (commandID)
		{
		case GET_ROOM_PROFILE:			
		
		case SET_ROOM_PROFILE:	
			
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
	
	public JSONObject getJason(String inComm,int len){			
		if(inComm==null) return null;
		String msgBody=inComm.substring(len);
		JSONObject jo;
		try {
			jo = new JSONObject(msgBody);
			return jo;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	


	public static void main(String[] args) {
		
		System.out.println(GET_APP_LIST+"!");
		
		if(GET_APP_LIST==6145){
			System.out.println("ture");
		}

	}	
	
}
