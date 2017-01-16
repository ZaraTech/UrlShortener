package urlshortener.zaratech.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.*;

import urlshortener.zaratech.domain.StatsChartData;

public class Statistics {
    /*
    * Class atributes
    * */

    @JsonProperty("jsonOs")
    private List<StatsChartData> jsonOsData;

    @JsonProperty("jsonVersion")
    private List<StatsChartData> jsonVersionData;

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

    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
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
        jsonOsData=new ArrayList<StatsChartData>();
        jsonVersionData=new ArrayList<StatsChartData>();
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
        float r=(float)this.clicksForOs.get(index)/totalClicks;
        return r;
    }

    public float getUseBrowserAndVersion(int index1,int index2){
        float r=(float)this.clicksForBrowserAndVersion.get(index1).get(index2)/totalClicks;
        //logger.info("Use of version "+r);
        return r;
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

    public int getIndexVersion(int browserIndex, String version) {
        int index=-1;
        for(int i=0;i<=this.versions.get(browserIndex).size()-1;i++){
            if(this.versions.get(browserIndex).get(i).equals(version)){
                index=i;
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

    public List<String> getListOs(){
        return this.os;
    }

    public List<String> getListBrowser(){
        return this.browsers;
    }

    public List<List<String>> getListVersions(){
        return this.versions;
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

    public void insertCharts(){
        List<String> aux=getListOs();
        if(aux!=null){
            for(int i=0;i<=aux.size()-1;i++){
                String nombre=aux.get(i);
                float y=getUseOs(i);
                StatsChartData data=new StatsChartData(nombre,y);
                this.jsonOsData.add(data);
            }
        }
        Gson gson = new Gson();
        gson.toJson(jsonOsData);
        List<List<String>> aux2=getListVersions();
        if(aux2!=null){
            for(int i=0;i<=aux2.size()-1;i++){
                for(int j=0;j<=aux2.get(i).size()-1;j++){
                    String nombre=getBrowser(i)+" "+aux2.get(i).get(j);
                    float y=getUseBrowserAndVersion(i,j);
                    StatsChartData data=new StatsChartData(nombre,y);
                    this.jsonVersionData.add(data);
                }

            }
        }
        gson.toJson(jsonVersionData);
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