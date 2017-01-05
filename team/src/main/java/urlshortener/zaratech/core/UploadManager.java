package urlshortener.zaratech.core;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.hash.Hashing;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.zaratech.domain.RedirectionException;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.scheduling.UploadTask;
import urlshortener.zaratech.store.UploadTaskDataStore;
import urlshortener.zaratech.web.UrlShortenerControllerWithLogs;

public class UploadManager {

    private static final Logger logger = LoggerFactory.getLogger(UploadManager.class);

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

    public static ResponseEntity<ShortURL> singleShort(ShortURLRepository shortURLRepository, String url,
            HttpServletRequest request) {

        ResponseEntity<ShortURL> response;

        try {
			if (RedirectionManager.isRedirectedToSelf(url)) {

			    logger.info("Uri redirects to itself, short url can't be created");
			    response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

			} else {

			    logger.info("Uri doesn't redirects to itself. Creating short url ...");

			    ShortURL su = createAndSaveIfValid(shortURLRepository, url, UUID.randomUUID().toString(),
			            extractIP(request));

			    if (su != null) {
			        HttpHeaders h = new HttpHeaders();
			        h.setLocation(su.getUri());
			        su = QrManager.getUriWithQR(su);
			        if(su != null){
			            response = new ResponseEntity<>(su, h, HttpStatus.CREATED);
			        } else {
			            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			        }
			    } else {
			        response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			    }
			}
		} catch (RedirectionException e) {
			response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

        return response;
    }

    public static ResponseEntity<ShortURL[]> MultiShortSync(ShortURLRepository shortURLRepository,
            MultipartFile csvFile, HttpServletRequest request) {

        LinkedList<String> urls = processFile(csvFile);

        ShortURL[] su = new ShortURL[urls.size()];

        if (validateUrlList(urls)) {
            int i = 0;

            for (String url : urls) {
                try {
					if (!RedirectionManager.isRedirectedToSelf(url)) {
					    
					    ShortURL tmpSu = createAndSaveIfValid(shortURLRepository, url, UUID.randomUUID().toString(),
					            extractIP(request));
					            
					    ShortURL tmpSu2 = QrManager.getUriWithQR(tmpSu);
					    
					    if(tmpSu2 != null){
					        su[i] = tmpSu2;
					        
					    } else {
					        su[i] = tmpSu;
					    }
					    
					    i++;
					}
				} catch (RedirectionException e) {
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
            }

            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(su, h, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public static String extractIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    /**
     * Returns all the comma-separated URLs contained in the CSV file
     */
    private static LinkedList<String> processFile(MultipartFile csvFile) {

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
    private static boolean validateUrlList(LinkedList<String> urls) {

        boolean resp = true;

        for (String url : urls) {
            resp &= isValid(url);
        }

        return resp;
    }

    private static ShortURL createAndSaveIfValid(ShortURLRepository shortURLRepository, String url, String owner,
            String ip) {
        if (isValid(url)) {
            String id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            ShortURL su = new ShortURL(id, url,
                    linkTo(methodOn(UrlShortenerController.class).redirectTo(id, null)).toUri(), null,
                    new Date(System.currentTimeMillis()), owner, HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }

    private static boolean isValid(String url) {
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        return urlValidator.isValid(url);
    }
}
