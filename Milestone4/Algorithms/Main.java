package pkg;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.io.BufferedWriter;
import java.util.List;
import java.util.stream.Collectors;

import pkg.possible_paths_1.DistanceResult;

import java.io.FileWriter;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
	public static String shortest_path = null;

    public static void main(String[] args) {
    	
    	String formattedDateTime = null;
    	
    	String weatherCsvFilePath = "C:/Users/saini/eclipse-workspace/Milestone3/src/pkg/weather.csv";
        String distanceCsvFilePath = "C:/Users/saini/eclipse-workspace/Milestone3/src/pkg/distance.csv";
        String seaLevelCsvFilePath = "C:/Users/saini/eclipse-workspace/Milestone3/src/pkg/seaLevel.csv";
        String distancewithoutheaders = "C:/Users/saini/eclipse-workspace/Milestone3/src/pkg/distance_matrix_without_citynames.csv";
        
        System.out.print("Please provide the name of your starting city and state in the specified format: Ex: (MillCity-NV) ");
        Scanner sc = new Scanner(System.in);
        String startcity = sc.nextLine();
        System.out.println();
        
        System.out.print("Please provide your destination city in the same format: (City-State) ");
        String endcity = sc.nextLine();
        System.out.println();
        
        System.out.print("Enter date and time of your travel commencement (yyyy-MM-dd HH:mm): ");
        String userInput = sc.nextLine();
        System.out.println();
        
        if (userInput.isEmpty()) {
            System.out.println("Error: Please provide a non-empty input.");
            return;
        }
        
        try {
            // Parse user input into LocalDateTime
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(userInput, inputFormatter);

            // Format the LocalDateTime into the desired output format
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            formattedDateTime = localDateTime.format(outputFormatter);

        } catch (Exception e) {
            System.out.println("Error: Unable to parse the input. Please provide a valid date and time format.");
            return;
        }
        
        
        System.out.print("Please enter the average mileage of your vehicle: (in mpg)");
        int mileage = sc.nextInt();
        System.out.println();
        
        
        //Declaration of Objects outside the try-catch block, ensuring its access across the function.
        
        CityWeatherManager cityWeatherManager = null;
        DistanceDataManager distanceDataManager = null;
        Map<String, CityData> cityWeatherMap = null;
        List<List<String>> distanceData = null;
        List<List<String>> seaLeveldata = null;
        display_temperatures temperature = null;
        possible_paths_1 paths;
        SeaLevel seaLevel;
        
        
        // Instantiate classes
        
        try {
            cityWeatherManager = new CityWeatherManager();
            distanceDataManager = new DistanceDataManager();
            seaLevel = new SeaLevel();

            // Load weather data
            cityWeatherMap = cityWeatherManager.loadWeatherData(weatherCsvFilePath);

            // Load distance data
            distanceData = distanceDataManager.loadDistanceData(distanceCsvFilePath);

            // Load seaLevel data
            seaLeveldata = seaLevel.loadSeaLevel(seaLevelCsvFilePath);

            // Combine data
            cityWeatherManager.combineData(cityWeatherMap, distanceData, seaLeveldata);

            temperature = new display_temperatures(cityWeatherMap, formattedDateTime);

        } catch (IOException e) {
            e.printStackTrace();
        }

       
        System.out.println("Please choose any one of the following: ");
        System.out.println("1. Display all possible paths from " + startcity + " to " + endcity);
        System.out.println("2. Run Dijkstra's Algorithm ");
        System.out.println("3. Run Bellman-Ford Algorithm");
        
        int user_selection = sc.nextInt();
        
        
        int[][] adjacencymatrix = distanceDataManager.readCSV(distancewithoutheaders);
        
		if(user_selection == 1) {
			int iCity = distanceDataManager.getIndex(startcity);
			int jCity = distanceDataManager.getIndex(endcity);
			
        	System.out.println("Distance between " + startcity + " and " + endcity + " is " + adjacencymatrix[iCity][jCity]);
        	System.out.print("Please specify how many miles additionally you would like to travel : " );
            int range = sc.nextInt();
            sc.nextLine();
            System.out.println();
            System.out.print("Displaying all available paths in the within  " + ( adjacencymatrix[iCity][jCity] + range ) + " mi");
            
           
            paths = new possible_paths_1(distanceData, startcity, endcity, range, cityWeatherMap, temperature, mileage);
            List<List<String>> allpaths = paths.findAllPaths();
            
        }else if(user_selection == 2) {
        	
        	Dijkstras Dijkstra = new Dijkstras(startcity, endcity, temperature, adjacencymatrix, mileage);
        	shortest_path = Dijkstra.return_shortest_path();
//        	System.out.println("Dijkstra Shortest Path: " + shortest_path);
        	
        	
        }else if(user_selection == 3) {
        	
        	Graph bellman_ford = new Graph(120, adjacencymatrix, startcity, endcity, mileage, temperature);
            bellman_ford.find_and_addedges();
            shortest_path = bellman_ford.return_shortest_path();
//            System.out.println("Bellman Shortest Path: " + bellman_shortest);

            
        }else {
        	System.out.println("Error selection. Please try again !");
        	return;
        }
		
		if(shortest_path != null) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\saini\\eclipse-workspace\\Milestone3\\src\\pkg\\output_graphical.txt"))) {
			    writer.write(shortest_path);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
    }
}