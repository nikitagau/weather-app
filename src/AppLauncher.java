import javax.swing.SwingUtilities;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                // displat our weather app gui
                new WeatherAppGui().setVisible(true);
               // System.out.println(WeatherApp.getlocationData("Berlin"));
               // System.out.println(WeatherApp.getCurrentTime());

            }

        });
    }
}
