package com.meglio.albuquerk.rubaink.devilpass;

public class Users {

    public String name;
    public String email;
    public String pass;
    public String phone;
    public String country_code;
    public String image,genre;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Users(String name, String email, String pass, String phone, String country_code, String image, String genre) {
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phone = phone;
        this.country_code = country_code;
        this.image = image;
        this.genre = genre;
    }

    public Users(){
    }
}