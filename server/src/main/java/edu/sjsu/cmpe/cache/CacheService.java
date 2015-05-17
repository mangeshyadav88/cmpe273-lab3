package edu.sjsu.cmpe.cache;

import java.io.File;

import edu.sjsu.cmpe.cache.config.CacheServiceConfiguration;
import edu.sjsu.cmpe.cache.domain.Entry;
import edu.sjsu.cmpe.cache.repository.CacheInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.openhft.chronicle.map.ChronicleMapBuilder;
import net.openhft.chronicle.map.ChronicleMap;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import edu.sjsu.cmpe.cache.repository.ChronicleMapCache;
import edu.sjsu.cmpe.cache.api.resources.CacheResource;

public class CacheService extends Service<CacheServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static String fileName="";
    public final static String PATH="/tmp/";
    
    public static void main(String[] args) throws Exception {
    	
    	
    	setFileName(args[1].substring(7, 15)+"_LOG.txt");
        new CacheService().run(args);
    }
    
    public static void setFileName(String name)
    {
    	fileName=name;
    }
    
    public String getFileName(){
    	return fileName;
    }
    @Override
    public void initialize(Bootstrap<CacheServiceConfiguration> bootstrap) {
        bootstrap.setName("cache-server");
    }

    @Override
    public void run(CacheServiceConfiguration configuration,
            Environment environment) throws Exception {
    	 String fileLocation=PATH+getFileName();
    	 File file=new File(fileLocation);
          ChronicleMap<Long, Entry> map =
                ChronicleMapBuilder.of(Long.class, Entry.class).entries(200)
                        .createPersistedTo(file);
        CacheInterface cache = new ChronicleMapCache(map);
        environment.addResource(new CacheResource(cache));
        log.info("Loaded resources");

    }
    
   
}