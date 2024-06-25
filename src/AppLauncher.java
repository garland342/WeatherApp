import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Scanner;
import java.net.HttpURLConnection;
//import java.io.IOException;

public class AppLauncher {
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable(){
            @Override

            public void run(){
                new WeatherAppGui().setVisible(true);   
            }

            
        });


    }
}
