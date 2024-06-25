import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherApp {


    public static JSONObject getWeatherData(String locationName){
        
        JSONArray locationData = getLocationData(locationName);

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                StringBuilder resultJson = new StringBuilder();
                Scanner scan = new Scanner(conn.getInputStream());
                while(scan.hasNext()){
                    resultJson.append(scan.nextLine());
                }

                scan.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                //JSONObject hourlyData = (JSONObject) resultJsonObj.get("hourly");
                JSONObject current = (JSONObject) resultJsonObj.get("current");

                /*JSONArray time = (JSONArray) hourlyData.get("time");
                int index = findIndexOfCurrentTime(time);*/

                //JSONArray temperatureData = (JSONArray) hourlyData.get("temperature_2m");
                double temperature = (double) current.get("temperature_2m");

                //JSONArray weatherCode = (JSONArray) hourlyData.get("weather_code");
                String weatherCondition = convertWeatherCode((long) current.get("weather_code"));

                //JSONArray relativeHumidity = (JSONArray) hourlyData.get("relative_humidity_2m");
                long humidity = (long) current.get("relative_humidity_2m");

                //JSONArray windspeedData = (JSONArray) hourlyData.get(("wind_speed_10m"));
                double windspeed = (double) current.get("wind_speed_10m");

                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);

                return weatherData;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static HttpURLConnection fetchApiResponse(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }
    
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime(); 

        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formatted = currentDateTime.format(formatter);

        return formatted;
    }

    public static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";

        if(weatherCode == 0L){
            weatherCondition = "Clear";
        }else if(weatherCode <= 3L && weatherCode > 0L){
            weatherCondition = "Cloudy";
        }else if((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){
            weatherCondition = "Rain";
        }else if(weatherCode >= 71L && weatherCode <=77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }

    public static JSONArray getLocationData(String locationName){
        locationName = locationName.replaceAll("\\s", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                StringBuilder resultJson = new StringBuilder();
                Scanner scan = new Scanner(conn.getInputStream());
                while(scan.hasNext()){
                    resultJson.append(scan.nextLine());
                }

                scan.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        
        return null;
    }
}
