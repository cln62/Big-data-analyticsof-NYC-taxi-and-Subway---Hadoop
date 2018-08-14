import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BatchProcessing {

    public static void main(String[] args) throws IOException {
        HashMap<Long, ArrayList<Integer>> map = new HashMap<Long, ArrayList<Integer>>();

        File allFiles = new File("the input path of the source file");
        File[] fs = allFiles.listFiles();
        for (File f : fs) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            while (line != null) {
                String[] lines = line.split(",");
                Double distance = Double.parseDouble(lines[0]);

                int time = Integer.valueOf(lines[1]);
                long newDistance = Math.round(distance);
                if (map.containsKey(newDistance)) {
                    map.get(newDistance).add(time);
                }
                else {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(time);
                    map.put(newDistance, list);
                }
                line = br.readLine();
            }
            br.close();
        }
        FileWriter fw = new FileWriter("the output path of the source file");
        BufferedWriter bw = new BufferedWriter(fw);

        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry)iter.next();
            Long key = (Long) entry.getKey();
            ArrayList vals = (ArrayList) entry.getValue();
            int t = 0;
            for( int i = 0 ; i < vals.size(); i++) {
                t += (Integer) vals.get(i);
            }
            if (vals.size() == 0) {
                continue;
            }
            System.out.println(vals.size());
            int finalT = t/vals.size();
            System.out.println(key);
            System.out.println(finalT);
            bw.write(String.valueOf(key));
            bw.write(",");
            bw.write(String.valueOf(finalT));
            bw.newLine();
        }
        bw.close();
        fw.close();
    }
}
