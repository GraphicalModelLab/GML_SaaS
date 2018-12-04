package org.graphicalmodellab.app.animationsystem.MotionCapReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by itomao on 10/1/18.
 */
public class BVHWriter {

    public static void main(String[] args) throws IOException {
        final File folder = new File("/Users/itomao/Documents/MotionCaptureCSV/Motion");
        final String outputDirectory = "/Users/itomao/Documents/MotionCaptureCSV/CSV";

        int totalRows = 0;
        String previousHeader = null;
        // Big Category
        for (final File directory : folder.listFiles()) {
            if (directory.isDirectory()) {
                // Sub Category
                for(final File subcategory : directory.listFiles()){

                    int count = 0;
                    if(subcategory.isDirectory()) {
                        for (final File file : subcategory.listFiles()) {
                            if(file.getName().endsWith("bvh")){
                                List<Object> result = toCSV(directory.getName(),subcategory.getName(),count,BVHReader.readMotion(file.getAbsolutePath(),"UTF-8"));

                                String[] tokens = file.getName().split("\\.");
                                FileWriter writer = new FileWriter(outputDirectory+"/"+tokens[0]+".csv");

                                String header = BVHReader.getFrameDataHeader(file.getAbsolutePath(),"UTF-8");

                                if(previousHeader != null){

                                    if(!previousHeader.equals(header)){
                                        System.out.println("Found different format");
                                    }

                                    previousHeader = header;
                                }
//                                System.out.println(header);
//                                System.out.println(file.getName());
                                System.out.println("num of headers:"+BVHReader.getFrameDataHeader(file.getAbsolutePath(),"UTF-8").split(",").length);
                                writer.append("category,sub_category,bvh_file_index,frame_no,"+BVHReader.getFrameDataHeader(file.getAbsolutePath(),"UTF-8")+"\n");
                                writer.append(result.get(1).toString());
                                writer.flush();
                                writer.close();

                                count++;

                                totalRows += (Integer)(result.get(0));
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Total column: "+totalRows);
    }

    public static List<Object> toCSV(String category, String subcategory, int BVH_Index, BVH_Parser.Motion motion){
        StringBuilder csv = new StringBuilder();

        BVH_Parser.Numbers nums = (BVH_Parser.Numbers)(motion.numbers());

        double[][] motion_data = BVHReader.getMotionData(nums);

        for(int j=0;j<motion_data.length;j++){
            double[] data = motion_data[j];

            csv.append(category+","+subcategory+","+BVH_Index+","+j+",");
            for(int i=0;i<data.length-1;i++){
                csv.append(data[i]+",");
            }

            csv.append(data[data.length-1]+"\n");
        }

        List<Object> result = new ArrayList<Object>();

        result.add(motion_data.length);
        result.add(csv.toString());
        return result;
    }
}
