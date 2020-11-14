package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

import com.mapbox.geojson.Point;

public class GameLogger {
    private String traceOfDrone;
    private ArrayList<Point> pathOfDrone;

    GameLogger(double latitude, double longitude){
    		traceOfDrone = ""; // String type that records every movement of drone during game
    		pathOfDrone = new ArrayList<>(); // array of drone's every location during game
        Point startingPoint = Point.fromLngLat(longitude, latitude); // drone's current location as type Point
        pathOfDrone.add(startingPoint); // append drone's first location to array pathOfDrone
    }


    public void logDrone(Position previousPosition, Direction direction, GameStatus gameStatus) {
        Position currentPosition = previousPosition.nextPosition(direction);
        String trace = previousPosition.latitude + "," + previousPosition.longitude + "," + direction +
                "," + currentPosition.latitude + "," + currentPosition.longitude + "," +
                gameStatus.getCoins() + "," + gameStatus.getPower() + "\n"; // this is the format required by coursework specification
        Point locationPoint = Point.fromLngLat(currentPosition.longitude, currentPosition.latitude); // drone's current location as type Point 
        traceOfDrone += trace; // drone's movement in format required by coursework specification is appended to String type
        pathOfDrone.add(locationPoint); // append drone's current location to array pathOfDrone
    }

    public String getDroneTrace() {
        return traceOfDrone;
    }

    public ArrayList<Point> getDronePath() {
        return pathOfDrone;
    }
}
