package edu.sjsu.cmpe.cache.client;
import java.nio.charset.Charset;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.SortedMap;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;


@SuppressWarnings("unused")
public class Client {
	
	 private static final Funnel<CharSequence> StringFunnl = Funnels.stringFunnel(Charset.defaultCharset());
	 private final static  HashFunction hashFun=Hashing.md5();
	 public static ArrayList<String>nodes=new ArrayList<String>();
	 static String ser1="http://localhost:3000";
	 static String ser2="http://localhost:3001";
	 static String ser3="http://localhost:3002";

     public static void main(String[] args) throws Exception {
        System.out.println("Client Started...");       
        SortedMap<Long,String>serMap=new TreeMap<Long,String>();     
        nodes.add(ser1);
        nodes.add(ser2);
        nodes.add(ser3);
            
        for(int i=0;i<nodes.size();i++)
        {
     	   serMap.put(Hashing.md5().hashString(nodes.get(i), Charsets.UTF_8).padToLong(), nodes.get(i));
        }
        
        ArrayList objects=new ArrayList ();        
        objects.add('a');
        objects.add('b');
        objects.add('c');
        objects.add('d');
        objects.add('e');
        objects.add('f');
        objects.add('g');
        objects.add('h');
        objects.add('i');
        objects.add('j');
        for(int i=0;i<objects.size();i++)
        {     
        	String node=getNode(Hashing.md5().hashString(objects.get(i).toString(), Charsets.UTF_8).padToLong(),serMap);
        	//String node=performRHash(objects.get(i).toString());
        	CacheServiceInterface cache = new DistributedCacheService(node);
        	cache.put(i+1, objects.get(i).toString());
            System.out.println("PUT["+(i+1)+" -->> " +objects.get(i)+" -->> "+node +"]");
        }
        
        for(int i=0;i<objects.size();i++)
        {
        	   //String node=performRHash(objects.get(i).toString());
        	   String node=getNode(Hashing.md5().hashString(objects.get(i).toString(), Charsets.UTF_8).padToLong(),serMap);
        	   CacheServiceInterface cache = new DistributedCacheService(
        			   node);
        	   String value=cache.get(i+1);
               System.out.println("GET["+(i+1)+" -->> "+value+" -->> "+node+"]");
        }
        System.out.println("Client Exit...");
    }

public static String getNode(Long hashfun,SortedMap<Long,String>serMap)
{
		if(!serMap.containsKey(hashfun)){
			SortedMap<Long, String> tailMap =serMap.tailMap(hashfun);	
			hashfun=tailMap.isEmpty() ? serMap.firstKey() : tailMap.firstKey();
			
		}
		
		return serMap.get(hashfun);
}

public static String performRHash(String key) {
	String maxStr = null;
	long maxValue = Long.MIN_VALUE;
	for (String node : nodes) 
	{
		long nodesHash = hashFun.newHasher().putObject(key, StringFunnl).putObject(node, StringFunnl).hash().asLong();
		if (nodesHash > maxValue) 
		{
			maxStr = node;
			maxValue = nodesHash;
		}
	}
	return maxStr;
}


}

