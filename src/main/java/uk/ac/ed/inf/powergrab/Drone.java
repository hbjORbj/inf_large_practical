package uk.ac.ed.inf.powergrab;

public abstract class Drone implements DroneInterface {
	
    private Position curPos; // current position

    public Drone(double latitude, double longitude, long seed) {
    		curPos = new Position(latitude, longitude);
    }

    public Position getCurrentPosition() { // returns current position of drone
        return curPos;
    }

    public void moveInDirection(Direction direction) { // void function that updates current position using given direction
    		curPos = curPos.nextPosition(direction);       // does not return anything
    }

}
