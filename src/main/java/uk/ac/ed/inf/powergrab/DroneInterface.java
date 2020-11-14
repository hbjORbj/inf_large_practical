package uk.ac.ed.inf.powergrab;

public interface DroneInterface {
    Position getCurrentPosition();
    void moveInDirection(Direction direction);
    Direction getDirectionToMove(GameMap gameMap); // this method is defined differently by Stateless and Stateful, respectively
}
