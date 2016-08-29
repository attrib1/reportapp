package com.kisscompany.reportapp.util;

/**
 * Created by chanpc on 8/29/2016.
 */
public class FoursquareVenue {
    private String name;
    private String city;
    private String postal;
    private String category;
    private String state;
    private String country;
    public FoursquareVenue() {
        this.name = "";
        this.city = "";
        this.setCategory("");
    }

    public String getCity() {
        if (city.length() > 0) {
            return city;
        }
        return city;
    }

    public void setCity(String city) {
        if (city != null) {
            this.city = city;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPostal(String postal)
    {
        this.postal = postal;
    }

    public String getPostal()
    {
        return postal;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setState(String state)
    {
        this.state = state;
    }
    public String getState()
    {
        return state;
    }
    public void setCountry(String country)
    {
        this.country = country;
    }
    public String getCountry()
    {
        return country;
    }
}