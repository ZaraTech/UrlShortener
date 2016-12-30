package urlshortener.zaratech.domain;

import java.sql.Date;

public class UrlDetails {

    private String id;
    private String target;
    private Date date;
    private Long clicks;
    private Long visitors;

    public UrlDetails(String id, String target, Date date, Long clicks,Long visitors) {
        this.id = id;
        this.target = target;
        this.date = date;
        this.clicks = clicks;
        this.visitors=visitors;
    }

    public String getId() {
        return id;
    }

    public String getTarget() {
        return target;
    }

    public Date getDate() {
        return date;
    }

    public Long getClicks() {
        return clicks;
    }

    public Long getVisitors() {
        return visitors;
    }
}
