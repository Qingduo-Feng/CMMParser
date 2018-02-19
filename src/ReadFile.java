/**
 * Created by qingduo-feng on 2017/10/9.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {
    private static String[] result;

    public static String[] readFromFile(String fileName){
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            ArrayList<String> resultList = new ArrayList<String>();
            // 一次读入一行，直到读入null为文件结束
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                resultList.add(tempString);
            }
            result = new String[resultList.size()];
            resultList.toArray(result);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return result;
    }
}
