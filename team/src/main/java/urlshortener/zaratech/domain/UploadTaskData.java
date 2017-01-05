package urlshortener.zaratech.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UploadTaskData extends TaskData implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String PENDING = "pending";
    private static final String ERROR = "error";
    private static final String COMPLETED = "completed";

    private HashMap<String, UploadTaskDataStruct> urlsMap;

    @JsonProperty("urlList")
    private List<UploadTaskDataStruct> urls;

    @JsonProperty("urlListId")
    private String id;

    public UploadTaskData(String id) {
        urls = new LinkedList<UploadTaskDataStruct>();
        urlsMap = new HashMap<String, UploadTaskDataStruct>();
        this.id = id;
    }

    public void addUrl(String url) {

        UploadTaskDataStruct tc = new UploadTaskDataStruct(url, PENDING);

        urls.add(tc);
        urlsMap.put(url, tc);
    }

    public void setUrlCompleted(String url) {
        UploadTaskDataStruct tc = urlsMap.get(url);
        tc.setProgress(COMPLETED);
    }

    public void setUrlError(String url) {
        UploadTaskDataStruct tc = urlsMap.get(url);
        tc.setProgress(ERROR);
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public List<String> getUrlList() {

        List<String> resp = new LinkedList<String>();

        for (UploadTaskDataStruct tc : urls) {
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
        UploadTaskData other = (UploadTaskData) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
