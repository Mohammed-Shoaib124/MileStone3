# TRAVEL PLANNER

## Overview
Welcome to the Travel Planner, a Java-based application designed to enhance your travel experience by providing comprehensive insights into weather conditions, optimal routes, and other crucial factors. Whether you're planning a road trip, business travel, or a leisurely journey, this program aims to make your travel decisions informed and efficient.

## Installation
### Prerequisites
Java Development Kit (JDK) installed on your machine.
CSV files containing weather, distance, and sea level data.
## File Paths: 
Adjust the following file paths in the Main.java file to point to your CSV files:
weather.csv  FilePath
distance.csv  FilePath
seaLevel.csv  FilePath
distance_matrix _without_citynames.csv
Also add the path of new_distance_matrics.csv file to the possible_paths_1.java 

## Input
- Starting city and state: [City-State]
- Destination city and state: [City-State]
- Date and time of commencement: yyyy-MM-dd HH:mm
## User Options :
1. Display All Possible Paths:
  a. Prompts for a travel range (in miles).
  b. Displays all available paths within the specified range.
2. Run Dijkstra's Algorithm:
  a. Finds the shortest path between the starting and destination cities         using Dijkstra's algorithm.
3. Run Bellman-Ford Algorithm:
  a. Determines the shortest path considering negative edge weights using        Bellman-Ford algorithm

## Steps
* EXECUTION OF TASK 1
- Extract all the files of the Algorithm folder 
- Add all the files into package folder into name "pkg".
- Then set the paths in the respective files mentioned above
- Run Main.java in local.
- Enter the inputs in the formats specified

* EXECUTION OF TASK 2
- Extract all the files in the project.
- After creating the project, add code to the main.java file and rename the file as CityGraph1.java
- To include the library files, follow this path: file>project sturucture>modules>dependencies>libraries
- In the CityGraph.class in the readDataFromCSV function provide the path of distance_matrics.csv file.
- In the CityGraph.class in the promptAndVisualize function provide the path of weatherdata.csv file and sealeveldata file 
  path
- Run CityGraph1.java
- Give the inputs in specified format and select option 1 to visualize all cities. 
- We get the visualization map as the output.

* EXECUTION OF TASK 3
- Execute Task 1 again in your IDE it will give you a text file as output.
- Download the text file.
- Extract all the files of the Vizualization folder and save the text file in that folder. 
- In the CityGraph1.java in the main class give the text file path.
- Run CityGraph1.java
- Give the inputs in specified format and select option 2 to shortest path.

## Output Format 
TASK 1 OUTPUT
For Dijkstra's:![TASK1FINAL](https://github.com/Mohammed-Shoaib124/MileStone3/assets/151399787/c2b37317-6963-41e5-8c33-3d15bb61ea0d)

For Bellman-Ford algorithm: 
![TASK1BFINAL](https://github.com/Mohammed-Shoaib124/MileStone3/assets/151399787/2d457b29-c644-4404-8652-ccb6204d0725)

TASK 2 OUTPUT
![TASK2](https://github.com/Mohammed-Shoaib124/Shortest-Path/assets/151399787/8f758d36-c9f6-4cc6-8b9b-070c02015c24)

TASK 3 OUTPUT
![TASK3](https://github.com/Mohammed-Shoaib124/Shortest-Path/assets/151399787/6fecaabe-df56-43cc-b742-49594cad8d2c)

![TASK3B](https://github.com/Mohammed-Shoaib124/Shortest-Path/assets/151399787/c6c76a3e-2b9a-4d7c-ae8b-fbac9aa9f7fb)

![TASK3G](https://github.com/Mohammed-Shoaib124/Shortest-Path/assets/151399787/47d90116-6aff-48ba-9ba4-4c96c7390599)


## SourceCode OnlineGDB

[SourceCode](https://onlinegdb.com/XfIqGzbXI)







