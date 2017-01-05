package urlshortener.zaratech.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String PENDING = "pending";
    private static final String ERROR = "error";
    private static final String COMPLETED = "completed";

    private HashMap<String, TaskComponents> urlsMap;

    @JsonProperty("urlList")
    private List<TaskComponents> urls;

    @JsonProperty("urlListId")
    private String id;

    public TaskDetails(String id) {
        urls = new LinkedList<TaskComponents>();
        urlsMap = new HashMap<String, TaskComponents>();
        this.id = id;
    }

    public void addUrl(String url) {

        TaskComponents tc = new TaskComponents(url, PENDING);

        urls.add(tc);
        urlsMap.put(url, tc);
    }

    public void setUrlCompleted(String url) {
        TaskComponents tc = urlsMap.get(url);
        tc.setProgress(COMPLETED);
    }

    public void setUrlError(String url) {
        TaskComponents tc = urlsMap.get(url);
        tc.setProgress(ERROR);
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public List<String> getUrlList() {

        List<String> resp = new LinkedList<String>();

        for (TaskComponents tc : urls) {
            resp.add(tc.getUrl());
        }

        return resp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskDetails other = (TaskDetails) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
