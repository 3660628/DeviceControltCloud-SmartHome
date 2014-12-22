package socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
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

import util.BytesUtil;


public class ServerThread extends Thread  {
    Socket clientRequest;// �û����ӵ�ͨ���׽���  
    BufferedReader input;// ������  
    PrintWriter output;// �����  
    ObjectOutputStream os;
    Message msg = null;
    String str = null;  
    public static BlockingQueue<Message> receiveCommandQueue= new ArrayBlockingQueue<Message>(10000);
    public static BlockingQueue<Message> sendCommandQueue= new ArrayBlockingQueue<Message>(10000);
    
    public static Logger LOG = Logger.getLogger(ServerThread.class);   
    
    
  
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
            reader = new InputStreamReader(this.clientRequest.getInputStream(),"utf-8");  
            input = new BufferedReader(reader); 
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
        	if(!sendCommandQueue.isEmpty()){
        		Message outMsg=null;
				try {
					outMsg = sendCommandQueue.poll(200, TimeUnit.MICROSECONDS);
					os.writeObject(outMsg);
					writeToClient(outMsg.MessageToString());  

				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}       		     		
        	}
        	
            try {
            	msg= readFromClient();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
			//System.out.println("message received from client:"+msgLine);  
			if(msg.isValid()){
				if(receiveCommandQueue.offer(msg))
				receiveCommandQueue.add(msg);
				sendCommandQueue.add(msg);
			}else{
				System.out.println("Error: Cant't add Message to Receive Message Queue ! please confirm the queue have enough capacity!");
			}
  
        }   
    } 
    


	private Message readFromClient() throws UnsupportedEncodingException, JSONException    
    {  
		byte[] b23=new byte[23]; 
		Header head=new Header();
		Message msg=new Message();
    	try {
			clientRequest.getInputStream().read(b23,0,23);
			head=new Header(b23);
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
    	int cookieInt=BytesUtil.getInt(cookie);
    	System.out.println("cookie: "+cookieInt);    
    	
    	byte[] commnad=new byte[head.msgLen-head.cookieLen];
    	try {
			clientRequest.getInputStream().read(commnad,0,head.msgLen-head.cookieLen);
		} catch (IOException e) {
			e.printStackTrace();
			CtrolSocketServer.sockMap.remove(clientRequest.getInetAddress().getHostAddress()); 
		}  
    	String comString=new String(commnad);
    	System.out.println("command: "+comString);
    	msg=new Message(head, cookieInt, comString);
        return msg; 
    } 
    

    
	private void writeToClient(String command)  
    {  
        output.println(command);            
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


