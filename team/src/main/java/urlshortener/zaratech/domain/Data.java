package urlshortener.zaratech.domain;

public class Data{
    private String nombre;
    private float y;
    public Data(String nombre,float y){
        this.nombre=nombre;
        this.y=y;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public void setY(float y){
        this.y=y;
    }
    public String getNombre(){
        return this.nombre;
    }
    public float getY(){
        return this.y;
    }
}