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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.Click;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;
import urlshortener.zaratech.core.HeadersManager;
import urlshortener.zaratech.core.UploadManager;
import urlshortener.zaratech.domain.RedirectionDetails;
import urlshortener.zaratech.domain.UploadTaskData;
import urlshortener.zaratech.domain.UrlDetails;
import urlshortener.zaratech.domain.Statistics;
import urlshortener.zaratech.domain.UserAgentDetails;
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
        UserAgentDetails ua=headersManager.getUA(request.getHeader("User-Agent"));

        ShortURL l = shortURLRepository.findByKey(id);
        if (l != null) {
            createAndSaveClick(id, UploadManager.extractIP(request),ua.getBrowserName(),ua.getBrowserVersion(),ua.getOsName());
            return createSuccessfulRedirectToResponse(l);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/statistics", produces = "application/json", method = RequestMethod.GET)
    public ResponseEntity<Statistics> showStatistics(HttpServletRequest request) {
        Statistics statistics=headersManager.getStatistics(clickRepository.listAll());
        return new ResponseEntity<Statistics>(statistics, HttpStatus.OK);
    }

    private void createAndSaveClick(String hash, String ip,String browser,String version,String os) {
        Click cl = new Click(null, hash, new Date(System.currentTimeMillis()), null, browser,version,os, null, ip, null);
        cl = clickRepository.save(cl);
        logger.info(cl != null ? "[" + hash + "] saved with id [" + cl.getId() + "]" : "[" + hash + "] was not saved");
    }
    
    private ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        h.setLocation(URI.create(l.getTarget()));
        return new ResponseEntity<>(h, HttpStatus.valueOf(l.getMode()));
    }

    @RequestMapping(value = "/link-single", method = RequestMethod.POST)
    public ResponseEntity<ShortURL> singleShortener(@RequestParam("url") String url, 
    		@RequestParam(value = "vCardFName", required = false) String vCardFName, 
    		@RequestParam(value = "vCardCheckbox", required = false) Boolean vCardCheckbox,
    		@RequestParam(value = "errorRadio", required = false) String errorRadio, HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);

        return UploadManager.singleShort(shortURLRepository, url, request, vCardFName, vCardCheckbox, errorRadio);
    }

    @RequestMapping(value = "/link-multi", method = RequestMethod.POST)
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("file") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new multi-short for CSV file '" + csvFile.getOriginalFilename() + "'");
        
        return UploadManager.multiShortSync(shortURLRepository, csvFile, request);
    }
    
    @RequestMapping(value = "/link-multi-async-file", method = RequestMethod.POST)
    public ResponseEntity<RedirectionDetails> multiShortenerAsyncFile(@RequestParam("file") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new ASYNC multi-short for CSV file '" + csvFile.getOriginalFilename() + "'");
        
        return UploadManager.multiShortAsync(scheduler, shortURLRepository, tdStore, csvFile, request);
    }
    
    @RequestMapping(value = "/link-multi-async-input", method = RequestMethod.POST)
    public ResponseEntity<RedirectionDetails> multiShortenerAsyncInput(@RequestParam("input") String urlList,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new ASYNC multi-short for FORM DATA");
        
        String[] urls = urlList.split("\r?\n");
        List<String> urlsList = new LinkedList<String>();
        
        for(String url : urls){
            urlsList.add(url);
        }
        
        return UploadManager.multiShortAsync(scheduler, shortURLRepository, tdStore, urlsList, request);
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
}
