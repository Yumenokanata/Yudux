package indi.yume.demo.newapplication.generators.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Created by yume on 2015/9/27.
 */
public class FileUtil {
    public static File newFile(String... paths){
        StringBuilder p = new StringBuilder();
        if(paths.length > 0)
            p.append(paths[0]);

        if(paths.length > 1)
            for(int i = 1; i < paths.length; i++)
                p.append(File.separator)
                        .append(paths[i]);

        return new File(p.toString());
    }

    public static void writeToFile(String string, File targetFile){
        if(!targetFile.exists()){
            try {
                targetFile.getParentFile().mkdirs();
                targetFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fs = new FileOutputStream(targetFile);
            PrintStream p = new PrintStream(fs);
            p.print(string);
            p.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File targetFile) {
        if(!targetFile.exists())
            throw new Error("File not exists.");

        int len = 0;
        StringBuilder str = new StringBuilder();
        try {
            FileInputStream is = new FileInputStream(targetFile);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader in= new BufferedReader(isr);

            String line = null;
            while( (line = in.readLine()) != null ) {
                if(len != 0)
                    str.append("\r\n").append(line);
                else
                    str.append(line);
                len++;
            }
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();

    }
}
