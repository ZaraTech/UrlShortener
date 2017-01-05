package urlshortener.zaratech.core;

import urlshortener.zaratech.domain.TaskDetails;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.scheduling.UploadTask;
import urlshortener.zaratech.store.TaskDetailsStore;

public class UploadManager {

    // TODO BORRAR
    public static void startTask(Scheduler scheduler, TaskDetailsStore tdStore, String id){
        TaskDetails details = new TaskDetails(id);

        details.addUrl("http://example1.com");
        details.addUrl("http://example2.com");
        details.addUrl("http://example3.com");
        details.addUrl("http://example4.com");
        details.addUrl("http://example5.com");
        details.addUrl("http://example6.com");
        details.addUrl("http://example7.com");
        details.addUrl("http://example8.com");
        
        tdStore.save(details);
        
        scheduler.newUploadTask(new UploadTask(details, tdStore));
    }
}
