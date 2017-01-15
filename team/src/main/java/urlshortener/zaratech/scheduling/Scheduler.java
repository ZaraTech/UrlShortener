package urlshortener.zaratech.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import urlshortener.common.repository.ShortURLRepository;

@Service
public class Scheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    private TaskExecutor taskExecutor;
    
    @Autowired
    private ShortURLRepository shortURLRepository;

    public Scheduler(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    public void newUploadTask(UploadTask ut){
        taskExecutor.execute(ut);
    }
    
    public void newRedirectionCheckTask(RedirectionCheckTask rct) {
        taskExecutor.execute(rct);
    }
    
    @Scheduled(fixedDelay = 60000)
    public void newRedirectionCheckScheduledTask() {
        RedirectionCheckScheduledTask rcst = new RedirectionCheckScheduledTask(shortURLRepository);
        taskExecutor.execute(rcst);
    }
}
