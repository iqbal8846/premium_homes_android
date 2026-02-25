package www.dpremiumhomes.com.models;

public class FavoriteModel {

    public String propertyId;
    public String title;
    public String price;
    public String location;
    public String image;

    public FavoriteModel(String propertyId, String title, String price, String location, String image) {
        this.propertyId = propertyId;
        this.title = title;
        this.price = price;
        this.location = location;
        this.image = image;
    }
}
