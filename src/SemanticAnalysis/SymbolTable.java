package SemanticAnalysis;


import java.util.ArrayList;

public class SymbolTable {
    //用单符号表组织
    private int largestLayer = 0;
    private ArrayList<Symbol> symbolList = new ArrayList<Symbol>();
    //插入表项
    public boolean insert(Symbol symbol){
        symbolList.add(symbol);
        if (symbol.getLayer() > largestLayer){
            largestLayer = symbol.getLayer();
        }
        return true;
    }
    //删除表项
    public boolean deleteLayer(int layer){
        ArrayList<Integer> indexs = new ArrayList<Integer>();
        for (int i = 0; i < symbolList.size();i++){
            if (symbolList.get(i).getLayer() >= layer && layer != 0){
                indexs.add(i);
            }
        }
        for (int i = indexs.size() - 1; i >= 0; i--){
            symbolList.remove(indexs.get(i).intValue());
        }
        largestLayer--;
        return false;
    }
    //修改表项
    public boolean modify(Symbol symbol){
        String value = symbol.getName();
//        for (Symbol tempSymbol: symbolList){
//            if (tempSymbol.getName().equals(value)){
//                symbolList.remove(tempSymbol);
//                symbolList.add(symbol);
//                return true;
//            }
//        }
        for (int i = 0;i < symbolList.size(); i++){
            if (symbolList.get(i).getName().equals(value)){
                if (symbol.getType().equals("int")){
                    symbolList.get(i).setIntValue(Integer.valueOf(symbol.getValue()));
                }
                else if (symbol.getType().equals("double")){
                    symbolList.get(i).setDoubleValue(Double.valueOf(symbol.getValue()));
                }
                return true;
            }
        }
        return false;
    }
    //查询表项
    public Symbol searchTable(String name){
        ArrayList<Symbol> result = new ArrayList<Symbol>();
        for (Symbol tempSymbol: symbolList){
            if (tempSymbol.getName().equals(name)){
                result.add(tempSymbol);
            }
        }
        Symbol maxlayerSymbol = null;
        int maxLayer = 0;
        if (result.size() > 1){
            for (Symbol symbol: result){
                if (symbol.getLayer() >= maxLayer){
                    maxLayer = symbol.getLayer();
                    maxlayerSymbol = symbol;
                }
            }
            return maxlayerSymbol;
        }
        else if (result.size() == 1){
            return result.get(0);
        }
        else{
            return null;
        }
    }


    public int getLargestLayer(){
        return largestLayer;
    }
}
