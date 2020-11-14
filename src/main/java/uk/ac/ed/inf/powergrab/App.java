package uk.ac.ed.inf.powergrab;

public class App 
{
    public static void main( String[] args ) {
    	        
    		String day = args[0];
    		String month = args[1];
    		String year = args[2];
    		double latitude = Double.parseDouble(args[3]);
    		double longitude = Double.parseDouble(args[4]);
    		long seed = Integer.parseInt(args[5]);
    		String droneType = args[6];

    		String url = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + year + '/' + month + '/' + day + "/powergrabmap.geojson"; // map URL
    		String fileName = droneType + "-" + day + "-" + month + "-" + year; // file name
    		
    		Game game = new Game(droneType, latitude, longitude, seed, url, fileName); // a new game is created using given input
        game.startGame(); // game starts
    	
    }
}
