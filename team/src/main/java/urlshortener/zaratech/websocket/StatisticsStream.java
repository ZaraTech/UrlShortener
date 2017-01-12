package urlshortener.zaratech.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import urlshortener.zaratech.websocket.StatisticHandler;


/**
 * This class configures the raw WebSocket used to keep track of the open sessions and inform
 * the clients of the number of active users at every moment.
 */
@Configuration
@EnableWebSocket
public class StatisticsStream implements WebSocketConfigurer {

    @Autowired
    private StatisticHandler statisticHandler;  // The WebSocketHandler that manages the events related to the socket

    /**
     * Registers a handler for the ws://<<HOST>>/ws WebSocket that allows any
     * client from any origin
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(statisticHandler, "/statistics-stream").setAllowedOrigins("*");
    }
}