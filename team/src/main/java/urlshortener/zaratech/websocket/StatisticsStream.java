package urlshortener.zaratech.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import urlshortener.zaratech.websocket.StatisticHandler;


@Configuration
@EnableWebSocket
public class StatisticsStream implements WebSocketConfigurer {

    @Autowired
    private StatisticHandler statisticHandler;  // The WebSocketHandler that manages the events related to the socket


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(statisticHandler, "/stats-stream").setAllowedOrigins("*");
    }
}