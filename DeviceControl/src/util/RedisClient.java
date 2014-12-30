package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;




public class RedisClient {

    private Jedis jedis;//����Ƭ��ͻ�������
    private JedisPool jedisPool;//����Ƭ���ӳ�
    private static ShardedJedis shardedJedis;//��Ƭ��ͻ�������
    private ShardedJedisPool shardedJedisPool;//��Ƭ���ӳ�

    
    public RedisClient() 
    { 
        initialPool(); 
        initialShardedPool(); 
        shardedJedis = shardedJedisPool.getResource(); 
        jedis = jedisPool.getResource();         
    } 
 
    /**
     * ��ʼ������Ƭ��
     */
    private void initialPool() 
    { 
        // �ػ������� 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxActive(20); 
        config.setMaxIdle(5); 
        config.setMaxWait(1000l); 
        config.setTestOnBorrow(false); 
        
        jedisPool = new JedisPool(config,"172.16.35.170",6379);
    }
    
    /** 
     * ��ʼ����Ƭ�� 
     */ 
    private void initialShardedPool() 
    { 
        // �ػ������� 
        JedisPoolConfig config = new JedisPoolConfig(); 
        config.setMaxActive(20); 
        config.setMaxIdle(5); 
        config.setMaxWait(1000l); 
        config.setTestOnBorrow(false); 
        // slave���� 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>(); 
        shards.add(new JedisShardInfo("172.16.35.170", 6379, "master")); 

        // ����� 
        shardedJedisPool = new ShardedJedisPool(config, shards); 
    } 
    
    public void show() {     
        jedisPool.returnResource(jedis);
        shardedJedisPool.returnResource(shardedJedis);
    } 

    public static void main(String[] args) {  
    /*	RedisClient redisClient= new RedisClient();
    	redisClient.show();
       //System.out.println(redisClient.jedis.flushDB()); 
        
        System.out.println("=============��=============");
        System.out.println("hashs�����key001��value001��ֵ�ԣ�"+shardedJedis.hset("hashs", "key001", "value001")); 
        System.out.println("hashs�����key002��value002��ֵ�ԣ�"+shardedJedis.hset("hashs", "key002", "value002")); 
        System.out.println("hashs�����key003��value003��ֵ�ԣ�"+shardedJedis.hset("hashs", "key003", "value003"));
        System.out.println("����key004��4�����ͼ�ֵ�ԣ�"+shardedJedis.hincrBy("hashs", "key004", 4l));
        System.out.println("hashs�е�����ֵ��"+shardedJedis.hvals("hashs"));
        System.out.println();
        
        System.out.println("=============ɾ=============");
        System.out.println("hashs��ɾ��key002��ֵ�ԣ�"+shardedJedis.hdel("hashs", "key002"));
        System.out.println("hashs�е�����ֵ��"+shardedJedis.hvals("hashs"));
        System.out.println();
        
        System.out.println("=============��=============");
        System.out.println("key004���ͼ�ֵ��ֵ����100��"+shardedJedis.hincrBy("hashs", "key004", 100l));
        System.out.println("hashs�е�����ֵ��"+shardedJedis.hvals("hashs"));
        System.out.println();
        
        System.out.println("=============��=============");
        System.out.println("�ж�key003�Ƿ���ڣ�"+shardedJedis.hexists("hashs", "key003"));
        System.out.println("��ȡkey004��Ӧ��ֵ��"+shardedJedis.hget("hashs", "key004"));
        System.out.println("������ȡkey001��key003��Ӧ��ֵ��"+shardedJedis.hmget("hashs", "key001", "key003")); 
        System.out.println("��ȡhashs�����е�key��"+shardedJedis.hkeys("hashs"));
        System.out.println("��ȡhashs�����е�value��"+shardedJedis.hvals("hashs"));
        System.out.println();*/
    	
   	Jedis jedis= new Jedis("172.16.35.170", 6379,200);
//    	jedis.set("richard", "good boy");
//    	jedis.get("richard");
        
        jedis.hset("hashs", "key001", "value001");


    	    	
    }



}