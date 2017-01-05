package urlshortener.zaratech.scheduling;

import java.util.List;

import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.store.UploadTaskDataStore;

public class UploadTask implements Runnable{
    
    private UploadTaskData task;
    private UploadTaskDataStore tdStore;
    
    public UploadTask(UploadTaskData task, UploadTaskDataStore tdStore) {
        this.task = task;
        this.tdStore = tdStore;
    }

    @Override
    public void run() {
        List<String> urls = task.getUrlList();
        
        for(String url : urls){
            
            if(processUrl(url)){
                task.setUrlCompleted(url);
               
            } else {
                task.setUrlError(url);
            }
            
            tdStore.save(task);
        }
    }
    
    private boolean processUrl(String url){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }

        return Math.random() > 0.5;
    }

}
