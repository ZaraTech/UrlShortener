package urlshortener.zaratech.core;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import urlshortener.common.domain.ShortURL;
import urlshortener.common.repository.ClickRepository;
import urlshortener.common.repository.ShortURLRepository;

public class HeadersManager {

	@Autowired
	protected static ShortURLRepository shortURLRepository;
	@Autowired
	protected static ClickRepository clickRepository;
	
	public static void fillModel(String id, Map<String, Object> model){
		model.put("id", id);
        ShortURL url=getDetails(id);
        Long clicks=getClickDetails(id);
        model.put("date",url.getCreated().toString());
        model.put("target",url.getTarget());
        model.put("clicks",clicks.toString());
	}

	private static ShortURL getDetails(String id){
		return shortURLRepository.findByKey(id);
	}
	
	private static Long getClickDetails(String id){
		return clickRepository.clicksByHash(id);
	}
}
