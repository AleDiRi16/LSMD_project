package it.unipi.lsmsd.DTO;

public class  VehicleDTO {
    private String brand;
    private Integer count;
    private String name;


    public VehicleDTO(String brand, Integer count) {
        this.brand = brand;

        this.count = count;
    }

    public String getBrand() { return brand;}
    public void setBrand(String brand) {this.brand = brand;}
    public String getName() { return name;}
    public void setName(String name) {this.name = name;}
    public Integer getCount() { return count;}
    public void setCount(Integer count) {this.count = count;}

}
