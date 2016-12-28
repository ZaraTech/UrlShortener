package urlshortener.zaratech.web;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.zaratech.core.HeadersManager;


@Controller
public class UrlShortnerWebController {
   

    @RequestMapping(value = {"", "/", "/single"}, method = RequestMethod.GET)
    public String indexSingle(HttpServletRequest request) {
        return "single";
    }
    
    @RequestMapping(value = "/multi", method = RequestMethod.GET)
    public String indexMulti(HttpServletRequest request) {
        return "multi";
    }
    
    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+", method = RequestMethod.GET)
    public String showDetails(@PathVariable String id, Map<String, Object> model) {
        HeadersManager.fillModel(id, model);
        return "details";
    }
    
}
