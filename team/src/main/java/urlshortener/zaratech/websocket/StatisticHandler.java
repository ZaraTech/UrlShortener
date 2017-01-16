package urlshortener.zaratech.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.lang.Object;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import urlshortener.zaratech.domain.Statistics;
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

    @Autowired
    private ObjectMapper mapper;

    @PostConstruct
    private void initialize() {
        logger.info("Initialize websocket");
        activeSessions = new ConcurrentHashMap<>();
    }


    private void broadcastSessionCount() {
        logger.info("Broadcast websocket");
        Statistics stats=headersManager.getStatistics(clickRepository.listAll());
        try {
            for (WebSocketSession s : activeSessions.values()) {
                logger.info("Sending stats "+stats.toString());
                s.sendMessage(new TextMessage(mapper.writeValueAsString(stats)));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        logger.info("After websocket");
        activeSessions.put(session.getId(), session);
        broadcastSessionCount();
    }

    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message){
        logger.info("Sending params "+message.toString());
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("Closed websocket");
        activeSessions.remove(session.getId());
        broadcastSessionCount();
    }
}