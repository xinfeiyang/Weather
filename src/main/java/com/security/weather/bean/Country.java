package com.security.weather.bean;

import org.litepal.crud.DataSupport;

/**
 * 县
 */
public class Country extends DataSupport {

    private Integer id;
    private String countryName;
    private String weatherId;
    private Integer cityId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", countryName='" + countryName + '\'' +
                ", weatherId='" + weatherId + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
