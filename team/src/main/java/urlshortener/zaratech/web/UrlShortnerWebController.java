package urlshortener.zaratech.web;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;


@Controller
public class UrlShortnerWebController {
    @Autowired
    protected ShortURLRepository shortURLRepository;
    @Autowired
    protected ClickRepository clickRepository;

    @RequestMapping(value = {"", "/", "/single"}, method = RequestMethod.GET)
    public String indexSingle(HttpServletRequest request) {
        return "single";
    }
    
    @RequestMapping(value = "/multi", method = RequestMethod.GET)
    public String indexMulti(HttpServletRequest request) {
        return "multi";
    }
    
    @RequestMapping(value = "/{id:(?!link-single|link-multi|index|single|multi).*}+", method = RequestMethod.GET)
    public String redirectTo(@PathVariable String id, Map<String, Object> model) {
        model.put("id", id);
        ShortURL url=getDetails(id);
        Long clicks=getClickDetails(id);
        model.put("date",url.getCreated().toString());
        model.put("target",url.getTarget());
        model.put("clicks",clicks.toString());
        return "details";
    }
    private ShortURL getDetails(String id){
        return shortURLRepository.findByKey(id);
    }
    private Long getClickDetails(String id){
        return clickRepository.clicksByHash(id);
    }
}
