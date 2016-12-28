package urlshortener.zaratech.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.UrlShortenerController;
import urlshortener.zaratech.core.QrManager;
import urlshortener.zaratech.core.RedirectionManager;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Override
	@RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}", method = RequestMethod.GET)
	
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		return super.redirectTo(id, request);
	}	  
    

	@Override
	public ResponseEntity<ShortURL> singleShortener(@RequestParam("url") String url,
											  @RequestParam(value = "sponsor", required = false) String sponsor,
											  HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		
		ResponseEntity<ShortURL> response; 
		
		if (RedirectionManager.isRedirectedToSelf(url)){  

			logger.info("Uri redirects to itself, short url can't be created");
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);		
		} else { 
			
			logger.info("Uri doesn't redirects to itself. Creating short url ...");
			response = QrManager.getUriWithQR(super.singleShortener(url, sponsor, request));
		}	
		
		return response;	
	}
	
	@Override
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("url") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new short for CSV file '" + csvFile.getOriginalFilename() + "'");
        return super.multiShortener(csvFile, sponsor, request);
    }

}

