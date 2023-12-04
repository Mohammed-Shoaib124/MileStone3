import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.SparseGraph;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class CityGraph1 extends JFrame {

    private Map<String, Point> cities = new HashMap<>();
    private Map<String, Map<String, Double>> distances = new HashMap<>();

    private Map<String, Map<String, String>> weatherData = new HashMap<>();

    private Map<String, Color> stateColors = new HashMap<>();

    private Map<String, String> seaLevelData = new HashMap<>(); // New map for sea level data


    public CityGraph1() {
        setTitle("City Graph Visualization");
        setSize(1500, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Read data from CSV and populate 'cities', 'distances', and 'stateColors' maps
        readDataFromCSV("D:/distance_matrics - Copy.csv");

    }
    private Graph<String, String> createGraphForPath(String[] pathCities, double gasMileage) {

        Graph<String, String> graph = new DirectedSparseGraph<>();

        for (int i = 0; i < pathCities.length - 1; i++) {
            String cityName = pathCities[i];
            String neighborCity = pathCities[i + 1];
            cityName = cityName.trim().toLowerCase().replaceAll("\\s", "");
            neighborCity = neighborCity.trim().toLowerCase().replaceAll("\\s", "");

            //System.out.println("Processing cities: " + cityName + ", " + neighborCity);

            // Check if weather data exists for the city
            if (weatherData.containsKey(cityName) && weatherData.containsKey(neighborCity)) {
                Map<String, String> cityWeather = weatherData.get(cityName);
                Map<String, String> neighborWeather = weatherData.get(neighborCity);

                double distance = distances.get(cityName).get(neighborCity);

                // Create edge label with distance and gas consumption
                double gasConsumption = Math.round((distance / gasMileage) * 100.0) / 100.0;
                String weatherStatus = neighborWeather.containsKey("WeatherCondition") ? neighborWeather.get("WeatherCondition") : "N/A";
                String temperature = neighborWeather.get("Temperature");

                String edgeLabel = cityName + "-" + neighborCity + "-" + distance + "m" + "," + gasConsumption + "g" + "," + weatherStatus + "," + temperature + "f";

                // Add directed edge
                graph.addEdge(edgeLabel, cityName, neighborCity, EdgeType.DIRECTED);
            } else if (!weatherData.containsKey(cityName)) {
                //System.out.println("Weather data not available for city: " + cityName);
            }else if (!weatherData.containsKey(neighborCity)) {
                //System.out.println("Weather data not available for city: " + neighborCity);
            }
        }

        // Visualize the graph after creating it
        visualizeText(graph,gasMileage,pathCities);

        return graph;
    }

    private void visualizeText(Graph<String, String> graph, double gasMileage, String[] pathCities) {
        System.out.println("Graph Visualization:");

        double totalGasConsumption = 0.0;
        double totalDistanceCovered = 0.0;

        for (int i = 0; i < pathCities.length - 1; i++) {
            String vertex = pathCities[i].trim().toLowerCase().replaceAll("\\s", "");
            System.out.println("City: " + vertex);

            Collection<String> successors = graph.getSuccessors(vertex);

            for (String neighbor : successors) {
                String edgeName = vertex + "-" + neighbor;

                // Check if the edge is directed (forward connection)
                if (graph.findEdge(vertex, neighbor) != null) {
                    double distance = distances.get(vertex).get(neighbor);

                    System.out.println("------------------------------------------------------> " + neighbor + " | Distance: " + distance + " miles");

                    // Check if weather data exists for the connected city
                    if (weatherData.containsKey(neighbor)) {
                        Map<String, String> neighborWeather = weatherData.get(neighbor);

                        // Print temperature information for the connected city
                        System.out.println("    Weather Information:");
                        String weatherStatus = neighborWeather.containsKey("WeatherCondition") ? neighborWeather.get("WeatherCondition") : "N/A";
                        String temperature = neighborWeather.get("Temperature");
                        String datetime = neighborWeather.get("DateTime");

                        System.out.println("WeatherCondition: " + weatherStatus);
                        System.out.println("Temperature: " + temperature + " F");
                        System.out.println("DateTime: " + datetime);

                        // Print temperature, unit, and sea_level information
                        String seaLevel = seaLevelData.containsKey(vertex) ? seaLevelData.get(vertex) : "N/A";
                        System.out.println("  Sea Level: " + seaLevel);

                        // Print gas consumption information for the connected city
                        double gasConsumption = distance / gasMileage;
                        System.out.println("    Gas Consumption: " + gasConsumption + " gallons");

                        // Update total gas consumption and distance
                        totalGasConsumption += gasConsumption;
                        totalDistanceCovered += distance;
                    } else {
                        System.out.println("    No weather data available for the connected city. (Check city name matching)");
                    }

                    System.out.println(); // Add a new line between connected cities
                }
            }
            System.out.println(); // Add a new line between cities
        }

        // Print total gas usage and distance covered
        System.out.println("Total Gas Usage: " + totalGasConsumption + " gallons");
        System.out.println("Total Distance Covered: " + totalDistanceCovered + " miles");
    }





    private void visualizePath(String path, double gasMileage)
    {

        String[] pathCities = path.split(",");
//        for(int i=0; i< pathCities.length; i++){
//            System.out.println(pathCities[i]);
//        }
//        System.out.println(pathCities);
        Graph<String, String> pathGraph = createGraphForPath(pathCities,gasMileage);
        visualizeGraph(pathGraph,pathCities,gasMileage);
    }
    private void visualizeFullGraph(double gasMileage) {
        //Graph<String, String> graph1 = createGraph();
        visualizefullGraph(gasMileage);
    }





    private void visualizefullGraph(double gasMileage){
        // Create JUNG graph
        Graph<String, String> graph = createGraph(gasMileage);

        printGraphInformation(graph,gasMileage);

        // Create JUNG layout using SpringLayout
        Layout<String, String> layout = new SpringLayout<>(graph);

        // Create visualization viewer
        VisualizationViewer<String, String> vv = new VisualizationViewer<>(layout, new Dimension(1500, 800));

        // Customize edge labels
        vv.getRenderContext().setEdgeLabelTransformer(e -> e.substring(e.lastIndexOf('-') + 1));

        // Customize vertex labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());

        // Customize vertex colors based on state ID
        vv.getRenderContext().setVertexFillPaintTransformer(vertex -> stateColors.get(vertex));

        getContentPane().add(vv);
        setVisible(true);

        // Animate the graph layout
        ScalingControl scaler = new CrossoverScalingControl();

        DefaultModalGraphMouse<String, String> graphMouse = new DefaultModalGraphMouse<>();
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING); // Set the default mode
        vv.setGraphMouse(graphMouse);
        vv.getRenderContext().setMultiLayerTransformer(vv.getRenderContext().getMultiLayerTransformer());
        vv.scaleToLayout(scaler);

        // This timer controls the speed of the animation
        Timer timer = new Timer(100, e -> {
            scaler.scale(vv, 1.03f, vv.getCenter());
            vv.repaint();
        });

        timer.setRepeats(true);
        timer.start();
    }



    private void visualizeGraph(Graph<String, String> pathGraph, String[] pathCities,double gasMileage) {

        Graph<String, String> graph = pathGraph;

        // Declare layout outside the if block
        Layout<String, String> layout;

        if (pathCities.length < 120) {
            layout = new ISOMLayout<>(graph);
        } else {
            layout = new KKLayout<>(graph);
        }
        // Adjust layout size
        Dimension layoutSize = new Dimension(1500, 800);
        layout.setSize(layoutSize);

        // Create visualization viewer
        VisualizationViewer<String, String> vv = new VisualizationViewer<>(layout, layoutSize);

        // Customize edge labels
        vv.getRenderContext().setEdgeLabelTransformer(e -> e.substring(e.lastIndexOf('-') + 1));

        // Customize vertex labels
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());

        // Customize vertex colors based on state ID
        vv.getRenderContext().setVertexFillPaintTransformer(vertex -> stateColors.get(vertex));

        getContentPane().removeAll(); // Remove previous components
        getContentPane().add(vv);
        setVisible(true);

        // Animate the graph layout
        ScalingControl scaler = new CrossoverScalingControl();

        DefaultModalGraphMouse<String, String> graphMouse = new DefaultModalGraphMouse<>();
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING); // Set the default mode
        vv.setGraphMouse(graphMouse);
        vv.getRenderContext().setMultiLayerTransformer(vv.getRenderContext().getMultiLayerTransformer());
        vv.scaleToLayout(scaler);
    }

    private Graph<String, String> createGraph(double gasMileage) {
        Graph<String, String> graph = new SparseGraph<>();

        for (String cityName : cities.keySet()) {
            graph.addVertex(cityName);
        }

        for (Map.Entry<String, Map<String, Double>> entry : distances.entrySet()) {
            String cityName = entry.getKey();
            Map<String, Double> cityDistances = entry.getValue();

            if (cityDistances != null) {
                for (Map.Entry<String, Double> neighborEntry : cityDistances.entrySet()) {
                    String neighborCity = neighborEntry.getKey();
                    double distance = neighborEntry.getValue();

                    // Check if weather data exists for the connected city
                    if (weatherData.containsKey(neighborCity)) {
                        Map<String, String> neighborWeather = weatherData.get(neighborCity);

                        double gasConsumption = Math.round((distance /  gasMileage) * 100.0) / 100.0;
                        String weatherStatus = neighborWeather.containsKey("WeatherCondition") ? neighborWeather.get("WeatherCondition") : "N/A";

                        String temperature = neighborWeather.get("Temperature");


                        String edgeLabel = cityName + "-" + neighborCity + "-" + distance + "m" + "," + gasConsumption + "g" + "," + weatherStatus+"," + temperature+"f";

                        String edge_distance = cityName + "-" + neighborCity + "-" + distance;

                        // Check if the edge already exists
                        boolean edgeExists = false;
                        for (String existingEdge : graph.getEdges()) {
                            if (existingEdge.startsWith(cityName + "-" + neighborCity) || existingEdge.startsWith(neighborCity + "-" + cityName)) {
                                edgeExists = true;
                                break;
                            }
                        }

                        // Check if the distance is within the desired range and the edge does not exist
                        if (!edgeExists && distance > 0 && distance <= 200) {
                            // Limit the connections based on the distance
                            graph.addEdge(edgeLabel, cityName, neighborCity, EdgeType.UNDIRECTED);
                        }
                    }
                }
            }
        }

        return graph;
    }

    private void printGraphInformation(Graph<String, String> graph,double gasMileage) {
        System.out.println("Graph Information:");

        for (String vertex : graph.getVertices()) {
            System.out.println("City: " + vertex);

            // Iterate over neighbors (connected cities)
            Collection<String> neighbors = graph.getNeighbors(vertex);
            for (String neighbor : neighbors) {
                String edgeName = vertex + "-" + neighbor;

                // Print edge information (distance)
                double distance = distances.get(vertex).get(neighbor);
                System.out.println("  Connected to " + neighbor + " | Distance: " + distance + " miles");

                // Check if weather data exists for the connected city
                if (weatherData.containsKey(neighbor)) {
                    Map<String, String> neighborWeather = weatherData.get(neighbor);

                    // Print temperature information for the connected city
                    String weatherStatus = neighborWeather.containsKey("WeatherCondition") ? neighborWeather.get("WeatherCondition") : "N/A";
                    String temperature = neighborWeather.get("Temperature");
                    String datetime = neighborWeather.get("DateTime");

                    System.out.println("WeatherCondition :"+ weatherStatus);
                    System.out.println("Temperature :"+ temperature);
                    System.out.println("DateTime :"+ datetime);

                    // Print gas consumption information for the connected city
                    System.out.println("    Gas Consumption: " + (distance / gasMileage) + " gallons");
                } else {
                    System.out.println("    No weather data available for the connected city. (Check city name matching)");
                }

                System.out.println(); // Add a new line between connected cities
            }
        }
    }

    private void readDataFromCSV(String distanceCsvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(distanceCsvFile))) {
            String line;
            boolean headerSkipped = false;
            String[] cityNames = null; // Store city names

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;

                    // Check if the first row contains city names
                    if (line.contains(",")) {
                        cityNames = line.split(",");
                    } else {
                        // If not, use the first column as city names
                        cityNames = new String[]{line};
                        // Add a default city name at 0,0 position if it's empty
                        if (cityNames[0].trim().isEmpty()) {
                            cityNames[0] = "defaultcity";
                        }
                    }

                    continue; // Skip the header row
                }

                String[] parts = line.split(",");
                String originCity = parts[0].trim().toLowerCase().replaceAll("\\s", ""); // Convert to lowercase and remove spaces
                String stateId = extractStateId(originCity);

                cities.putIfAbsent(originCity, getRandomPoint());

                // Get or generate a color for the state based on the entire state ID
                Color stateColor = stateColors.computeIfAbsent(stateId, k -> getRandomColor());

                // Store state ID to color mapping
                stateColors.putIfAbsent(originCity, stateColor);

                // Iterate over distances and add them to the map
                for (int i = 1; i < parts.length; i++) {
                    String destinationCity = cityNames[i].trim().toLowerCase().replaceAll("\\s", ""); // Convert to lowercase and remove spaces
                    String distanceStr = parts[i].trim();

                    double distance = distanceStr.equals("0") ? Double.MAX_VALUE : Double.parseDouble(distanceStr);

                    cities.putIfAbsent(destinationCity, getRandomPoint());

                    distances.putIfAbsent(originCity, new HashMap<>());
                    distances.get(originCity).put(destinationCity, distance);

                    String destStateId = extractStateId(destinationCity);
                    Color destStateColor = stateColors.computeIfAbsent(destStateId, k -> getRandomColor());
                    stateColors.putIfAbsent(destinationCity, destStateColor);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
    private void processWeatherDataFromCSV(String weatherCsvFile, String seaLevelCsvFile, String inputDate, String inputTime, int choice, String path) {
        if (choice == 1) {
            // Full Visualization
            processWeatherDataForFullVisualization(weatherCsvFile, seaLevelCsvFile, inputDate, inputTime);
        } else if (choice == 2) {
            // Path Visualization
            processWeatherDataForPathVisualization(weatherCsvFile, seaLevelCsvFile, inputDate, inputTime, path);

        } else {
            System.out.println("Invalid choice. Exiting...");
        }
    }

    private void processWeatherDataForFullVisualization(String weatherCsvFile, String seaLevelCsvFile, String inputDate, String inputTime) {
        try (BufferedReader br = new BufferedReader(new FileReader(weatherCsvFile))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header row
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String cityName = parts[0].trim().toLowerCase().replaceAll("\\s", ""); // CityName-StateID


                    String dateTimeStr = parts[1].trim();
                    String dateStr = dateTimeStr.split("T")[0];

                    String timeStr = dateTimeStr.split("T")[1].split(":")[0] + ":00";
                    String weatherCondition = parts[2].trim().toLowerCase().replaceAll("\\s", "");
                    String temperatureStr = parts[3].trim();
                    // Check if the current row matches the input date and time
                    if (isMatchingDateTime(dateStr, timeStr, inputDate, inputTime)) {
                        // Add the weather data to the map
                        Map<String, String> cityWeather = weatherData.computeIfAbsent(cityName, k -> new HashMap<>());
                        cityWeather.put("DateTime", dateStr + " " + timeStr);
                        //System.out.println(cityWeather);
                        cityWeather.put("WeatherCondition", weatherCondition);
                        cityWeather.put("Temperature", temperatureStr+" F");
                    }
                }
            }


        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        processSeaLevelDataFromCSV(seaLevelCsvFile);
    }
    private void processWeatherDataAndUpdateTime(String weatherCsvFile, String cityName, String currentDate, String currentTime, double travelTimeInHours) {
        try (BufferedReader br = new BufferedReader(new FileReader(weatherCsvFile))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header row
                }

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String currentCityName = parts[0].trim().toLowerCase().replaceAll("\\s", "");

                    // Check if the current row matches the specified city
                    if (currentCityName.equals(cityName)) {
                        String dateTimeStr = parts[1].trim();
                        String dateStr = dateTimeStr.split("T")[0];
                        String timeStr = dateTimeStr.split("T")[1].split(":")[0] + ":00";

                        // Parse the current date and time
                        LocalDate currentDateObj = LocalDate.parse(currentDate);
                        LocalTime currentTimeObj = LocalTime.parse(currentTime);

                        // Parse the date and time from the CSV file
                        LocalDate dataDate = LocalDate.parse(dateStr);
                        LocalTime dataTime = LocalTime.parse(timeStr);

                        // Calculate new time based on travel time
                        LocalDateTime updatedDateTime = LocalDateTime.of(dataDate, dataTime)
                                .plusHours((long) travelTimeInHours);

                        // Check if the updated time exceeds 23:59
                        if (updatedDateTime.toLocalTime().isAfter(LocalTime.parse("23:59"))) {
                            // Increment date by 1 day and reset time to midnight
                            updatedDateTime = updatedDateTime.plusDays(1).with(LocalTime.MIDNIGHT);
                        }

                        String updatedDateTimeStr = updatedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        // Add the weather data to the map
                        Map<String, String> cityWeather = weatherData.computeIfAbsent(cityName, k -> new HashMap<>());
                        cityWeather.put("DateTime", updatedDateTimeStr);
                        String weatherCondition = parts[2].trim().toLowerCase().replaceAll("\\s", "");
                        cityWeather.put("Temperature", parts[3].trim());
                        cityWeather.put("WeatherCondition", weatherCondition);

                        // Update the current time for the next iteration
                        currentTime = updatedDateTime.toLocalTime().toString();
                        currentDate = updatedDateTime.toLocalDate().toString(); // Update current date
                    }
                }
            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void processWeatherDataForPathVisualization(String weatherCsvFile, String seaLevelCsvFile, String inputDate, String inputTime, String path) {
        String[] cities = path.split(",");
        String currentDate = inputDate;
        String currentTime = inputTime;

        for (String city : cities) {
            processWeatherDataAndUpdateTime(weatherCsvFile, city.trim(), currentDate, currentTime, 0);
        }

        for (int i = 0; i < cities.length - 1; i++) {
            String startCity = cities[i].trim().toLowerCase().replaceAll("\\s", "");
            String endCity = cities[i + 1].trim().toLowerCase().replaceAll("\\s", "");

            // Calculate travel time based on assumed speed limit (e.g., 50 miles per hour)
            double distance = getDistanceBetweenCities(startCity, endCity);
            double speedLimit = 50; // Adjust the speed limit as needed
            double travelTimeInHours = distance / speedLimit;

            // Retrieve weather data for the end city with updated time and date
            boolean updated = retrieveWeatherDataAndUpdateTime(weatherCsvFile, startCity, travelTimeInHours, currentDate, currentTime);

            // Stop updating if no more data available
            if (!updated) {
                break;
            }

            // Update current date for the next city
            Map<String, String> startCityWeather = weatherData.get(startCity);
            if (startCityWeather != null) {
                currentDate = startCityWeather.get("DateTime").split(" ")[0];
                currentTime = startCityWeather.get("DateTime").split(" ")[1];
            }
        }

        processSeaLevelDataFromCSV(seaLevelCsvFile);
    }

    private boolean retrieveWeatherDataAndUpdateTime(String weatherCsvFile, String cityName, double travelTimeInHours, String currentDate, String currentTime) {
        // Use the same method for retrieving and updating weather data
        processWeatherDataAndUpdateTime(weatherCsvFile, cityName, currentDate, currentTime, travelTimeInHours);
        // Assuming that the update will always be successful in this context
        return true;
    }

    private double getDistanceBetweenCities(String startCity, String endCity) {
        // Check if startCity and endCity exist in distances map


        Map<String, Double> startCityDistances = distances.get(startCity);
        if (distances.containsKey(startCity) && distances.containsKey(endCity)) {

            // Check if endCity exists in the distances for startCity
            if (startCityDistances.containsKey(endCity)) {
                // Retrieve the distance
                return startCityDistances.get(endCity);
            } else {
                // Handle the case where the distance is not available
                System.out.println("Distance not available for " + startCity + " to " + endCity);
            }
        }
        // Return a default distance or handle the situation as appropriate for your logic
        return 0.0; // You may want to adjust this default value
    }
    private boolean isMatchingDateTime(String dataDate, String dataTime, String inputDate, String inputTime) {
        // Add your logic to check if the data date and time match the input date and time
        return dataDate.equals(inputDate) && dataTime.equals(inputTime);
    }

    private void processSeaLevelDataFromCSV(String seaLevelCsvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(seaLevelCsvFile))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header row
                }

                String[] parts = line.split(","); // Assuming tab-separated values


                String cityName = parts[0].trim().toLowerCase().replaceAll("\\s", ""); // CityName-StateID

                String seaLevelStr = parts[1].trim();

                // Store sea level data in your map or perform any other processing
                seaLevelData.put(cityName, seaLevelStr);

            }
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void promptAndVisualize(String path) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the date (YYYY-MM-DD):");
        String inputDate = scanner.next();

        System.out.println("Enter the time (HH:mm):");
        String inputTime = scanner.next();


        System.out.println("Choose an option:");
        System.out.println("1. Visualize full graph");
        System.out.println("2. Visualize a specific path");
        int choice = scanner.nextInt();

        if (choice == 1) {
            System.out.println("Enter gas mileage:");
            double gasMileage = scanner.nextDouble();
            processWeatherDataFromCSV("D:/New folder/weather (1).csv", "D:/Weather data_120 cities - Sheet1.csv", inputDate, inputTime, choice, "");
            visualizeFullGraph(gasMileage);
        } else if (choice == 2) {
//            System.out.println("Enter the path (comma-separated cities):");
//            scanner.nextLine(); // Consume the newline character
//            String path = scanner.nextLine();

            System.out.println("Enter gas mileage:");
            double gasMileage = scanner.nextDouble();
            processWeatherDataFromCSV("D:/New folder/weather (1).csv", "D:/New folder/New folder/sealevelforallcities.csv", inputDate, inputTime, choice, path);

            visualizePath(path, gasMileage);


        } else {
            System.out.println("Invalid choice. Exiting...");
        }

        scanner.close();
    }
    private String extractStateId(String city) {
        return city.substring(city.lastIndexOf('-') + 1).trim();
    }

    private Point getRandomPoint() {
        int x = (int) (Math.random() * 500) + 50; // Adjust the range as needed
        int y = (int) (Math.random() * 500) + 50;
        return new Point(x, y);
    }

    private Color getRandomColor() {
        // Generate a random color
        return new Color((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
    }
    private String visualizePathFromFile(String outputPath, double gasMileage) {
        String pathCitiesString = null;
        try (BufferedReader br = new BufferedReader(new FileReader(outputPath))) {
            String line;
            StringBuilder content = new StringBuilder();

            // Read all lines
            while ((line = br.readLine()) != null) {
                content.append(line);
            }

            // Remove square brackets and split the line using ","
            String[] pathCitiesWithState = content.toString().replace("[", "").replace("]", "").split(",");
            String[] pathCities = new String[pathCitiesWithState.length];

            // Extract cities from "City-State" format
            for (int i = 0; i < pathCitiesWithState.length; i++) {
                String[] cityStatePair = pathCitiesWithState[i].split("-");
                if (cityStatePair.length == 2) {
                    pathCities[i] = cityStatePair[0] + "-" + cityStatePair[1]; // Join city and state back
                } else {
                    System.out.println("Invalid format in the output file: " + pathCitiesWithState[i]);
                    // Handle the error or skip the invalid entry as needed
                }
            }
            // Convert pathCities array to a single string
            String dummyValue = " ";
            pathCitiesString = String.join(",", pathCities);
            pathCitiesString += "," + dummyValue;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathCitiesString;
    }
    // Modify the main method as follows
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CityGraph1 cityGraph = new CityGraph1();
            int gasMilege = 0;
            //Uncomment the line below and provide the correct path to the output file
            String pathcities = cityGraph.visualizePathFromFile("C:/Users/samin/Downloads/output_graphical.txt",gasMilege);
//            System.out.println(Arrays.toString(pathcities));
//            System.out.println("Output: " + pathString);
            cityGraph.promptAndVisualize(pathcities);

            //cityGraph.visualizePath("MillCity-NV,Eureka-NV,Wells-NV,Howe-ID,Jackpot-NV,Montello-NV,Owyhee-NV,Eugene-OR,Gresham-OR,Hillsbboro-OR,Bend-OR,Beaverton-OR");
        });
    }
}



