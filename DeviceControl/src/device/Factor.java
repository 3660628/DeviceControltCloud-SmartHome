package device;

import java.util.*;

/*** �龰ģʽ���������龰����*/
public class Factor {
 
	/***<pre>0-10������
	10����
	20������
	40: �յ�

	41: �յ�����
	42���յ��¶�
	43���յ�����

	60������
	80������
	90��ů��

	201����
	301��PM2.5 
	401���к�����
	501��ʪ��
	601���¶�
	701��������Ԥ����
	901������*/
	int factorID;
	//int factorType;
	//String factorName;
	int minValue;
	int maxValue;
	int compareWay;
	int validFlag;
	Date createTime;
	Date modifyTime;
	
	public Factor() {
		// TODO Auto-generated constructor stub
	}

	
	Factor(	int factorID,	
			//int factorType,	
			//String factorName,
			int minValue,
			int maxValue,
			int compareWay,
			int validFlag,
			Date createTime,
			Date modifyTime )
	{
		this.factorID=factorID;
		//this.factorType=factorType;
		//this.factorName=factorName;
		this.minValue=minValue;
		this.maxValue=maxValue;
		this.compareWay=compareWay;
		this.validFlag=validFlag;
		this.createTime=createTime;
		this.modifyTime=modifyTime;		
	}
	
	/*** ��������ʱ��,�����������Ƶĳ�ʼ������*/
	Factor(	int factorID,	
			int factorType,	
			int minValue,
			int maxValue,
			int compareWay,
			int validFlag
			)
	{
		this.factorID=factorID;
		//this.factorType=factorType;
		this.minValue=minValue;
		this.maxValue=maxValue;
		this.compareWay=compareWay;
		this.validFlag=validFlag;	
	}
	

}

