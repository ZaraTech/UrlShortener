package urlshortener.zaratech.domain;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketClientData {

    private String desde;
    private String hasta;
    private WebSocketSession wss;
    
    public WebSocketClientData(String desde, String hasta, WebSocketSession wss) {
        this.desde = desde;
        this.hasta = hasta;
        this.wss = wss;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }

    public WebSocketSession getWss() {
        return wss;
    }

    public void setWss(WebSocketSession wss) {
        this.wss = wss;
    }
    
}
