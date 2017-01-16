package urlshortener.zaratech.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.domain.RedirectionException;
import urlshortener.zaratech.scheduling.RedirectionCheckTask;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.scheduling.UploadTask;

public class RedirectionManager {
    
    public static final int MAX_REDIRECTIONS = 5;
    

    private static final Logger logger = LoggerFactory.getLogger(RedirectionManager.class);

    /**
     * Method that checks if the URL given redirects to itself
     * 
     * @param url
     *            - URL to check
     * @return True if the URL given redirects to itself.
     * @throws RedirectionException
     */
    public static boolean isRedirectedToSelf(String url) throws RedirectionException {

        URI uri;
        boolean isRedirected = false;

        try {
            uri = new URI(url);

            HttpClient client = HttpClientBuilder.create().build();
            HttpClientContext context = HttpClientContext.create();

            try {

                List<URI> redirectLocations;

                // HTTP GET
                client.execute(new HttpGet(uri), context);
                redirectLocations = context.getRedirectLocations();

                if (redirectLocations != null && !redirectLocations.isEmpty()) {
                    for (URI location : redirectLocations) {
                        if (location != null) {
                            if (url.equals(location.toString())) {

                                isRedirected = true;
                            }
                        }
                    }
                }
            } catch (ClientProtocolException e1) {
                logger.info("Failed request execution. Http protocol error");
                throw new RedirectionException();

            } catch (IOException e2) {
                logger.info("Failed request execution. Connection aborted.");

                throw new RedirectionException();
            }
        } catch (URISyntaxException e) {
            logger.info("Failed getting uri. Bad syntax");
            throw new RedirectionException();
        }

        return isRedirected;
    }
    
    
    // METHODS THAT WILL RUN ASYNC
    
    public static void startAsyncCheck(Scheduler scheduler, ShortURLRepository shortURLRepository, ShortURL su){
        scheduler.newRedirectionCheckTask(new RedirectionCheckTask(shortURLRepository, su));
    }
    
    /**
     * Checks the number of redirections starting from an URI
     */
    public static void checkNorFromUri(ShortURLRepository shortURLRepository, ShortURL su){
        try {
            
            logger.info("CHECKING URI " + su.getTarget());
            
            int nor = getNumberOfRedirections(su.getTarget());
            if(nor >= MAX_REDIRECTIONS){
                su = shortURLRepository.mark(su, false);
            } else {
                su = shortURLRepository.mark(su, true);
            }
        } catch (RedirectionException e) {
            su = shortURLRepository.mark(su, false);
        }
    }
    
    /**
     * Checks the number of redirections, URI by URI, from an URI list
     */
    public static void checkNorFromList(ShortURLRepository shortURLRepository){
        
        final Long LIMIT = 50L;
        
        Long count = shortURLRepository.count();
        Long offset = 0L;
        
        List<ShortURL> list;
        
        while(count > 0L){
            list = shortURLRepository.list(LIMIT, offset);
            offset += LIMIT;
            count -= LIMIT;
            
            for(ShortURL su : list){
                try {
                    
                    boolean checkNumber = false;
                    boolean checkItSelf = false;
                    
                    logger.info("CHECKING NUM OF REDIRECTIONS FROM URI " + su.getTarget());
                    
                    int nor = getNumberOfRedirections(su.getTarget());
                    checkNumber = (nor >= MAX_REDIRECTIONS);
                    
                    logger.info("CHECKING REDIRECTION TO ITSELF FROM URI " + su.getTarget());
                    
                    checkItSelf = isRedirectedToSelf(su.getTarget());
                    
                    su = shortURLRepository.mark(su, !(checkNumber || checkItSelf));
                    
                } catch (RedirectionException e) {
                    su = shortURLRepository.mark(su, false);
                }
            }
        }
    }
    

    private static int getNumberOfRedirections(String url) throws RedirectionException {

        URI uri;
        int numberOfRedirections = 0;

        try {
            uri = new URI(url);

            HttpClient client;
            HttpClientContext context;

            try {

                List<URI> redirectLocations;

                // HTTP GET
                
                URI location = uri;
                
                while(location != null){
                    client = HttpClientBuilder.create().build();
                    context = HttpClientContext.create();

                    client.execute(new HttpGet(location), context);
                    redirectLocations = context.getRedirectLocations();

                    if (redirectLocations != null && !redirectLocations.isEmpty()) {
                        location = redirectLocations.get(0);
                        
                        if (location != null) {
                            numberOfRedirections++;
                        }
                    } else {
                        location = null;
                    }
                }

            } catch (ClientProtocolException e1) {
                logger.info("Failed request execution. Http protocol error");
                throw new RedirectionException();

            } catch (IOException e2) {
                logger.info("Failed request execution. Connection aborted.");

                throw new RedirectionException();
            }
        } catch (URISyntaxException e) {
            logger.info("Failed getting uri. Bad syntax");
            throw new RedirectionException();
        }

        logger.info("URI " + uri.toString() + " HAS " + numberOfRedirections + " REDIRECTIONS");
        return numberOfRedirections;
    }
}
