package urlshortener.zaratech.scheduling;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.UploadManager;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.store.UploadTaskDataStore;

public class UploadTask implements Runnable{
    
    private static final Logger logger = LoggerFactory.getLogger(UploadTask.class);
    
    private UploadTaskData task;
    private String ip;
    private String baseUrl;
    
    private UploadTaskDataStore tdStore;
    
    private ShortURLRepository shortURLRepository;
    
    public UploadTask(ShortURLRepository shortURLRepository, UploadTaskDataStore tdStore, UploadTaskData task, String ip, String baseUrl) {
        this.task = task;
        this.ip = ip;
        this.baseUrl = baseUrl;
        this.tdStore = tdStore;
        this.shortURLRepository = shortURLRepository;
    }

    @Override
    public void run() {
        
        logger.info("Starting UploadTask with id=" + task.getId());
        
        List<String> urls = task.getUrlList();
        
        for(String url : urls){
            
            ShortURL su = processUrl(url);
            
            if(su != null){
                task.setUrlCompleted(su);                
                logger.info("UploadTask (id " + task.getId() + "): Completed URL " + url);
               
            } else {
                task.setUrlError(url);
                logger.info("UploadTask (id " + task.getId() + "): Error URL " + url);
            }
            
            tdStore.save(task);
        }
    }
    
    private ShortURL processUrl(String url){
        try {
            return UploadManager.singleShort(shortURLRepository, baseUrl, url, ip);
            
        } catch (Exception e) {
            return null;
        }
    }

}
