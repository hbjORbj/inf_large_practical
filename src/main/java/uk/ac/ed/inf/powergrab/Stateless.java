package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.mapbox.geojson.Feature;

public class Stateless extends Drone {
	
	private Random randomNumberGenerator;
	
	Stateless(double latitude, double longitude, long seed) {
		super(latitude, longitude, seed);
		randomNumberGenerator = new Random(seed); // random number generator is created using given seed
	}
	
	
	public Double getSumOfCoinsAndPower(Feature station) {
		double stationCoins = station.getProperty("coins").getAsDouble();
		double stationPower = station.getProperty("power").getAsDouble();
		return (stationCoins + stationPower); // sum of given station's coins and power 
	}
	
	public HashMap<Direction, Double> getHashMap(GameMap gameMap) { // hash map with key being Direction and value being sum of coins and power of station nearest 
																   // to position when drone moves in the direction (key)
		Position currentPosition = this.getCurrentPosition();
		ArrayList<Feature> nearbyStations = gameMap.getNearbyStations(currentPosition);
		HashMap<Direction, Double> hashMap = new HashMap<>();
		
		for (int i = 0; i < nearbyStations.size(); i++){
			Double sumOfCoinsAndPower = 0.0; // if station == null, sumOfCoinsAndPower is 0
			if (nearbyStations.get(i) != null) {
				sumOfCoinsAndPower = this.getSumOfCoinsAndPower(nearbyStations.get(i));
			} 
			hashMap.put(Direction.values()[i], sumOfCoinsAndPower); // key being Direction and value being sum of coins and power of station nearest
																   // to position when drone moves in the direction (key)
		}
		
		return hashMap;
	}
	
	public Direction getDirectionToMove(GameMap gameMap) {
		HashMap<Direction, Double> hashMap = this.getHashMap(gameMap);
		hashMap.entrySet().removeIf(element -> (!this.getCurrentPosition().nextPosition(element.getKey()).inPlayArea())); // if next position after moving in particular direction is not in play area,
																									      // remove the direction-sumOfCoinsAndPower pair from hashMap
		Double maxSumOfCoinsAndPower = Collections.max(hashMap.values()); // maximum sum of coins and power among nearby stations
		
		if (maxSumOfCoinsAndPower > 0.0) { // in case there are one or more positive stations nearby
			Direction resultingDirection = null;
			for (Direction direction : hashMap.keySet()) { // loop over all 16 directions
				if (hashMap.get(direction).equals(maxSumOfCoinsAndPower)) { 
					resultingDirection = direction; // the direction in which drone moves and finds its nearest station with maximum sum of coins and power
					break; // finish the loop immediately
				}
			}
			return resultingDirection;
		} else {		// in case there is no positive station nearby
			int randomIndex = this.randomNumberGenerator.nextInt(hashMap.size()); // choosing random number between 0 
																				// and (the number of directions in hashmap - 1) inclusive
			Direction randomDirection = (Direction) hashMap.keySet().toArray()[randomIndex];
			return randomDirection;			// return random direction
		}
	}

}
