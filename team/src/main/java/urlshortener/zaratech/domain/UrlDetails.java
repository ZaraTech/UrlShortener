package urlshortener.zaratech.domain;

import java.sql.Date;

public class UrlDetails {

    private String id;
    private String target;
    private Date date;
    private Long clicks;

    public UrlDetails(String id, String target, Date date, Long clicks) {
        this.id = id;
        this.target = target;
        this.date = date;
        this.clicks = clicks;
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

}
