/**
 * Created by cln62 on 4/29/2018.
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class TaxiSpeed {

    public static class TaxiMapper extends Mapper<Object, Text, IntWritable, LongWritable> {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        /*@Override
        public void setup(Context context) throws IOException{
            Configuration configuration = context.getConfiguration();
        }*/

        @Override
        public void map(Object key, Text val, Context context) {
            String[] lines = val.toString().trim().split(",");
           try {
                Date pickUp = df.parse(lines[1]);
                Date dropOff = df.parse(lines[2]);
                long tdiff = Math.abs(pickUp.getTime() - dropOff.getTime()) / 1000;
                if (dropOff.compareTo(pickUp) < 0) {
                    tdiff = 12 * 60 * 60 - tdiff;
                }
                int distance = Integer.parseInt(lines[3]);
                context.write(new IntWritable(distance), new LongWritable(tdiff));
            }
            catch(Exception e)
            {
                e.getMessage();
            }
/*            try {
                int n1 = Integer.parseInt(lines[1]);
                int n2 = Integer.parseInt(lines[2]);
                long diff = n2 - n1;
                context.write(new LongWritable(10), new LongWritable(diff));
            }
            catch (Exception e) {
                e.getMessage();
            }*/
        }
    }

    public static class TaxiReducer extends Reducer<IntWritable, LongWritable, IntWritable, LongWritable> {
//        private LongWritable result = new LongWritable();
        @Override
        public void reduce(IntWritable key, Iterable<LongWritable> vals, Context context) throws IOException, InterruptedException {
            long sumTime = 0;
            long count = 0;
            for (LongWritable val : vals) {
                sumTime += val.get();
                count++;
            }
            long avgTime = sumTime / count;

//            result.set(avgTime);
            context.write(key, new LongWritable(avgTime));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        Job job = new Job(configuration);
        job.setJarByClass(TaxiSpeed.class);
        job.setMapperClass(TaxiMapper.class);
        job.setReducerClass(TaxiReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(LongWritable.class);
       // job.setInputFormatClass(Text.class);
        TextInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }
}