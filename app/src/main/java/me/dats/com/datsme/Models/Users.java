package me.dats.com.datsme.Models;

public class Users {

    public String name;
    public String thumb_image;
    public String image;

    public Users() {
        //For Firebase
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public Users(String name, String image, String thumb_image) {
        this.name = name;
        this.image = image;
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
