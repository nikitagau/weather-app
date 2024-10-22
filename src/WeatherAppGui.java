import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGui(){
        // setup our gui anf add title
        super( "Weather App");
        // configure gui to end the program's process once's it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //set the size of our gui
        setSize(450,650);
        //load our gui at the centre of the screen
        setLocationRelativeTo(null);
        // make our layout manager null to manually position our component within the gui
        setLayout(null);
        //prevent any  resize of the gui
        setResizable(false);
         addGuiComponents();
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextFeild = new JTextField();
        // set the loactaion and size of our component
        searchTextFeild.setBounds(15,15,351,45);
        //change the fomt style and size
        searchTextFeild.setFont(new Font("Dialog", Font.PLAIN,24));
        add(searchTextFeild);



        //weather image
        JLabel weatherConditionImage= new JLabel(loadImage("src/assests/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);


        // temperature text
        JLabel temperatureText=new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("",Font.BOLD,48));
        temperatureText.setFont(new Font("Dialog",Font.BOLD,48));
        // centre the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);



        // weather condition description
         JLabel weatherconditiondesc = new JLabel("cloudy");
        weatherconditiondesc.setBounds(0,404,450,36);
        weatherconditiondesc.setFont(new Font("Dialog",Font.PLAIN,32));
        weatherconditiondesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherconditiondesc);


        // humidity image
        JLabel humidityImage= new JLabel(loadImage("src/assests/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        // humidity text
        JLabel humidityText =new JLabel("<html><b>Humidity</b>  100%</html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityText);

        // windspeed image
        JLabel windSpeedImage = new JLabel(loadImage("src/assests/windspeed.png"));
        windSpeedImage.setBounds(220,500,74,66);
        add(windSpeedImage);


        // windspeedtext
        JLabel windspeedText =  new JLabel("<html><b> windspeed </b> 15km/h</html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(windspeedText);

        // search button
        JButton searchButton = new JButton(loadImage("src/assests/search.png"));
        // change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextFeild.getText();
                 if(userInput.replaceAll("\\s", "").length()<=0){
                     return;
                 }
                 // retrieve weatherData
                weatherData = WeatherApp.getWeatherData(userInput);

                 // update weather gui

                //update weather image
                 String weatherCondition =  (String) weatherData.get("weather_condition");

                 // depending on the condition we will upadte the image that  corresponds with the condition
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assests/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assests/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assests/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assests/snow.png"));
                        break;

                }
                //update temperature text
                 double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "C");

                // update weather condition text
                weatherconditiondesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b> Humidity</b> "+ humidity +"%</html>");

                // update windspeed text
               double windspeed = (double) weatherData.get("windspeed");
               // System.out.println(windspeed);
              windspeedText.setText("<html><b>windspeed</b> "+windspeed+"km/h </html>");
                //System.out.println(windspeed);

            }
        });


        add(searchButton);



    }


    //used to create image in our gui component
    private ImageIcon loadImage(String resourcePath){
        try{
            //read the image file from the path given

            BufferedImage image = ImageIO.read(new File(resourcePath));
            // return an imageicon so taht our component can render it
            return new ImageIcon(image);

        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("could not find resource");
        return null;
    }

}
