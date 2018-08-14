
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class Subway {

    public static class Location {
        private Double lat;
        private Double lon;

        Location(Double lat, Double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }


    public static void main(String args[]) throws IOException, ParseException {
        XSSFWorkbook myWorkBook = new XSSFWorkbook("the input path of the source file");
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        int len = 30;
        HashMap<Integer, Location> map = new HashMap<Integer, Location>();
        HashMap<Double, List<Long>> map_result = new HashMap<Double, List<Long>>();
        Double dis = 0.0;

        Iterator<Row> rowIterator = mySheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            boolean isStation = false;
            int stationId = 0;
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (!isStation) {
                    stationId = (int) cell.getNumericCellValue();
                    isStation = true;
                }
                else {
                    String location = cell.getStringCellValue().trim();
                    String lat_tmp = location.split(" ")[0];
                    String lat = lat_tmp.substring(1);
                    String lon_tmp = location.split(" ")[1];
                    String lon = lon_tmp.substring(0, lon_tmp.length() - 1);
                    isStation = false;
                    if (!map.containsKey(stationId)) {
                        map.put(stationId, new Location(Double.parseDouble(lat), Double.parseDouble(lon)));
                    }
                }
            }
        }

        for (int i = 0; i < len; i++) {
            BufferedReader br2 = new BufferedReader(new FileReader("the input path of the source file"));
            String line = br2.readLine();
            List<Long> tdiffList = new ArrayList<Long>();
            boolean alreadyHasValue = false;
            while(line != null) {
                String[] tmp = line.split(",");
                if (tmp.length <= i + 1) {
                    break;
                }
                if (tmp[i] == null || tmp[i+ 1] == null || tmp[i].length() == 0 || tmp[i + 1].length() == 0) {
                    line = br2.readLine();
                    continue;
                }
                String regex=".*[a-zA-Z]+.*";
                Matcher m=Pattern.compile(regex).matcher(tmp[i]);
                if (m.matches()) {
                    if (alreadyHasValue) {
                        if (map_result.containsKey(dis)) {
                            map_result.get(dis).addAll(new ArrayList(tdiffList));
                        }
                        else {
                            map_result.put(dis, new ArrayList(tdiffList));
                        }
                        tdiffList.clear();
                        alreadyHasValue = false;
                    }
                    if (tmp[i].length() < 3 || tmp[i + 1].length() < 3) {
                        line = br2.readLine();
                        continue;
                    }
                    Integer stop1 = Integer.parseInt(tmp[i].substring(2));
                    System.out.println(i + 1);
                    Integer stop2 = Integer.parseInt(tmp[i + 1].substring(2));

                    if (map.containsKey(stop1) && map.containsKey(stop2)) {
                        dis = getDistance(map.get(stop1).lat, map.get(stop1).lon,
                                map.get(stop2).lat, map.get(stop2).lon);

                        alreadyHasValue = true;
                    }
                    else {
                        line = br2.readLine();
                        continue;
                    }
                }
                else if (!alreadyHasValue) {
                    line = br2.readLine();
                    continue;
                }
                else if (!(tmp[i].equals("666") || tmp[i + 1].equals("666")) ){
                    Date t1 = df.parse(tmp[i]);
                    Date t2 = df.parse(tmp[i + 1]);
                    long tdiff = Math.abs(t1.getTime() - t2.getTime()) / 1000;
                    if (t2.compareTo(t1) < 0) {
                        tdiff = 12 * 60 * 60 - tdiff;
                    }
                }
                line = br2.readLine();
                if (line == null) {
                    if (map_result.containsKey(dis)) {
                        map_result.get(dis).addAll(new ArrayList(tdiffList));
                    }
                    else {
                        map_result.put(dis, new ArrayList(tdiffList));
                    }
                }
            }
            br2.close();
        }

        Iterator iter = map_result.entrySet().iterator();
        int c = 0;
        ArrayList<String> list = new ArrayList<String>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            String key = entry.getKey().toString();
            List val = (List) entry.getValue();
            long t = 0;
            for( int i = 0 ; i < val.size(); i++) {
                t += (Long) val.get(i);
            }

            if (val.size() == 0) {
                continue;
            }
            long finalT = t/val.size();
            list.add(key + '-' + String.valueOf(finalT));
            System.out.println("distance: " + key + " miles, " + "average time: " +  finalT + " s");
            c++;
        }
        for (int i  = 0; i < list.size(); i++) {
            double dis1 = Double.valueOf(list.get(i).split("-")[0]);
            long t1 = Long.valueOf(list.get(i).split("-")[1]);
            String record = String.valueOf(dis1) + "-" + String.valueOf(t1);
            WriteStringToFile(record);
            for (int j = i + 1; j < list.size(); j++) {
                dis1 += Double.valueOf(list.get(j).split("-")[0]);
                t1 += Long.valueOf(list.get(j).split("-")[1]);
                String record2 = String.valueOf(dis1) + "-" + String.valueOf(t1);
                WriteStringToFile(record2);
            }
        }
        System.out.println(c);
    }


    public static Double getDistance(Double lat1, Double lon1, Double lat2, Double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void WriteStringToFile(String string2Write) {
        try {
            FileWriter fw = new FileWriter("the output path of the source file", true);
            BufferedWriter bw = new BufferedWriter(fw);
                bw.write(string2Write + "\n");
                bw.close();
                fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

