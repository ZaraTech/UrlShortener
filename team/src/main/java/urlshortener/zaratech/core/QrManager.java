package urlshortener.zaratech.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import urlshortener.common.domain.ShortURL;

public class QrManager {
	
	private static final Logger logger = LoggerFactory.getLogger(QrManager.class);
	

	/**
	 * Method that assigns a QR code to the given HTTP response, which contains the associated URI
	 * @param response HTTP response
	 * @return HTTP response with QR code assigned
	 */
	public static ResponseEntity<ShortURL> getUriWithQR(ResponseEntity<ShortURL> response){
		
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
	
	/*
	private static PublicShortUrl getPublicBody(ShortURL body){
		PublicShortUrl newBody = new PublicShortUrl();
		
		newBody.setHash(body.getHash());
		newBody.setTarget(body.getTarget());
		newBody.setUri(body.getUri());
		newBody.setCreated(body.getCreated());
		newBody.setOwner(body.getOwner());
		newBody.setMode(body.getMode());
		newBody.setIp(body.getIP());
		newBody.setCountry(body.getCountry());
		
		return newBody;
	}*/
}
