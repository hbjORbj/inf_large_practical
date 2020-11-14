package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Stateful extends Drone {
	
    Stateful(double latitude, double longitude, long seed) {
        super(latitude, longitude, seed);
    }
    

    private HashMap<Feature, Double> getHashMap(GameMap gameMap) {
        HashMap<Feature, Double> hashMap = new HashMap<>(); // creates a hash map with key being Feature (or station) and value being the station's value
        													   // the station's value will be computed below by evaluateStation() method
        ArrayList<Feature> gameMapStations = (ArrayList<Feature>) gameMap.stations.clone(); // creates an exact copy of 
        																					  // an array of all stations (or features) on given map

        gameMapStations.removeIf(station -> station.getProperty("coins").getAsDouble() <=  0.0); // removes negative stations

        for (int i = 0; i < gameMapStations.size(); i++) { // loop over all stations on map
        		Feature station = gameMapStations.get(i);	  
            Double valueOfStation = this.evaluateStation(station); // compute value of station
            hashMap.put(station, valueOfStation);                  // add to hash map with key being station (of type Feature) and 
            													      // value being the station's value computed by evaluateStation() method
        }

        return hashMap;
    }
    
    private double evaluateStation(Feature station) {
        double stationCoins = station.getProperty("coins").getAsDouble(); // given station's coins
        double stationPower = station.getProperty("power").getAsDouble(); // given station's power
        
        Point stationLocation = (Point) station.geometry(); // given station's location as type Point
        assert(stationLocation != null);
        double distance = GameMap.calculateDistance(this.getCurrentPosition(), stationLocation); // distance between drone's position and given station's location
        double reciprocalDistance = 1 / distance; // the shorter the distance, the larger the reciprocalDistance value
        double valueOfStation = (stationCoins + stationPower + reciprocalDistance); // the larger the valueOfStation, the better station it is
        return valueOfStation;
    }
    

    private Feature bestStation(GameMap gameMap){
        HashMap<Feature, Double> hashMap = this.getHashMap(gameMap); // get a hash map with key being Feature (or station) and value being the station's value
        
        if (hashMap.size() == 0) { // this is when there is no positive station on the map because we removed all negative stations from hash map above
            return null;
        }
        
        Double max = Collections.max(hashMap.values()); // find the maximum station's value 
        
        Feature bestStation = null; // initialise a variable bestStation of type Feature
        
		for (Feature station : hashMap.keySet()) {
			if (hashMap.get(station).equals(max)) { // find the station that has the maximum value
				bestStation = station; // set bestStation to the station
				break; // finish the loop
			}
		}
        
        return bestStation;
    }

    private Direction bestDirectionTowardStation(Feature station, GameMap gameMap) {
        Point stationLocation = (Point) station.geometry(); // given station's location as type Point
        double minDistance = Double.MAX_VALUE;
        Direction bestDirection = null; // initialise a variable bestDirection of type Direction
        for (Direction direction : Direction.values()){ // loop over all 16 directions defined in Direction class
            Position nextPosition = this.getCurrentPosition().nextPosition(direction); // compute next position when drone moves in given direction
            double distance = GameMap.calculateDistance(nextPosition, stationLocation); // computer distance between next position and given station's location
     
            if (gameMap.isNearNegativeStation(nextPosition)){
                continue; // immediately jump to the next iteration of the loop if the nearest station from next position is negative station
            }
            
            if (distance < minDistance){ // if the nearest station from next position is not negative station,
                minDistance = distance;  // minDistance = distance between next position and given station's location
                bestDirection = direction; // bestDirection = direction used to computer next position
            }
        }
        return bestDirection; // bestDirection returned when looping over all 16 directions is done
    }

    public Direction getDirectionToMove(GameMap gameMap) {
        Feature targetStation = this.bestStation(gameMap); 
        
        if (targetStation == null) { // this is when there is no positive station on the map 
            for (Direction direction: Direction.values()){
        			Position nextPosition = this.getCurrentPosition().nextPosition(direction);
        			if (!gameMap.isNearNegativeStation(nextPosition) && nextPosition.inPlayArea()) { // returns any direction that leads to position where
        				return direction;                                                            // nearest station is not negative and position is in play area
        			}
            }
        } 
                
        Direction bestDirection = this.bestDirectionTowardStation(targetStation, gameMap); // this is when there are one or more positive stations on the map 
        return bestDirection;
        
    }
    
}
