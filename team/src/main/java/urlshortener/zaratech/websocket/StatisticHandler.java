package urlshortener.zaratech.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import urlshortener.zaratech.domain.Statistics;
import urlshortener.zaratech.domain.WebSocketClientData;
import urlshortener.common.repository.ClickRepository;
import urlshortener.zaratech.core.HeadersManager;

@Component
public class StatisticHandler extends TextWebSocketHandler {

    @Autowired
    protected ClickRepository clickRepository;

    @Autowired
    private HeadersManager headersManager;

    private static final Logger logger = LoggerFactory.getLogger(StatisticHandler.class);

    private ConcurrentMap<String, WebSocketSession> activeSessions;
    private ConcurrentMap<String, WebSocketClientData> activeSessionsFiltered;

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    private void initialize() {
        logger.info("Initialize websocket");
        activeSessions = new ConcurrentHashMap<>();
        activeSessionsFiltered = new ConcurrentHashMap<>();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        //logger.info("Receiving params from " + session.getId() + ". PARAMS=" + message.getPayload());

        String[] params = message.getPayload().toString().split("&");

        if (params.length == 2) {
            if (params[0].contains("desde") && params[1].contains("hasta")) {
                String[] desde = params[0].split("=");
                String[] hasta = params[1].split("=");
                
                Statistics stats;

                if (desde.length == 2 && hasta.length == 2) {
                    //logger.info("WS ID " + session.getId() + ". HASTA+DESDE");
                    WebSocketClientData wscd = new WebSocketClientData(desde[1], hasta[1], session);
                    activeSessionsFiltered.put(session.getId(), wscd);
                    activeSessions.remove(session.getId());
                    
                    stats = headersManager.getStatistics(clickRepository.listSinceAndFor(desde[1], hasta[1]));

                } else if (desde.length == 2) {
                    //logger.info("WS ID " + session.getId() + ". DESDE");
                    WebSocketClientData wscd = new WebSocketClientData(desde[1], null, session);
                    activeSessionsFiltered.put(session.getId(), wscd);
                    activeSessions.remove(session.getId());
                    
                    stats = headersManager.getStatistics(clickRepository.listSince(desde[1]));
                    
                } else if (hasta.length == 2) {
                    //logger.info("WS ID " + session.getId() + ". HASTA");
                    WebSocketClientData wscd = new WebSocketClientData(null, hasta[1], session);
                    activeSessionsFiltered.put(session.getId(), wscd);
                    activeSessions.remove(session.getId());
                    
                    stats = headersManager.getStatistics(clickRepository.listFor(hasta[1]));
                    
                } else {
                    //logger.info("WS ID " + session.getId() + ". NADA");
                    activeSessionsFiltered.remove(session.getId());
                    activeSessions.put(session.getId(), session);
                    
                    stats = headersManager.getStatistics(clickRepository.listAll());
                }
                
                // update data only to this session
                try {
                    session.sendMessage(new TextMessage(mapper.writeValueAsString(stats)));
                } catch (IOException e) {
                    logger.info("HandleMessage IO exception", e);
                }
            }
        }
    }

    @Scheduled(fixedDelay = 3000)
    private void broadcastData() {
        //logger.info("WS BROADCAST");

        Statistics stats = headersManager.getStatistics(clickRepository.listAll());

        try {
            
            for (WebSocketSession s : activeSessions.values()) {
                s.sendMessage(new TextMessage(mapper.writeValueAsString(stats)));
            }
            
            for(WebSocketClientData wscd : activeSessionsFiltered.values()){
                
                String desde = wscd.getDesde();
                String hasta = wscd.getHasta();
                
                if(desde != null && hasta != null){
                    stats = headersManager.getStatistics(clickRepository.listSinceAndFor(desde, hasta));
                } else if(desde != null){
                    stats = headersManager.getStatistics(clickRepository.listSince(desde));
                } else if(hasta != null){
                    stats = headersManager.getStatistics(clickRepository.listFor(hasta));
                } else {
                    // should never happen
                }
                
                wscd.getWss().sendMessage(new TextMessage(mapper.writeValueAsString(stats)));
            }
            
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("New WS connection " + session.getId());
        activeSessions.put(session.getId(), session);
        broadcastData();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Closed WS connection " + session.getId());
        activeSessions.remove(session.getId());
        activeSessionsFiltered.remove(session.getId());
    }
}