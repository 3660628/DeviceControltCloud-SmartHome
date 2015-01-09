package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;

import control.LogicControl;
import util.BytesUtil;


public class ServerThread extends Thread  {
    Socket clientRequest;// �û����ӵ�ͨ���׽���  
    BufferedReader input;// ������  
    PrintWriter output;// �����  
    Message msg = null;
    String str = null;  

    
    public static Logger log = Logger.getLogger(CtrolSocketServer.class);   
    
    
  
    // serverThread�Ĺ�����  
    public ServerThread(Socket s)  
    {  
        this.clientRequest = s;  
        // ����receiveServer�������׽��� 

        OutputStreamWriter writer;  
        InputStreamReader reader;  
        try  
        { // ��ʼ�����롢�����              
            writer = new OutputStreamWriter(clientRequest.getOutputStream());   
            output = new PrintWriter(writer, true); 
            reader = new InputStreamReader(clientRequest.getInputStream(),"utf-8");  
            input = new BufferedReader(reader); 
            //dataOut=new DataOutputStream(clientRequest.getOutputStream());
        } catch (IOException e)  
        {  
            System.out.println(e.getMessage());  
        }  
        output.println("Welcome to DeviceControl server!"); 
        output.flush();
        // �ͻ������ӻ�ӭ��  
    }  
  
    @Override 
    public void run()  
    { // �̵߳�ִ�з���  

        while (true)  
        { 
        	if(!CtrolSocketServer.sendCommandQueue.isEmpty()){
        		Message outMsg=null;
				try {
					outMsg = CtrolSocketServer.sendCommandQueue.poll(100, TimeUnit.MICROSECONDS);
					outMsg.writeToSock(clientRequest);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}       		     		
        	}
        	
            try {
				msg= readFromClient();
				//readTest();
			} catch (UnsupportedEncodingException | JSONException e) {
				e.printStackTrace();
			} 
            if(msg.isValid()){
            	if(CtrolSocketServer.receiveCommandQueue.offer(msg)==false){
            		log.error("can't add message to the receiving queue. SequeeceID:"+msg.header.sequeeceNo);
            	}
            }else{
            	log.info("Invalid command receive. SequeeceID:"+msg.header.sequeeceNo+" command ID :"+msg.header.commandID);
            	Message errMsg=msg;
            	errMsg.header.commandID+= LogicControl.COMMAND_ACK_OFFSET;
            	errMsg.json=new JSONObject();
            	try {
					errMsg.json.put("errorCode", LogicControl.WRONG_COMMAND);
					CtrolSocketServer.sendCommandQueue.offer(errMsg, 100, TimeUnit.MICROSECONDS);
				} catch (JSONException | InterruptedException e) {
					e.printStackTrace();
				}
            	//CtrolSocketServer.sendCommandQueue.add(errMsg);
            }
        }   
    } 
    
/*	private void readTest() 
    {  
		byte[] b2=new byte[50]; 
		try {
			clientRequest.getInputStream().read(b2,0,2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("B2: "+b2[0]+","+b2[1]);
		//byte[] b2={b2[0],b2[1]};
		System.out.println("B2 int: "+BytesUtil.getShort(b2) );
		System.out.println("B2 int: "+BytesUtil.bytesToShort(b2) );
		System.out.println("String:"+new String(b2));
		
		byte[] b50=new byte[50]; 
		try {
			//clientRequest.getInputStream().read(b50,0,50);
			System.out.println(new String(b2,"UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
    }*/

	private Message readFromClient() throws UnsupportedEncodingException, JSONException    
    {  
		byte[] b23=new byte[23]; 
		Header head=new Header();
		Message msg=new Message();
    	try {
			clientRequest.getInputStream().read(b23,0,23);
			head=new Header(b23);
			head.printHeader();
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress());  
		}
    	
    	
    	byte[] cookie=new byte[head.cookieLen];
    	try {
			clientRequest.getInputStream().read(cookie,0,head.cookieLen);
		} catch (IOException e) {			
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
		}
    	//int cookieInt=BytesUtil.getInt(cookie);
    	String cookieStr=new String(cookie);
    	System.out.println("cookie: "+cookieStr);    
    	
    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
    	try {
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
			System.out.println("error:exception happened,connection from "+clientRequest.getInetAddress().getHostAddress() + " has been closed!");
		}  
    	String comString=new String(commnad);
    	System.out.println("command: "+comString);
    	msg=new Message(head, cookieStr, comString);
        return msg; 
    } 
    

    
    
/*    
 private void validateCommand(Message msg) {
    	String command=null; 
    	if(str==null) return;
        command = str.trim().toUpperCase();  
        if (command.equals("HELP"))  
        {  
            // ����help��ѯ���������ɽ��ܵ����� 
        	output.println("This is DeviseControl server,only following command can be accepted:");
            output.println("SynRoomProfile");  
            output.println("SwitchToRoomProfile");  
            output.println("SynApplianceList");  
            output.println("SwitchApplicanceState");  
        } else if (command.startsWith("QUERY"))  
        { // ����query  
            output.println("Command Accept !");  
            try {
            	receiveCommandQueue.put(command);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }  
        // else if ����.. //�ڴ˿ɼ��������������ָ��  
        else if (!command.startsWith("HELP") && !command.startsWith("QUIT")  
                && !command.startsWith("QUERY"))  
        {  
            output.println("Invalid Command ! Please key in HELP to inquire all valid command !");  
        }    	
    } 
    
    
    private String readFromClient2()  
    {        
        try  
        {  
        	str = input.readLine(); 
            return str;  
        }  
        //�����׽���쳣��������Socket��Ӧ�Ŀͻ����Ѿ��ر�  
        catch (IOException e)  
        {  
            //ɾ����Socket��  
            CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress());    //�� 
            //System.out.println("error:exception happened,connection from "+clientRequest.getInetAddress().getHostAddress() + " has been closed!");  
        }  
        return null;  
    } */
      
}  


