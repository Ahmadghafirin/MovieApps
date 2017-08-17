package com.example.ahmad.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ahmad on 11/08/17.
 */

public class Movie implements Parcelable {
    private String title, imageUrl, synopsis, distributor, country;
    private int id;
    private double rating;

    public Movie(String title, String imageUrl, int id) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.id = id;
    }

    public Movie(String synopsis, double rating) {
        this.synopsis = synopsis;
        this.rating = rating;
    }

    public Movie(String object) {
    }

    public Movie(Parcel in) {
        title = in.readString();
        imageUrl = in.readString();
        id = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie(String synopsis, double rating, String release) {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(imageUrl);
        parcel.writeInt(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", distributor='" + distributor + '\'' +
                ", country='" + country + '\'' +
                ", id=" + id +
                ", rating=" + rating +
                '}';
    }
}
