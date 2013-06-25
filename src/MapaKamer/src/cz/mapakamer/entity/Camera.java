package cz.mapakamer.entity;

import org.osmdroid.util.GeoPoint;

import android.graphics.drawable.Drawable;
import android.location.Location;


public class Camera {

	private static final int STATUS_NEW = 0;
	private int id;
	private double latitude;
	private double longitude;
	private GeoPoint coordinates;
	private Drawable image;
    private String address;
    private String description;    
    private String author;
    private int status;
	private String imageBase64Encoded;
	private int distance;
    
	

	public Camera() {
		this.status = STATUS_NEW;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
    public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public GeoPoint getCoordinates(){
		return coordinates;
	}
	public void setCoordinates(GeoPoint coordinates){
		this.coordinates = coordinates;
	}	
	public Drawable getImage(){
		return image;
	}
	public void setImage(Drawable image){
		this.image = image;
	}	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}	
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getImageBase64Encoded() {
		return imageBase64Encoded;
	}
	public void setImageBase64Encoded(String imageBase64Encoded) {
		this.imageBase64Encoded = imageBase64Encoded;
	}
	
	
	
	
	public static int howFar(double lat1, double long1, double lat2, double long2) {
        float result[] = new float[3];
        Location.distanceBetween(lat1, long1, lat2, long2, result);
        return (int) result[0];
    }
	
	//by distance
	public int compare(Camera camera1, Camera camera2) {
        if (camera1.getDistance() > camera2.getDistance())
            return 1;
        if (camera1.getDistance() < camera2.getDistance())
            return -1;
        return 0;
    }
    
}
