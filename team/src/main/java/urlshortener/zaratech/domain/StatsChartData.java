package urlshortener.zaratech.domain;

public class StatsChartData {

    private String name;
    private float y;

    public StatsChartData(String nombre, float y) {
        this.name = nombre;
        this.y = y;
    }

    public void setName(String nombre) {
        this.name = nombre;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getName() {
        return this.name;
    }

    public float getY() {
        return this.y;
    }
}