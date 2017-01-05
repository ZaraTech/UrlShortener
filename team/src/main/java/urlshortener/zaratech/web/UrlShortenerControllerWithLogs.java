package urlshortener.zaratech.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.core.QrManager;
import urlshortener.zaratech.core.RedirectionManager;
import urlshortener.zaratech.core.UploadManager;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.domain.UrlDetails;
import urlshortener.zaratech.scheduling.Scheduler;
import urlshortener.zaratech.store.UploadTaskDataStore;

@RestController
public class UrlShortenerControllerWithLogs {

    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Autowired
    protected ShortURLRepository shortURLRepository;

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    private HeadersManager headersManager;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private UploadTaskDataStore tdStore;

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
        logger.info("Requested redirection with hash " + id);

        ShortURL l = shortURLRepository.findByKey(id);
        if (l != null) {
            createAndSaveClick(id, UploadManager.extractIP(request));
            return createSuccessfulRedirectToResponse(l);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    private void createAndSaveClick(String hash, String ip) {
        Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), null, null, null, ip, null);
        cl = clickRepository.save(cl);
        logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }
    
    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

    @RequestMapping(value = "/link-single", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> singleShortener(@RequestParam("url") String url, HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);

        return UploadManager.singleShort(shortURLRepository, url, request);
    }

    @RequestMapping(value = "/link-multi", method = RequestMethod.POST)
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("url") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new short for CSV file '" + csvFile.getOriginalFilename() + "'");
        
        return UploadManager.MultiShortSync(shortURLRepository, csvFile, request);
    }

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<UrlDetails> showDetailsJson(@PathVariable String id) {

        logger.info("Requested JSON details for id '" + id + "'");

        UrlDetails details = headersManager.getDetails(id);

        return new ResponseEntity<UrlDetails>(details, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+", produces = "text/html", method = RequestMethod.GET)
    public ResponseEntity<UrlDetails> showDetailsHtml(@PathVariable String id, HttpServletRequest request) {

        logger.info("Requested HTML details for id '" + id + "' --> REDIRECT");

        HttpHeaders h = new HttpHeaders();

        try {
            h.setLocation(new URI(request.getRequestURL() + ".html"));
            return new ResponseEntity<UrlDetails>(h, HttpStatus.TEMPORARY_REDIRECT);

        } catch (URISyntaxException e) {
            return new ResponseEntity<UrlDetails>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/task/{id:.*}", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<UploadTaskData> showTaskDetails(@PathVariable String id) {

        logger.info("Requested TASK progress for id '" + id + "'");

        UploadTaskData details = tdStore.find(id);

        // TODO ERROR si no esta la task en cache

        return new ResponseEntity<UploadTaskData>(details, HttpStatus.OK);
    }

    @RequestMapping(value = "/task-start/{id:.*}", method = RequestMethod.GET)
    public ResponseEntity<?> startTask(@PathVariable String id) {

        logger.info("Requested START TASK progress for id '" + id + "'");

        UploadManager.startTask(scheduler, tdStore, id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
