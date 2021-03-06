--情景因素配置表dic_st_factor
insert into dic_st_factor
(
 factorid      ,
 factortype    ,
 factorname    ,
 measurement   ,
 mstype        ,
 createoperator,
 modifyoperator,
 createtime    ,
 modifytime    
)
 values
(10,0,'灯           ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(20,0,'电视         ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(40,0,'空调         ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(41,0,'空调开关     ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(42,0,'空调温度     ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(43,0,'空调风速     ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(60,0,'窗户         ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(80,0,'窗帘         ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(90,0,'暖器         ','开关',1,      14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(201,1,'光          ','lux',1,       14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(301,1,'PM2.5       ','lux',1,       14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(401,1,'有害气体    ','lux',1,       14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(501,1,'湿度        ','centigrid',1, 14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(601,1,'温度        ','centigrid',1, 14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(701,1,'天气（预报）','weather',1,   14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00'),
(901,1,'声音        ','DB',1,        14,14,'2015-01-19 16:34:00','2015-01-19 16:34:00');