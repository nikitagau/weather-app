import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName){

          // declaration
        JSONArray locationData= getlocationData(locationName);
        //extract longitude and latitude data
        JSONObject location =(JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude="  + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto";
        try{

            // call api and response
            HttpURLConnection conn = fetchApiResponse(urlString);
            // check for response data
            // 200 means for successful connected
            if(conn.getResponseCode()!=200){
                System.out.println(" ERROR = could not connected to the API");
                return null;
            }
            // store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                // read and store into stringbuilder
                resultJson.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();
            //close url connection
            conn.disconnect();

            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj= (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");


            // we want to get the current hour's data
            // so we want to get the index of current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

           // get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get wind speed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed =  (double) windspeedData.get(index);

            // build the weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature" , temperature);
            weatherData.put("weather_condition",weatherCondition);
            weatherData.put("humidity",humidity);
            weatherData.put("windspeed",windspeed);

            return weatherData;


        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static JSONArray getlocationData(String locationName){
        locationName = locationName.replaceAll(" ","+");
        String urlstring ="https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            HttpURLConnection conn = fetchApiResponse(urlstring);

            // check response status
            // 200 means successfully connected
            if(conn == null){
                System.out.println("Error could not establish connection");
                return null;
            }
           else if(conn.getResponseCode()!= 200){
                System.out.println("error  : could not connect to API");
                return null;
            }
            else
            {
                // store the API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner= new Scanner(conn.getInputStream());

                // read and store the resulting json data into our stringBuilder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                //System.out.println(resultJson);
                // close scanner
                scanner.close();
                //close url disconnected
                conn.disconnect();

            // parse the json string into json obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API generated from the loaction name
                JSONArray locationData= (JSONArray) resultsJsonObj.get("results");
                //System.out.println(locationData);
                return locationData;
            }
        }catch(Exception e){
            e.printStackTrace();

        }
        // cannot find the loaction
        return null;
    }
    private  static HttpURLConnection fetchApiResponse( String urlString ){
        try{
           URL url= new URL(urlString);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.disconnect();

            return conn;

        }catch(Exception e ){
         e.printStackTrace();
        }
           return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timelist){
        String currentTime = getCurrentTime();
        //iterate through the time list and  match which one the current time
        for(int i=0;i<timelist.size();i++){
            String time = (String) timelist.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // return the index
                return i;
            }
        }
        return 0;
    }
    private static String getCurrentTime(){
        //get current data and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //FORMATE DATE TO BE 2023-09-02T00:00 (this is how is the read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYY-MM-dd'T'HH':00'");

      // formate and print the correct date and time
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }
   // convert the weather code to something more readable
    private static String convertWeatherCode( long weathercode){
        String  weatherCondition = "";
        if(weathercode == 0l){
          weatherCondition = "clear";
        }else if(weathercode>0l && weathercode <= 3l){
             weatherCondition = " Cloudy";
        }else if((weathercode >= 51l && weathercode <= 67l) || (weathercode >= 80l && weathercode <= 99l)){
            weatherCondition = "Rain";
        }else if(weathercode >= 71 && weathercode <= 77l){
          weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
