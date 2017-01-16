package urlshortener.zaratech.web;

import javax.servlet.http.HttpServletRequest;

public class BaseUrlManager {
    
    /**
     * Return a string with local name and server port.
     * (Example http://localhost:8080)
     */
    public static String getLocalBaseUrl(HttpServletRequest request){
        
        String url = "http://" + request.getLocalName() + ":" + request.getServerPort();
        
        if(url.contains("heroku")){
            url = "https://zaratech-bus.herokuapp.com";
        }
        
        return url;
    }

}
