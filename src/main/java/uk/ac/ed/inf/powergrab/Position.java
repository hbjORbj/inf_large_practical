package uk.ac.ed.inf.powergrab;

import java.lang.Math;

public class Position {

	public double latitude; // y
	public double longitude; // x
	double degree;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double degreeOf(Direction direction) { // returns degree of given direction 
		if (direction == Direction.E)             // East is 0 degrees, North 90 degrees, 
			degree = 0;                           // West 180 degrees, South 270 degrees

		else if (direction == Direction.ENE)
			degree = 22.5;

		else if (direction == Direction.NE)
			degree = 45;

		else if (direction == Direction.NNE)
			degree = 67.5;

		else if (direction == Direction.N)
			degree = 90;

		else if (direction == Direction.NNW)
			degree = 112.5;

		else if (direction == Direction.NW)
			degree = 135;

		else if (direction == Direction.WNW)
			degree = 157.5;

		else if (direction == Direction.W)
			degree = 180;

		else if (direction == Direction.WSW)
			degree = 202.5;

		else if (direction == Direction.SW)
			degree = 225;

		else if (direction == Direction.SSW)
			degree = 247.5;

		else if (direction == Direction.S)
			degree = 270;

		else if (direction == Direction.SSE)
			degree = 292.5;

		else if (direction == Direction.SE)
			degree = 315;

		else if (direction == Direction.ESE)
			degree = 337.5;

		return degree;
	}

	public Position nextPosition(Direction direction) { // Computing next position based on given direction
		Position curPos = new Position(latitude, longitude); // current position
		double givenDegree = degreeOf(direction); // degree of given direction
		double newLatitude = 0.0003 * Math.sin(Math.toRadians(givenDegree)) + curPos.latitude; // latitude for next position
		double newLongitude = 0.0003 * Math.cos(Math.toRadians(givenDegree)) + curPos.longitude; // longitude for next position
		return new Position(newLatitude, newLongitude);
	}

	public boolean inLatitude() { // Checking whether or not drone is in allowed range of latitude
		return latitude > 55.942617 && latitude < 55.946233;
	}

	public boolean inLongitude() { // Checking whether or not drone is in allowed range of longitude
		return longitude > -3.192473 && longitude < -3.184319;
	}

	public boolean inPlayArea() { // Checking whether or not drone is in allowed range of play area
		return inLatitude() && inLongitude();
	}

}
