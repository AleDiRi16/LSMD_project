package it.unipi.lsmsd.DTO;



public class AnalyticDTO {

    private String name;
    private Double count;


    public AnalyticDTO(String name,Double count) {
        this.name = name;
       this.count=count;
    }


    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public Double getCount() {return this.count;}
    public void setCount(Double count) {this.count = count;}
}
