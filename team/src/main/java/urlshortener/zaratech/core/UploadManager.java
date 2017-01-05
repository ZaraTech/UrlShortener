package urlshortener.zaratech.core;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.hash.Hashing;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.scheduling.UploadTask;
import urlshortener.zaratech.store.UploadTaskDataStore;
import urlshortener.zaratech.web.UrlShortenerControllerWithLogs;

public class UploadManager {
    
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    // TODO BORRAR
    public static void startTask(Scheduler scheduler, UploadTaskDataStore tdStore, String id) {
        UploadTaskData details = new UploadTaskData(id);

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
    
    public static void createAndSaveClick(ClickRepository clickRepository, String hash, String ip) {
        Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), null, null, null, ip, null);
        cl = clickRepository.save(cl);
        logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }
    
    public static String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
    
    public static ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

    /**
     * Returns all the comma-separated URLs contained in the CSV file
     */
    public static LinkedList<String> processFile(MultipartFile csvFile) {

        InputStream is;

        try {
            is = csvFile.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            LinkedList<String> list = new LinkedList<String>();
            while ((line = br.readLine()) != null) {
                String[] urls = line.split(",");
                for (String url : urls) {
                    if (!url.trim().equals("")) {
                        list.addLast(url.trim());
                    }
                }
            }

            return list;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns true if, and only if, all URLs are valid
     */
    public static boolean validateUrlList(LinkedList<String> urls) {

        boolean resp = true;

        for (String url : urls) {
            resp &= isValid(url);
        }

        return resp;
    }
    
    public static ShortURL createAndSaveIfValid(ShortURLRepository shortURLRepository, String url, String sponsor, String owner, String ip) {
        if (isValid(url)) {
            String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            ShortURL su = new ShortURL(id, url,
                    linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null)).toUri(), sponsor,
                    new Date(System.currentTimeMillis()), owner, HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }

    public static boolean isValid(String url) {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        return urlValidator.isValid(url);
    }
}
