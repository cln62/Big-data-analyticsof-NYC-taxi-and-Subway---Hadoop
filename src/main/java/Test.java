import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {


    public static void main(String[] args) throws IOException, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        BufferedReader br = new BufferedReader(new FileReader("/home/iwantacat/Taxi/data/2.csv"));
        String line = br.readLine();
        while (line != null) {
            String lines[] = line.split(",");
            Date pickUp = df.parse(lines[1]);
            Date dropOff = df.parse(lines[2]);
            long tdiff = Math.abs(pickUp.getTime() - dropOff.getTime()) / 1000;
            if (dropOff.compareTo(pickUp) < 0) {
                tdiff = 12 * 60 * 60 - tdiff;
            }
            Float distance = Float.parseFloat(lines[3]);
            System.out.println(tdiff);
            System.out.println(distance);
            line = br.readLine();
        }
        br.close();
    }




}
