package berman.andrew;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;

/**
 * Created by andrew on 9/18/15.
 */
public class Point implements Serializable {
    private static final double R = 3961; //Earth's radius in miles

    private Double latitude = 0.0;
    private Double longitude = 0.0;

    public Point(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Point() {

    }

    public Double getLatitude() {
        return latitude;
    }

    protected void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    protected void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (latitude != null ? !latitude.equals(point.latitude) : point.latitude != null) return false;
        return !(longitude != null ? !longitude.equals(point.longitude) : point.longitude != null);
    }

    @Override
    public int hashCode() {
        int result = latitude != null ? latitude.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }

    public double getDistanceInMiles(Point point) {
        //Haversine distance
        double dLat = Math.toRadians(point.getLatitude() - this.getLatitude());
        double dLon = Math.toRadians(point.getLongitude() - this.getLongitude());
        double lat1 = Math.toRadians(this.getLatitude());
        double lat2 = Math.toRadians(point.getLatitude());

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
