package urlshortener.zaratech.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.web.UrlShortenerController;

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
		
		if (isRedirectedToSelf(url)){  

			logger.info("Uri redirects to itself, short url can't be created");
			response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);		
		} else { 
			
			logger.info("Uri doesn't redirects to itself. Creating short url ...");
			response = getUriWithQR(super.singleShortener(url, sponsor, request));
		}	
		
		return response;	
	}
	
	@Override
    public ResponseEntity<ShortURL[]> multiShortener(@RequestParam("url") MultipartFile csvFile,
            @RequestParam(value = "sponsor", required = false) String sponsor, HttpServletRequest request) {

        logger.info("Requested new short for CSV file '" + csvFile.getOriginalFilename() + "'");
        return super.multiShortener(csvFile, sponsor, request);
    }

	
	/**
	 * Method that checks if the URL given redirects to itself
	 * @param url - URL to check
	 * @return True if the URL given redirects to itself.
	 */
	private boolean isRedirectedToSelf(String url){
		
		URI uri;
		URI location;
		boolean isRedirected = false;
		int responseCode = 0;

		try {
			uri = new URI(url);
			
			// HTTP GET
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<ShortURL> response = restTemplate.getForEntity(uri.toString(), null);
			location = response.getHeaders().getLocation();
			responseCode = response.getStatusCodeValue();
			
			if (responseCode >= 300 && responseCode < 400){ // HTTP code 3XX - Redirection
				
				if (location != null){
					
					if (url.equals(location.toString())) {	
						
						isRedirected = true;				
					} else {
						
						isRedirected = false;
					}
				} else {
					
					isRedirected = true; 
				}
			} else {
				
				isRedirected = false;
			}		
		} catch (RestClientException e) {
			logger.info("Failed checking redirection");
			
		} catch (URISyntaxException e) {
			logger.info("Failed getting uri. Bad syntax");
		}
		
		return isRedirected;	
	}
	
	
	/**
	 * Method that assigns a QR code to the given HTTP response, which contains the associated URI
	 * @param response HTTP response
	 * @return HTTP response with QR code assigned
	 */
	private ResponseEntity<ShortURL> getUriWithQR(ResponseEntity<ShortURL> response){
		
		ShortURL body = response.getBody();
		HttpHeaders headers= response.getHeaders();
		HttpStatus status = response.getStatusCode();
		URI uriQR;	
		
		try {
			if( status.value() >= 200 && status.value() < 300) { // HTTP code 2XX - Success
				
				URI uri = body.getUri();
				logger.info("Requested new QR for uri " + uri.toString());
				
				// API QRserver
				//http://api.qrserver.com/v1/create-qr-code/?data="+ uri + "!&size=100x100"
				
				// API de Google
				uriQR = new URI("http://chart.googleapis.com/chart?cht=qr&chs=100x100&chl=" + uri.toString() + "&choe=UTF-8");
				body.setQR(uriQR);
				
				logger.info("Uri-QR obtained " + uriQR);			
			} else {
				
				logger.info("Uri-QR Error code " + status.value());
			}			
		} catch (URISyntaxException e) {			
			logger.info("Uri-QR assignment failed.");
		}		
		
		ResponseEntity<ShortURL> responseWithQR = new ResponseEntity<>(body, headers, status);
		
		return responseWithQR;	
	}
}

