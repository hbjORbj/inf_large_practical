package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameMap {
	public FeatureCollection featureCollection;
    public String JSON_MAP;
    public ArrayList<Feature> stations;
    
    GameMap(String mapUrl) {
        JSON_MAP = GameMap.getMap(mapUrl); // map is obtained using given map url
        featureCollection = FeatureCollection.fromJson(JSON_MAP); // FeatureCollection is obtained from map obtained
        stations = (ArrayList<Feature>) featureCollection.features(); // All features (or stations) from FeatureCollection are obtained
    }
    
    
    private static String getMap(String url) {

        String result = "";
        
        try {
            URL mapUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
            conn.setReadTimeout(10000); // milliseconds
            conn.setConnectTimeout(15000); // milliseconds
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            Scanner reader = new Scanner(inputStream);
            while (reader.hasNext()) {
            		result += (reader.nextLine());
            }
            reader.close();
            inputStream.close();
            
        } catch (IOException e){
            e.printStackTrace();
        }

        return result;

    }

    void updateDroneAndStationOnGameMap(Drone drone, GameStatus gameStatus){
        Feature nearestStation = this.getNearestStation(drone.getCurrentPosition());
        if (nearestStation != null) {
            for (Feature station : stations) { // loop over all stations
                if (nearestStation.getProperty("id").equals(station.getProperty("id"))) {
                    double stationCoins = station.getProperty("coins").getAsDouble();
                    double stationPower = station.getProperty("power").getAsDouble();

                    double updatedCoins = gameStatus.getCoins() + stationCoins; // sum of drone's and station's coins
                    double updatedPower = gameStatus.getPower() + stationPower; // sum of drone's and station's power

                    double remainingStationCoins = 0.0;
                    double remainingStationPower = 0.0;

                    if (updatedCoins >= 0) { // if sum is positive,
                    		gameStatus.setCoins(updatedCoins); // drone's coins is updated to the sum
                    } else { 
                    		gameStatus.setCoins(0.0); // otherwise, drone's coins is set to 0,
                    		remainingStationCoins = updatedCoins; // and stations' coins is updated to the sum
                    }

                    if (updatedPower >= 0) { // if sum is positive,
                    		gameStatus.setPower(updatedPower); // drone's power is updated to the sum
                    } else {
                    		gameStatus.setPower(0.0); // otherwise, drone's power is set to 0,
                    		remainingStationPower = updatedPower; // and stations' power is updated to the sum
                    }

                    station.removeProperty("coins");
                    station.removeProperty("power");
                    station.addNumberProperty("coins", remainingStationCoins); // replace station's coins
                    station.addNumberProperty("power", remainingStationPower); // replace station's power
                }
            }
        }
    }

    void addDronePathToMap(ArrayList<Point> dronePath){
        Geometry geometryFlightPath = LineString.fromLngLats(dronePath);
		Feature featureFlightPath = Feature.fromGeometry(geometryFlightPath);
		stations.add(featureFlightPath);
    }

    public boolean isNearNegativeStation(Position position) { // boolean function that checks if nearest station from given position is negative station
        Feature nearestStation = this.getNearestStation(position);
        if (nearestStation != null){
            return (nearestStation.getProperty("coins").getAsDouble() < 0); // no need to check power because station either has positive or negative values for both
        }
        return false;

    }
    
    private Feature getNearestStation(Position currentPosition) { // returns nearest station from given position
        Feature nearestStation = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Feature station : stations) { // loop over all stations on map
            Point stationLocation = (Point) station.geometry(); // get station's location
            assert(stationLocation != null);
            double distance = calculateDistance(currentPosition, stationLocation);
            if (distance < nearestDistance && distance < 0.00025) { // 0.00025 degree is the allowed distance from station to get charged
                nearestStation = station;
                nearestDistance = distance;
            }
        }
        return nearestStation;
    }

    ArrayList<Feature> getNearbyStations(Position currentPosition) {
        ArrayList<Feature> nearbyStations = new ArrayList<>();
        for (Direction direction: Direction.values()) { // loop over all 16 directions
            Position nextPosition = currentPosition.nextPosition(direction); // compute next position when drone moves in given direction
            nearbyStations.add(this.getNearestStation(nextPosition)); // add nearest station from next position to array nearbyStations
        }
        return nearbyStations;
    }
    
    public static double calculateDistance(Position a, Point b) { // simple mathematical distance formula is used to compute
    		double x = a.longitude - b.longitude(); // distance between Position and Point
    		double y = a.latitude - b.latitude();
    		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }


}
