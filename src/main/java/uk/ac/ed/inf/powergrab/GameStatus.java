package uk.ac.ed.inf.powergrab;

public class GameStatus {
	public static double coins;
    public static double power;
    public static int availableMoves;

    GameStatus(double coins, double power, int availableMoves){
        this.coins = coins;
        this.power = power;
        this.availableMoves = availableMoves;
    }

    public boolean isFinished() {
        return (availableMoves <= 0 || power < 1.25); // drone needs 1.25 of power to make one move so if power < 1.25, game is finished
    }
    
    public double getCoins() {
        return coins;
    }

    public static double getPower() {
        return power;
    }
 
    public static double getAvailableMoves() {
        return availableMoves;
    }

    public void setCoins(double coins) {
        this.coins = coins;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void updateAvailableMoves() { // decrement drone's available move by one
        availableMoves -= 1;
    }
    
    public void updatePowerByOneMove() { // drone needs 1.25 of power to make one move
        		power -= 1.25;
    }

}
