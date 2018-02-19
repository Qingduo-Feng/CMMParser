package SemanticAnalysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Symbol {

    private String name;
    private String type;
    private int level;
    private int[] intarr;
    private double[] douarr;
    //为每种类型声明一个value
    private Integer intValue;
    private Double floatValue;
    private Integer[] intArrValue;
    private Float[] floatArrValue;
    private String boolValue;

    public Symbol(String name, String type, int level){
        this.name = name;
        if (type.equals("int")){
            this.type = type;
            intValue = null;
        }
        else if (type.equals("double")){
            this.type = type;
            floatValue = null;
        }
        else if (type.equals("bool")){
            this.type = type;
            boolValue = null;
        }
        else{
            //没有识别到的类型
        }
        this.level = level;
    }

    public Symbol(String name, String type, int level, String value){
        this.name = name;
        if (type.equals("int")){
            this.type = type;
            intValue = Integer.valueOf(value);
        }
        else if (type.equals("double")){
            this.type = type;
            floatValue = Double.valueOf(value);
        }
        else if (type.equals("bool")){
            this.type = type;
            boolValue = value;
        }
        else{
            //没有识别到的类型
        }
        this.level = level;
    }

    public Symbol(String name, String type, int level, int[] array) {
        this.name = name;
        this.type = type;
        this.level = level;
        intarr = array;

    }

    public Symbol(String name, String type, int level, double[] array) {
        this.name = name;
        this.type = type;
        this.level = level;
        douarr = array;

    }

    public String getType(){
        return type.toString();
    }

    public int getLayer(){
        return level;
    }
    public int[] getIntarr()
    {
        return intarr;
    }
    public String getValue(){
        switch (type){
            case "int":
                return intValue.toString();
            case "bool":
                return boolValue.toString();
            case "double":
                return floatValue.toString();
            case "int[]":
                return "int[]";
            case "double[]":
                return "double[]";
            default:
                //报错
                return null;
        }
    }

    public String getName(){
        return name;
    }

    public double[] getDouarr() {
        return douarr;
    }

    public void setIntArray(int i, int i1) {
        intarr[i] = i1;
    }

    public void setDouArr(int i, double v) {
        douarr[i] = v;
    }

    public void setIntValue(Integer i){
        intValue = i;
    }

    public void setDoubleValue(Double i)
    {
        floatValue = i;
    }
}
