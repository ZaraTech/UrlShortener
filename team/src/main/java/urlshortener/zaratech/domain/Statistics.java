package urlshortener.zaratech.domain;

import java.sql.Date;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Statistics {
    /*
    * Class atributes
    * */

    @JsonProperty("browserList")
    private List<String> browsers;

    @JsonProperty("versionList")
    private List<List<String>> versions;

    @JsonProperty("osList")
    private List<String>  os;

    @JsonProperty("clicks")
    private int totalClicks;

    @JsonProperty("clicksforbrowser")
    private List<Integer> clicksForBrowser;

    @JsonProperty("clicksforversion")
    private List<List<Integer>> clicksForBrowserAndVersion;

    @JsonProperty("clicksforos")
    private List<Integer> clicksForOs;

    /*
    * Class constructor
    * */
    public Statistics() {
        browsers=new  ArrayList<String>();
        versions=new ArrayList<List<String>>();
        os=new  ArrayList<String>();
        clicksForBrowser=new  ArrayList<Integer>();
        clicksForBrowserAndVersion=new  ArrayList<List<Integer>>();
        clicksForOs=new  ArrayList<Integer>();
    }

    //Getters
    public String getBrowser(int index){
        return this.browsers.get(index);
    }

    public String getVersion(int index1,int index2){
        return this.versions.get(index1).get(index2);
    }

    public String getOs(int index){
        return this.os.get(index);
    }

    public float getUseOs(int index){
        return (this.clicksForOs.get(index)/totalClicks)*100;
    }

    public float getUseBrowserAndVersion(int index1,int index2){
        return (this.clicksForBrowserAndVersion.get(index1).get(index2)/totalClicks)*100;
    }

    public int getIndexBrowser(String browser) {
        int index=-1;
        for (int i = 0; i <= this.browsers.size() - 1; i++) {
            if(this.browsers.get(i).equals(browser)){
                index=i;
            }
        }
        return index;
    }

    public int getIndexVersion(String version) {
        int index=-1;
        for (int i = 0; i <= this.versions.size() - 1; i++) {
            for(int j=0;j<=this.versions.get(i).size()-1;j++){
                if(this.versions.get(i).get(j).equals(version)){
                    index=j;
                }
            }
        }
        return index;
    }

    public int getIndexOs(String os) {
        int index=-1;
        for (int i = 0; i <= this.os.size() - 1; i++) {
            if(this.os.get(i).equals(os)){
                index=i;
            }
        }
        return index;
    }

    //Setters
    public void setBrowser(int index,String browser){
        this.browsers.set(index,browser);
    }

    public void setVersion(int index1,int index2,String version){
        this.versions.get(index1).set(index2,version);
    }

    public void setOs(int index,String os){
        this.os.set(index,os);
    }

    //Insert
    public void insertBrowser(String browser){
        this.browsers.add(browser);
        this.versions.add(new ArrayList<String>());
        this.clicksForBrowser.add(1);
        clicksForBrowserAndVersion.add(new ArrayList<Integer>());
    }

    public void insertVersion(int browser,String version){
        this.versions.get(browser).add(version);
        this.clicksForBrowserAndVersion.get(browser).add(1);
    }

    public void insertOs(String os){
        this.os.add(os);
        this.clicksForOs.add(1);
    }

    public void addTotal(int total){
        this.totalClicks=total;
    }

    //Updates
    public void updateclicksForBrowser(int index){
        int aux=clicksForBrowser.get(index);
        this.clicksForBrowser.set(index,aux+1);
    }

    public void updateclicksForOs(int index){
        int aux=clicksForOs.get(index);
        this.clicksForOs.set(index,aux+1);
    }

    public void updateclicksForBrowserAndVersion(int index1,int index2){
        int aux=this.clicksForBrowserAndVersion.get(index1).get(index2);
        this.clicksForBrowserAndVersion.get(index1).set(index2,aux+1);
    }


}