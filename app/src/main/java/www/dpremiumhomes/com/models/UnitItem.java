package www.dpremiumhomes.com.models;

public class UnitItem {
    private String unitName;
    private String area;
    private String bedrooms;
    private String bathrooms;
    private String price;

    public UnitItem(String unitName, String area, String bedrooms, String bathrooms, String price) {
        this.unitName = unitName;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.price = price;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getArea() {
        return area;
    }

    public String getBedrooms() {
        return bedrooms;
    }

    public String getBathrooms() {
        return bathrooms;
    }

    public String getPrice() {
        return price;
    }
}