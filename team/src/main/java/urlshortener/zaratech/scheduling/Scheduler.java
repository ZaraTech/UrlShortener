package urlshortener.zaratech.scheduling;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {

    private TaskExecutor taskExecutor;

    public Scheduler(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    public void newUploadTask(UploadTask ut){
        taskExecutor.execute(ut);
    }
}
