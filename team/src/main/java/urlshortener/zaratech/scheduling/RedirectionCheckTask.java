package urlshortener.zaratech.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.RedirectionManager;
import urlshortener.zaratech.domain.RedirectionException;

public class RedirectionCheckTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RedirectionCheckTask.class);
    
    private ShortURLRepository shortURLRepository;
    private ShortURL su;

    public RedirectionCheckTask(ShortURLRepository shortURLRepository, ShortURL su) {
        this.shortURLRepository = shortURLRepository;
        this.su = su;
    }

    @Override
    public void run() {
        logger.info("--- STARTING REDIRECTION CHECK FOR URI " + su.getTarget() + " ---");
        
        // check: redirection to itself
        boolean result = false;
        try {
            result = RedirectionManager.isRedirectedToSelf(su.getTarget());
        } catch (RedirectionException e) {}
        
        if(result){
            logger.info("URI " + su.getTarget() + " redirects to itself --> DELETE");
            shortURLRepository.delete(su.getHash());
        }else{
            logger.info("URI " + su.getTarget() + " does NOT redirects to itself");
        }
        
        // check: 5 or more redirections
        RedirectionManager.checkNorFromUri(shortURLRepository, su);
    }
}
