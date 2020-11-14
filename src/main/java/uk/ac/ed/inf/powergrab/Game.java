package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.util.ArrayList;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

public class Game {
	
	private Drone drone;
	private GameMap gameMap;
	private GameStatus gameStatus;
	private GameLogger gameLogger;
	private String fileName;

    public Drone createDrone(String droneType, double latitude, double longitude, long seed) {
		if (droneType.equals("stateless")) { // if drone type is stateless, Stateless drone is returned
			return new Stateless(latitude, longitude, seed);
		} else if (droneType.equals("stateful")) { // if drone type is stateful, Stateful drone is returned
			return new Stateful(latitude, longitude, seed);
		} else {
			return null;
		}
    }
    
    Game(String droneType, double latitude, double longitude, long seed, String URL, String fileName){
        drone = this.createDrone(droneType, latitude, longitude, seed); // this drone might be Stateless or Stateful depending on input
        gameMap = new GameMap(URL); // map is obtained using given URL
        gameStatus = new GameStatus(0, 250, 250); // (initialCoins, initialPower, maxMoves)
        gameLogger = new GameLogger(latitude, longitude); // this combination of latitude and longitude creates a value type of Point and 
        													 // this is added to path of drone as starting point in GameLogger class
        this.fileName = fileName; // this file name is used for output files
    }
    
    public void startGame() { // the method that starts game
        while (!gameStatus.isFinished()){ // while game is NOT finished
            Position previousPosition = drone.getCurrentPosition(); // drone's current position is saved to a variable previousPosition because
            														  // drone is about to move very soon
            Direction directionToMove = drone.getDirectionToMove(gameMap); // direction for drone to move is obtained
            drone.moveInDirection(directionToMove); // drone moves in the direction
            gameStatus.updateAvailableMoves(); // drone's available moves decrements by one
            gameStatus.updatePowerByOneMove(); // drone's power decrements by 1.25
            gameMap.updateDroneAndStationOnGameMap(drone, gameStatus); // coins and power of drone and a particular station are updated
            gameLogger.logDrone(previousPosition, directionToMove, gameStatus); // drone's movement and its current coins and power are logged
        }
        
        gameMap.addDronePathToMap(gameLogger.getDronePath()); // after game is finished, drone's entire navigation path is added to 
        String path = System.getProperty("user.dir");         // particular variable "stations" in GamaMap class; this variable contains all the stations on map
        FeatureCollection completeFc = FeatureCollection.fromFeatures(gameMap.stations); // creates a FeatureCollection using all the features from "stations" in GameMap class
        																				// this variable contains all the stations on map and drone's entire navigation path
        this.writeToFile(fileName + ".geojson", completeFc.toJson(), path); // The complete FeatureCollection is output as a GEOJSON file
        this.writeToFile(fileName + ".txt", gameLogger.getDroneTrace(), path ); // The collection of drone's every trace is output as a text file
    }
    
    public void writeToFile(String fileName, String content, String path) { // a common method that writes given contents to file
        try {
            FileWriter file = new FileWriter(fileName);
            file.write(content);
            file.close();
        } catch (Exception error) {
            System.out.println(error);
        }
    }
}
