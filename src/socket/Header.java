package socket;

import java.io.UnsupportedEncodingException;

import util.BytesUtil;



public class Header{
	/*** 6�ֽ�  Ĭ�ϣ�#XRPC#*/
	String headTag; 
	/***Э�����汾��*/
    byte mainVersion ;
	/***Э���Ӱ汾��*/
    byte subVersion ;
	/***�����ܳ��ȣ�cookie+��Ϣ��+��β*/
    public short msgLen ;
	/***�����*/
	public short commandID;	
	/***���к�*/
	public int sequeeceNo ;
	/***���ܷ�ʽ: 0������, ����ֵΪԼ���ļ��ܷ�ʽ*/
 	short encType; 
 	/***cookie����,cookie�Ӱ�ͷ����λ�ÿ�ʼ*/
 	public short cookieLen; 
 	/*�����ֶ�*/	
 	int reserve; 
 	
 	private static final int commandMax=0x19FF;
 	private static final int commandMin=0x1600;	

	public Header(){} 
 	Header(Header header){
			this.headTag=header.headTag;			
			this.mainVersion=header.mainVersion;
			this.subVersion=header.subVersion;
			this.msgLen=header.msgLen;
			this.commandID=header.commandID;
			this.sequeeceNo=header.sequeeceNo;
			this.encType=header.encType; 
			this.cookieLen=header.cookieLen;
			this.reserve=header.reserve;			
	 }
 	
 	Header(String headTag,byte mainVersion,byte subVersion,short msgLen,short commandID,int sequeeceNo,short encType,
 			short cookieLen,
 			int reserve ){
		this.headTag=headTag;			
		this.mainVersion=mainVersion;
		this.subVersion=subVersion;
		this.msgLen=msgLen;
		this.commandID=commandID;
		this.sequeeceNo=sequeeceNo;
		this.encType=encType; 
		this.cookieLen=cookieLen;
		this.reserve=reserve;			
 }
	 
	 /***23 bytes of header
	 * @param mainVersoin 
	 * @throws UnsupportedEncodingException */
	 Header(byte[] header) throws UnsupportedEncodingException{   
		     byte[] headTag     ={header[0],header[1],header[2],header[3],header[4],header[5]};	
			 byte mainVersion	=header[6];
			 byte subVersion	=header[7];
			 byte[] msgLen		= {header[8],header[9]};	
			 byte[] commandID	= {header[10],header[11]};
			 byte[] sequeeceNo	={header[12],header[13],header[14],header[15]} ;
			 short   encType     =header[16];
			 byte[] cookieLen	= {header[17],header[18]};
			 byte[] reserve	    ={header[19],header[20],header[21],header[22]} ;
			 
			//����ֽ��� 
			this.headTag=new String(headTag,"UTF-8");;			
			this.mainVersion=mainVersion;
			this.subVersion=subVersion;
			this.msgLen=BytesUtil.getShort(msgLen);
			this.commandID=BytesUtil.getShort(commandID);
			this.sequeeceNo=BytesUtil.getInt(sequeeceNo);
			this.encType=encType; 
			this.cookieLen=BytesUtil.getShort(cookieLen);
			this.reserve=BytesUtil.getInt(reserve);
			 
			 
			/*this.headTag=new String(headTag,"UTF-8");;			
			this.mainVersion=mainVersion;
			this.subVersion=subVersion;
			this.msgLen=Message.bytesToShort(msgLen);
			this.CommandID=Message.bytesToShort(commandID);
			this.sequeeceNo=Message.bytesToInt(sequeeceNo);
			this.encType=encType; 
			this.cookieLen=Message.bytesToShort(cookieLen);
			this.reserve=Message.bytesToInt(reserve);*/
			 

		}
	 
  public  void	 printHeader(){
	  
	  System.out.println(
			  this.headTag    + "\n"+	
			  this.mainVersion+ "\n"+	
			  this.subVersion + "\n"+	
			  this.msgLen     + "\n"+
			  this.commandID  + "\n"+	
			  this.sequeeceNo + "\n"+	
			  this.encType    + "\n"+	
			  this.cookieLen  + "\n"+	
			  this.reserve    + "\n"			  
			  );		 
	 }
  
  public boolean isValid() {   
  	int commandID=this.commandID;
  	if(commandID>=commandMin && commandID<=commandMax)  {	
		return true;
	}
    return false;
  }
  
  public static void main(String[] args){
	  
	  //Header head=new Header("#XRPC#", 1, 2, 15, 0x1601, 12345, 1, 5, 6);
			  
  }
  
}
