package ShowResult;
import GUI.IMainForm;
import SemanticAnalysis.SemanticException;
import SemanticAnalysis.Symbol;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.TAC;
import java.util.ArrayList;


public class GenerateResult {
    private String op;
    private int layer;//?????????
    private int index;//?????????±??????????
    private SymbolTable symbolTable;
    private int maxIndex;
    private IMainForm mf;

    public GenerateResult(IMainForm mf)
    {
        symbolTable = new SymbolTable();
        layer = 0;//???????????
        this.mf = mf;
    }

    public void getResult(ArrayList<TAC> tacArrayList)throws Exception
    {
        maxIndex = tacArrayList.size() - 1;
        for(index = 0; index<tacArrayList.size(); index++)
        {
            TAC tac= tacArrayList.get(index);
            op = tac.getOp();
            switch(op)
            {
                case "jmp":
                    jmpStmt(tac);
                    break;
                case "read":
                    readStmt(tac);
                    break;
                case "write":
                    writeStmt(tac);
                    break;
                case "in":
                    inStmt(tac);
                    break;
                case "out":
                    outStmt(tac);
                    break;
                case "int":
                    intStmt(tac);
                    break;
                case "double":
                    doubleStmt(tac);
                    break;
                case "intarr":
                    intarrStmt(tac);
                    break;
                case "doublearr":
                    doublearrStmt(tac);
                    break;
                case "assign":
                    assignStmt(tac);
                    break;
                case "+":
                    addStmt(tac);
                    break;
                case "-":
                    subStmt(tac);
                    break;
                case "*":
                    mulStmt(tac);
                    break;
                case "/":
                    divideStmt(tac);
                    break;
                case ">":
                    largeStmt(tac);
                    break;
                case "<":
                    lessStmt(tac);
                    break;
                case ">=":
                    larequalStmt(tac);
                    break;
                case "<=":
                    lesequalStmt(tac);
                    break;
                case "==":
                    equalStmt(tac);
                    break;
                case "!=":
                    notequalStmt(tac);
                    break;
                case "_":
                    notStmt(tac);
                    break;
                default:


            }
        }
    }


    private void jmpStmt(TAC tac) throws Exception {
        //???
        //???????????jmp???????(jmp,????,null,???)
        String args = tac.getArg1();//???????
        if(args.equals("_"))
        {
            index = Integer.valueOf(tac.getAddress())-1;
        }
        else if (args.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$")){
            Double dou = Double.parseDouble(args);
            if (dou.equals(0)){
                index = Integer.valueOf(tac.getAddress())-1;
            }
        }
        else
        {
            String bool = symbolTable.searchTable(args).getValue();
            if(bool.equals("true"))
            {

            }else if(symbolTable.searchTable(args).getValue().matches("^(\\-|\\+)?\\d+(\\.\\d+)?$"))
            {
                Symbol symbol = symbolTable.searchTable(args);
                Double dou = Double.parseDouble(symbol.getValue());
                if(dou.equals(0))
                {
                    index = Integer.valueOf(tac.getAddress())-1;
                }
            }
            else
            {
                index = Integer.valueOf(tac.getAddress())-1;
            }
        }

    }

    private void readStmt(TAC tac)throws Exception  {
        String args = tac.getAddress();//?????????????
        String subindex = tac.getArg2();//?????±?
        if(subindex.equals("_"))//??????
        {
            //??????????????? ?????????????????????????

            Symbol symbol = symbolTable.searchTable(args);
            if(symbol == null)
            {
                String wrong = "do not exist variable "+args;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
            else
            {

//                Scanner scanner = new Scanner(System.in);
//                String value = scanner.nextLine();
                String value = mf.readFuc();
                //?ж?????????
                if(value.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$"))
                {

                    if(value.contains("."))//??????
                    {
                        if(symbol.getType().equals("double"))
                        {
                            Symbol symbol1 = new Symbol(args,"double",layer,value);
                            symbolTable.modify(symbol1);
                        }else{
                            String wrong = "应该输入一个整数";
                            mf.reportErr(wrong);
                            throw  new SemanticException("");
                        }

                    }else if(symbol.getType().equals("int") || symbol.getType().equals("double"))//????
                    {
                        if(symbol.getType().equals("int"))
                        {
                            Symbol symbol1 = new Symbol(args,"int",layer,value);
                            symbolTable.modify(symbol1);
                        }else if(symbol.getType().equals("double"))
                        {
                            Symbol symbol1 = new Symbol(args,"double",layer,value);
                            symbolTable.modify(symbol1);
                        }

                    }
                }
                else
                {
                    //???????????
                    String wrong = "输入的必须是数字";
                    mf.reportErr(wrong);
                    throw  new SemanticException("");
                }
            }
        }else{//??????????
            Symbol symbol = symbolTable.searchTable(args);
            if(symbol == null)
            {
                String wrong = "do not exist variable "+args;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
            else
            {
//                Scanner scanner = new Scanner(System.in);
//                String value = scanner.nextLine();
                String value = mf.readFuc();
                if(symbol.getType().equals("int[]"))
                {//???????int??????
                    if(value.matches("^-?[1-9]\\d*$") || value.equals("0"))
                    {
                        //?ж?subindex???????????
                        if(subindex.matches("^-?[1-9]\\d*$") || subindex.equals("0"))
                        {
                            symbol.setIntArray(Integer.parseInt(subindex),Integer.parseInt(value));
                        }else
                        {
                            Symbol symbol1 = symbolTable.searchTable(subindex);
                            if(symbol1 != null)
                            {
                                if(symbol1.getType().equals("int"))
                                {
                                    symbol.setIntArray(Integer.parseInt(subindex),Integer.parseInt(value));
                                }else
                                {
                                    //????????
                                }

                            }else
                            {
                                //????δ????
                            }
                        }


                    }else{
                        //????????
                    }

                }else if(symbol.getType().equals("double[]"))
                {//???????????????
                    if(value.matches("^(-?\\d+)(\\.\\d+)?$"))
                    {
                        //?ж?subindex???????????
                        if(subindex.matches("^-?[1-9]\\d*$") || subindex.equals("0"))
                        {
                            symbol.setDouArr(Integer.parseInt(subindex),Double.parseDouble(value));
                        }else
                        {
                            Symbol symbol1 = symbolTable.searchTable(subindex);
                            if(symbol1 != null)
                            {
                                if(symbol1.getType().equals("int"))
                                {
                                    symbol.setDouArr(Integer.parseInt(subindex),Double.parseDouble(value));
                                }else
                                {
                                    //????????
                                }

                            }else
                            {
                                //????δ????//?????±??????
                            }
                        }

                    }else{
                        //???????
                    }
                }
            }
        }

    }

    public void writeStmt(TAC tac) throws Exception {
        String args = tac.getAddress();
        //?????????ж????????????????
        Double number = convert2Float(args);
        //?????ж???????
        if(number == null)
        {

        }
        else
        {
            //??????
            if(args.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$"))
            {
                if(args.contains("."))//??????
                {
                    mf.writeFunc(String.valueOf(Double.parseDouble(args)));
//                    System.out.println(Double.parseDouble(args));
                }else//????
                {
                    mf.writeFunc(String.valueOf(Integer.parseInt(args)));
//                    System.out.println(Integer.parseInt(args));
                }
            }
            else//?????
            {
                Symbol symbol = symbolTable.searchTable(args);
                if(symbol != null)
                {
                    if(symbol.getType().equals("int"))
                    {
                        mf.writeFunc(number.toString().split("\\.")[0]);
//                        System.out.println(number.toString().split("\\.")[0]);
                    }
                    else//??double????
                    {
                        mf.writeFunc(number.toString());
//                        System.out.println(number);
                    }

                }
                else
                {
                    String wrong = "do not exist variable "+args;
                    mf.reportErr(wrong);
                    throw  new SemanticException("");
                }
            }


        }
    }

    private void inStmt(TAC tac)throws Exception  {
        layer++;
    }

    private void outStmt(TAC tac) throws Exception {
        symbolTable.deleteLayer(layer);
        layer--;

    }

    private void intStmt(TAC tac)throws Exception  {


        String value = tac.getArg1();//?

        if(value.equals("_"))//??????????????
        {
            Symbol symbol = new Symbol(tac.getAddress(),tac.getOp(),layer);
            symbolTable.insert(symbol);
        }else //??????????????
        {
            //???ж?????????
            if(value.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$"))
            {
                //?ж?????????
                if(value.matches("^-?[1-9]\\d*$") || value.equals("0"))
                {
                    Symbol symbol = new Symbol(tac.getAddress(),tac.getOp(),layer,value);
                    symbolTable.insert(symbol);
                }
                else
                {
                    String wrong = "应输入整数";
                    mf.reportErr(wrong);
                    throw  new SemanticException("");
                }
            }else if(convert2Float(value) != null)//???null???????????????????
            {
                Double number = convert2Float(value);
                if(symbolTable.searchTable(value).getType().equals("int"))
                {
                    Symbol symbol = new Symbol(tac.getAddress(),"int",layer,number.toString().split("\\.")[0]);
                    symbolTable.insert(symbol);
                }else
                {
                    //????????????????
                    String wrong = "应输入整数";
                    mf.reportErr(wrong);
                    throw  new SemanticException("");
                }
            }

        }

    }

    private void doubleStmt(TAC tac)throws Exception  {
        String value = tac.getArg1();//?

        if(value.equals("_"))//??????????????
        {
            Symbol symbol = new Symbol(tac.getAddress(),tac.getOp(),layer);
            symbolTable.insert(symbol);
        }else
        {
            //???ж?????????
            if(value.matches("^(\\-|\\+)?\\d+(\\.\\d+)?$"))
            {
                Symbol symbol = new Symbol(tac.getAddress(),tac.getOp(),layer,value);
                symbolTable.insert(symbol);

            }else if(convert2Float(value) != null)//???null???????????????????
            {
                Double number = convert2Float(value);
                if(symbolTable.searchTable(value).getType().equals("double") || symbolTable.searchTable(value).getType().equals("int")) {
                    Symbol symbol = new Symbol(tac.getAddress(), "double", layer, number.toString());
                    symbolTable.insert(symbol);
                }
            }

        }

    }

    private void intarrStmt(TAC tac)throws Exception  {
        String length = tac.getArg2();
        String name = tac.getAddress();

        if(length.matches("^[1-9]\\d*$"))
        {
            int[] array = new int[Integer.parseInt(length)];
            Symbol symbol = new Symbol(name,"int[]",layer,array);
            symbolTable.insert(symbol);

        }else if(length.matches("^(-?\\d+)(\\.\\d+)?$ ") && length.contains("."))
        {
            String wrong = "数组长度应为整数";
            mf.reportErr(wrong);
            throw  new SemanticException("");
        }else
        {
            Double number = convert2Float(length);
            if(number != null)
            {
                if(symbolTable.searchTable(name).getType().equals("int"))//??????int??
                {
                    int[] array = new int[Integer.parseInt(number.toString().split("\\.")[0])];
                    Symbol symbol = new Symbol(name,"int[]",layer,array);
                    symbolTable.insert(symbol);
                }
            }

        }

    }

    private void doublearrStmt(TAC tac)throws Exception  {
        String length = tac.getArg2();
        String name = tac.getAddress();

        if(length.matches("^[1-9]\\d*$"))
        {
            double[] array = new double[Integer.parseInt(length)];
            Symbol symbol = new Symbol(name,"double[]",layer,array);
            symbolTable.insert(symbol);

        }else if(length.matches("^(-?\\d+)(\\.\\d+)?$") && length.contains("."))
        {
            String wrong = "数组长度应为整数";
            mf.reportErr(wrong);
            throw  new SemanticException("");
        }else
        {
            Double number = convert2Float(length);
            if(number != null)
            {
                if(symbolTable.searchTable(name).getType().equals("int"))//??????int??
                {
                    double[] array = new double[Integer.parseInt(number.toString().split("\\.")[0])];
                    Symbol symbol = new Symbol(name,"int[]",layer,array);
                    symbolTable.insert(symbol);
                }
            }

        }

    }

    private void assignStmt(TAC tac)throws Exception  {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        if(args2.equals("_")) //???????????
        {
            Double number1 = convert2Float(args1);
            if(number1 == null)
            {

            }else
            {
                String name = tac.getAddress();//??????
                Symbol symbol = symbolTable.searchTable(name);

                if(symbol != null)
                {
                    if(symbol.getType().equals("int") )
                    {
                        int number = Integer.parseInt(number1.toString().split("\\.")[0]);
                        Symbol symbol1 = new Symbol(name,symbol.getType(),layer,String.valueOf(number));
                        symbolTable.modify(symbol1);
                    }
                    else
                    {
                        Symbol symbol1 = new Symbol(name,symbol.getType(),layer,number1.toString());
                        symbolTable.modify(symbol1);
                    }

                }
                else
                {

                    String wrong = "do not exist variable "+name;
                    mf.reportErr(wrong);
                    throw new SemanticException("");
                }

            }
        }else //?????鸳?
        {
            Double number1 = convert2Float(args1);
            Double subindex = convert2Float(args2);//?????±?
            if(number1 == null || subindex == null)
            {
                //????
            }else
            {
                String name = tac.getAddress();//??????
                Symbol symbol = symbolTable.searchTable(name);
                //?ж????????
                if(symbol != null)
                {
                    if(symbol.getType().equals("int[]"))//?????int??????
                    {
                        if((args1.matches("^-?[1-9]\\d*$")) || args1.equals("0"))//?????????
                        {
                            //??args1????????±?
                            if(args2.matches("^\\d+$")){//???????
                                if((Integer.parseInt(args2) < symbolTable.searchTable(name).getIntarr().length) && Integer.parseInt(args2) >=0)
                                {
                                    //???
                                    symbolTable.searchTable(name).setIntArray(Integer.parseInt(args2),Integer.parseInt(args1));
                                }
                                else{
                                    String wrong = "array out of index";
                                    mf.reportErr(wrong);
                                    throw new SemanticException("");
                                }
                            }
                            else{
                                Symbol symbol1 = symbolTable.searchTable(args2);
                                if(symbol1 != null)
                                {
                                    if(Integer.parseInt(symbol1.getValue()) < symbolTable.searchTable(name).getIntarr().length  && Integer.parseInt(symbol1.getValue()) >= 0)
                                    {
                                        //???
                                        symbolTable.searchTable(name).setIntArray(Integer.parseInt(symbol1.getValue()),Integer.parseInt(args1));
                                    }
                                    else{
                                        String wrong = "array out of index";
                                        mf.reportErr(wrong);
                                        throw new SemanticException("");
                                    }

                                }
                                else {
                                    String wrong = "do not exist variable "+args2;
                                    mf.reportErr(wrong);
                                    throw new SemanticException("");
                                }

                            }
                        }else{
                            //???????????
                            Symbol symbol1 = symbolTable.searchTable(args1);
                            if(symbol1 != null)
                            {
                                if(symbol1.getType().equals("int")) //?????????????????????int
                                {
                                    //??number1.tosTRING.SPLIT(".")[0]????????±?
                                    if(args2.matches("^\\d+$")){//???????
                                        if(Integer.parseInt(args2) < symbolTable.searchTable(name).getIntarr().length && Integer.parseInt(args2) >= 0)
                                        {
                                            //???
                                            symbolTable.searchTable(name).setIntArray(Integer.parseInt(args2),Integer.parseInt(number1.toString().split("\\.")[0]));
                                        }
                                        else{
                                            //越界
                                            String wrong = "array out of index";
                                            mf.reportErr(wrong);
                                            throw new SemanticException("");
                                        }
                                    }
                                    else{
                                        Symbol symbol2 = symbolTable.searchTable(args2);
                                        if(symbol2 != null)
                                        {
                                            if(Integer.parseInt(symbol2.getValue()) < symbolTable.searchTable(name).getIntarr().length && Integer.parseInt(symbol2.getValue()) >= 0)
                                            {
                                                //???
                                                symbolTable.searchTable(name).setIntArray(Integer.parseInt(symbol2.getValue()),Integer.parseInt(symbol1.getValue()));
                                            }
                                            else{
                                                String wrong = "array out of index";
                                                mf.reportErr(wrong);
                                                throw new SemanticException("");
                                            }

                                        }
                                        else {
                                            //????
                                            String wrong = "do not exist variable "+args2;
                                            mf.reportErr(wrong);
                                            throw new SemanticException("");
                                        }
                                        //????
                                    }
                                }else
                                {
                                    //????????????
                                }

                            }else
                            {
                                String wrong = "do not exist variable "+args1;
                                mf.reportErr(wrong);
                                throw new SemanticException("");
                            }
                        }




                    }
                    else if(symbol.getType().equals("double[]"))//?????double??????
                    {
                        if((args1.matches("^(-?\\d+)(\\.\\d+)?$")))//??????????
                        {
                            //??args1????????±?
                            if(args2.matches("^\\d+$")){//???????
                                if(Integer.parseInt(args2) < symbolTable.searchTable(name).getDouarr().length && Integer.parseInt(args2) >= 0)
                                {
                                    //???
                                    symbolTable.searchTable(name).setDouArr(Integer.parseInt(args2),Double.parseDouble(args1));
                                }
                                else{
                                    //???
                                    String wrong = "array out of index";
                                    mf.reportErr(wrong);
                                    throw new SemanticException("");
                                }
                            }
                            else{
                                Symbol symbol1 = symbolTable.searchTable(args2);
                                if(symbol1 != null)
                                {
                                    if(Integer.parseInt(symbol1.getValue()) < symbolTable.searchTable(name).getDouarr().length && Integer.parseInt(symbol1.getValue()) >= 0)
                                    {
                                        //???
                                        symbolTable.searchTable(name).setDouArr(Integer.parseInt(symbol1.getValue()),Double.parseDouble(args1));
                                    }
                                    else{
                                        //???
                                        String wrong = "array out of index";
                                        mf.reportErr(wrong);
                                        throw new SemanticException("");
                                    }

                                }
                                else {
                                    //????????
                                    String wrong = "do not exist variable "+args2;
                                    mf.reportErr(wrong);
                                    throw new SemanticException("");
                                }
                            }
                        }else{
                            //??????????
                        }

                        Symbol symbol1 = symbolTable.searchTable(args1);
                        if(symbol1 != null)
                        {
                            if(symbol1.getType().equals("double")) //?????????????????????int
                            {
                                //??number1.tosTRING.SPLIT(".")[0]????????±?
                                if(args2.matches("^\\d+$")){//???????
                                    if(Integer.parseInt(args2) < symbolTable.searchTable(name).getDouarr().length && Integer.parseInt(args2) >= 0)
                                    {
                                        //???
                                        symbolTable.searchTable(name).setDouArr(Integer.parseInt(args2),Double.parseDouble(number1.toString()));
                                    }
                                    else{
                                        //???
                                        String wrong = "array out of index";
                                        mf.reportErr(wrong);
                                        throw new SemanticException("");
                                    }
                                }
                                else{
                                    Symbol symbol2 = symbolTable.searchTable(args2);
                                    if(symbol2 != null)
                                    {
                                        if(Integer.parseInt(symbol2.getValue()) < symbolTable.searchTable(name).getDouarr().length && Integer.parseInt(symbol2.getValue()) >= 0)
                                        {
                                            //???
                                            symbolTable.searchTable(name).setDouArr(Integer.parseInt(symbol2.getValue()),Double.parseDouble(symbol1.getValue()));
                                        }
                                        else{
                                            //???
                                            String wrong = "array out of index";
                                            mf.reportErr(wrong);
                                            throw new SemanticException("");
                                        }

                                    }
                                    else {
                                        //????
                                        String wrong = "do not exist variable" + args2;
                                        mf.reportErr(wrong);
                                        throw new SemanticException("");
                                    }

                                }
                            }else
                            {
                                //??????????
                            }
                        }

                    }

                }
                else
                {
                    String wrong = "do not exist variable "+name;
                    mf.reportErr(wrong);
                    throw new SemanticException("");
                }

            }
        }
    }

    private void addStmt(TAC tac) throws Exception  {
        //?????????м???
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);



        //???ж?????????
        if(((args1.matches("^-?[1-9]\\d*$")) || args1.equals("0")) && ((args2.matches("^-?[1-9]\\d*$")) || args2.equals("0"))) {
            Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(number1 + number2).split("\\.")[0]);
            symbolTable.insert(symbol1);
        }else if((args1.matches("^(-?\\d+)(\\.\\d+)?$") && args1.contains(".")) || (args2.matches("^(-?\\d+)(\\.\\d+)?$") && args2.contains("."))){
            Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(number1 + number2));
            symbolTable.insert(symbol1);
        }else if(args1.matches("^-?[1-9]\\d*$") || args1.equals("0"))//??????????????????????
        {
            Symbol symbol = symbolTable.searchTable(args2);
            if (symbol != null) {
                if (symbol.getType().equals("int")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "int", layer, String.valueOf(number1 + number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                } else if (symbol.getType().equals("double")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "double", layer, String.valueOf(number1 + number2));
                    symbolTable.insert(symbol1);
                }
            } else {
                String wrong = "do not exist variable "+args2;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }else if(args2.matches("^-?[1-9]\\d*$") || args2.equals("0"))//??????????????????????
        {
            Symbol symbol = symbolTable.searchTable(args1);
            if (symbol != null) {
                if (symbol.getType().equals("int")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "int", layer, String.valueOf(number1 + number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                } else if (symbol.getType().equals("double")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "double", layer, String.valueOf(number1 + number2));
                    symbolTable.insert(symbol1);
                }
            } else {
                String wrong = "do not exist variable "+args1;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }else{//?ж?????????
            Symbol symbolarg1 = symbolTable.searchTable(args1);
            Symbol symbolarg2 = symbolTable.searchTable(args2);
            if((symbolarg1 != null) && (symbolarg2 != null)){
                if(symbolarg1.getType().equals("int") && symbolarg2.getType().equals("int"))
                {
                    Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(number1 + number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                }else
                {
                    Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(number1 + number2));
                    symbolTable.insert(symbol1);
                }
            }else{
                String wrong = "变量"+args1+"和变量"+args2+"有一个不存在";
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }

    }

    private void subStmt(TAC tac) throws Exception  {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        //???ж?????????
        if(((args1.matches("^-?[1-9]\\d*$")) || args1.equals("0")) && ((args2.matches("^-?[1-9]\\d*$")) || args2.equals("0"))) {
            Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(number1 - number2).split("\\.")[0]);
            symbolTable.insert(symbol1);
        }else if((args1.matches("^(-?\\d+)(\\.\\d+)?$") && args1.contains(".")) || (args2.matches("^(-?\\d+)(\\.\\d+)?$") && args2.contains("."))){
            Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(number1 - number2));
            symbolTable.insert(symbol1);
        }else if(args1.matches("^-?[1-9]\\d*$") || args1.equals("0"))//??????????????????????
        {
            Symbol symbol = symbolTable.searchTable(args2);
            if (symbol != null) {
                if (symbol.getType().equals("int")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "int", layer, String.valueOf(number1 - number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                } else if (symbol.getType().equals("double")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "double", layer, String.valueOf(number1 - number2));
                    symbolTable.insert(symbol1);
                }
            } else {
                String wrong = "do not exist variable "+args2;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }else if(args2.matches("^-?[1-9]\\d*$") || args2.equals("0"))//??????????????????????
        {
            Symbol symbol = symbolTable.searchTable(args1);
            if (symbol != null) {
                if (symbol.getType().equals("int")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "int", layer, String.valueOf(number1 - number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                } else if (symbol.getType().equals("double")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "double", layer, String.valueOf(number1 - number2));
                    symbolTable.insert(symbol1);
                }
            } else {
                String wrong = "do not exist variable "+args1;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }else{//?ж?????????
            Symbol symbolarg1 = symbolTable.searchTable(args1);
            Symbol symbolarg2 = symbolTable.searchTable(args2);
            if((symbolarg1 != null) && (symbolarg2 != null)){
                if(symbolarg1.getType().equals("int") && symbolarg2.getType().equals("int"))
                {
                    Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(number1 - number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                }else
                {
                    Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(number1 - number2));
                    symbolTable.insert(symbol1);
                }
            }else{
                String wrong = "变量"+args1+"和变量"+args2+"有一个不存在";
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }
    }

    private void mulStmt(TAC tac) throws Exception {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        //???ж?????????
        if(((args1.matches("^-?[1-9]\\d*$")) || args1.equals("0")) && ((args2.matches("^-?[1-9]\\d*$")) || args2.equals("0"))) {
            Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(number1 * number2).split("\\.")[0]);
            symbolTable.insert(symbol1);
        }else if((args1.matches("^(-?\\d+)(\\.\\d+)?$") && args1.contains(".")) || (args2.matches("^(-?\\d+)(\\.\\d+)?$") && args2.contains("."))){
            Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(number1 * number2));
            symbolTable.insert(symbol1);
        }else if(args1.matches("^-?[1-9]\\d*$") || args1.equals("0"))//??????????????????????
        {
            Symbol symbol = symbolTable.searchTable(args2);
            if (symbol != null) {
                if (symbol.getType().equals("int")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "int", layer, String.valueOf(number1 * number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                } else if (symbol.getType().equals("double")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "double", layer, String.valueOf(number1 * number2));
                    symbolTable.insert(symbol1);
                }
            } else {
                String wrong = "do not exist variable "+args2;
                mf.reportErr(wrong);
                throw  new SemanticException("");

            }
        }else if(args2.matches("^-?[1-9]\\d*$") || args2.equals("0"))//??????????????????????
        {
            Symbol symbol = symbolTable.searchTable(args1);
            if (symbol != null) {
                if (symbol.getType().equals("int")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "int", layer, String.valueOf(number1 * number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                } else if (symbol.getType().equals("double")) {
                    Symbol symbol1 = new Symbol(tac.getAddress(), "double", layer, String.valueOf(number1 * number2));
                    symbolTable.insert(symbol1);
                }
            } else {
                String wrong = "do not exist variable "+args1;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }else{//?ж?????????????????
            Symbol symbolarg1 = symbolTable.searchTable(args1);
            Symbol symbolarg2 = symbolTable.searchTable(args2);
            if((symbolarg1 != null) && (symbolarg2 != null)){
                if(symbolarg1.getType().equals("int") && symbolarg2.getType().equals("int"))
                {
                    Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(number1 * number2).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                }else
                {
                    Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(number1 * number2));
                    symbolTable.insert(symbol1);
                }
            }else{
                String wrong = "变量"+args1+"和变量"+args2+"有一个不存在";
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }
    }

    private void divideStmt(TAC tac) throws Exception  {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        //???number1?????????洢??????????number???????????洢???????
        //???ж?????????
        if((args1.matches("^-?[1-9]\\d*$")) || args1.equals("0")) {//????????????????
            //?ж???????????????????
            if(args2.matches("^(-?\\d+)(\\.\\d+)?$"))//??????????????????
            {
                double res = Integer.parseInt(args1) / number2;
                Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(res).split("\\.")[0]);
                symbolTable.insert(symbol1);
            }else{ //????
                Symbol symbol = symbolTable.searchTable(args2);
                if(symbol != null)
                {
                    double res = Integer.parseInt(args1) / Double.parseDouble(symbol.getValue());
                    Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(res).split("\\.")[0]);
                    symbolTable.insert(symbol1);
                }
                else{
                    String wrong = "do not exist variable "+args2;
                    mf.reportErr(wrong);
                    throw  new SemanticException("");
                }
            }
        }else if(args1.matches("^(-?\\d+)(\\.\\d+)?$") && args1.contains(".")){//?ж??????????????
            //?ж???????????????????
            if(args2.matches("^(-?\\d+)(\\.\\d+)?$"))//??????????????????
            {
                double res = Double.parseDouble(args1) / number2;
                Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(res));
                symbolTable.insert(symbol1);
            }else{ //????
                Symbol symbol = symbolTable.searchTable(args2);
                if(symbol != null)
                {
                    double res = Double.parseDouble(args1) / Double.parseDouble(symbol.getValue());
                    Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(res));
                    symbolTable.insert(symbol1);
                }else{
                    String wrong = "do not exist variable "+args2;
                    mf.reportErr(wrong);
                    throw  new SemanticException("");
                }
            }
        }else//??????????
        {
            //???ж????????????????
            Symbol symbol = symbolTable.searchTable(args1);
            if (symbol != null) {
                if (symbol.getType().equals("int")) { //?????int??
                    if(args2.matches("^(-?\\d+)(\\.\\d+)?$"))//??????????????????
                    {
                        double res = Integer.parseInt(symbol.getValue()) / number2;
                        Symbol symbol1 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(res).split("\\.")[0]);
                        symbolTable.insert(symbol1);
                    }else{ //????
                        Symbol symbol1 = symbolTable.searchTable(args2);
                        if(symbol1 != null)
                        {
                            double res = Integer.parseInt(symbol.getValue()) / number2;
                            Symbol symbol2 = new Symbol(tac.getAddress(),"int",layer,String.valueOf(res).split("\\.")[0]);
                            symbolTable.insert(symbol2);
                        }else{
                            String wrong = "do not exist variable "+args2;
                            mf.reportErr(wrong);
                            throw new SemanticException("");
                        }
                    }

                } else if (symbol.getType().equals("double")) {
                    if(args2.matches("^(-?\\d+)(\\.\\d+)?$"))//??????????????????
                    {
                        double res = Double.parseDouble(symbol.getValue()) / number2;
                        Symbol symbol1 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(res));
                        symbolTable.insert(symbol1);
                    }else{ //????
                        Symbol symbol1 = symbolTable.searchTable(args2);
                        if(symbol1 != null)
                        {
                            double res = Double.parseDouble(symbol.getValue()) / Double.parseDouble(symbol1.getValue());
                            Symbol symbol2 = new Symbol(tac.getAddress(),"double",layer,String.valueOf(res));
                            symbolTable.insert(symbol2);
                        }else{
                            String wrong = "do not exist variable "+args2;
                            mf.reportErr(wrong);
                            throw  new SemanticException("");
                        }
                    }
                }
            } else {
                String wrong = "do not exist variable "+args1;
                mf.reportErr(wrong);
                throw  new SemanticException("");
            }
        }
    }

    private void largeStmt(TAC tac) throws Exception {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        if(number1 > number2) {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "true");
            symbolTable.insert(symbol);
        }else
        {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "false");
            symbolTable.insert(symbol);
        }

    }

    private void lessStmt(TAC tac) throws Exception  {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        if(number1 < number2) {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "true");
            symbolTable.insert(symbol);
        }else
        {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "false");
            symbolTable.insert(symbol);
        }
    }

    private void larequalStmt(TAC tac) throws Exception {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        if(number1 >= number2) {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "true");
            symbolTable.insert(symbol);
        }else
        {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "false");
            symbolTable.insert(symbol);
        }
    }

    private void equalStmt(TAC tac) throws Exception  {

        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);


        if(number1.equals(number2)) {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "true");
            symbolTable.insert(symbol);
        }else
        {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "false");
            symbolTable.insert(symbol);
        }
    }

    private void lesequalStmt(TAC tac) throws Exception  {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        if(number1 <= number2) {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "true");
            symbolTable.insert(symbol);
        }else
        {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "false");
            symbolTable.insert(symbol);
        }
    }

    private void notequalStmt(TAC tac) throws Exception {
        String args1 = tac.getArg1();
        String args2 = tac.getArg2();

        Double number1 = convert2Float(args1);
        Double number2 = convert2Float(args2);

        if(number1.equals(number2)) {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "false");
            symbolTable.insert(symbol);
        }else
        {
            Symbol symbol = new Symbol(tac.getAddress(), "bool", layer, "true");
            symbolTable.insert(symbol);
        }
    }

    private void notStmt(TAC tac) throws Exception {
        String name = tac.getArg1();
        String subindex = tac.getArg2();
        String newVariable = tac.getAddress();

        Symbol symbol = symbolTable.searchTable(name);//数组symbol
        if(symbol != null)
        {

            if(symbol.getType().equals("int[]")) //int??????
            {
                if(subindex.matches("^[1-9]\\d*|0$"))//非负整数
                {
                    if(Integer.parseInt(subindex) < symbol.getIntarr().length && Integer.parseInt(subindex) >= 0)
                    {
                        String value = String.valueOf(symbol.getIntarr()[Integer.parseInt(subindex)]);
                        Symbol symbol1 = new Symbol(newVariable,"int",layer,value);
                        symbolTable.insert(symbol1);
                    }else{
                        //越界
                        String wrong = "array out of index";
                        mf.reportErr(wrong);
                        throw new SemanticException("");
                    }

                }else
                {
                    Symbol symbol1 = symbolTable.searchTable(subindex);
                    if(symbol1 != null)
                    {
                        if(symbol1.getType().equals("int")){
                            if(Integer.parseInt(symbol1.getValue()) < symbol.getIntarr().length && Integer.parseInt(symbol1.getValue()) >=0)
                            {
                                String value = String.valueOf(symbol.getIntarr()[Integer.parseInt(symbol1.getValue())]);
                                Symbol symbol2 = new Symbol(newVariable,"int",layer,value);
                                symbolTable.insert(symbol2);
                            }else{
                                String wrong = "array out of index";
                                mf.reportErr(wrong);
                                //越界
                                throw new SemanticException("");
                            }

                        }else{
                            //类型不对
                            throw new SemanticException("");
                        }

                    }
                    else{

                    }
                }


            }else if(symbol.getType().equals("double[]")){//double??????
                if(subindex.matches("^[1-9]\\d*|0$"))
                {
                    if(Integer.parseInt(subindex) < symbol.getDouarr().length)
                    {
                        String value = String.valueOf(symbol.getDouarr()[Integer.parseInt(subindex)]);
                        Symbol symbol1 = new Symbol(newVariable,"double",layer,value);
                        symbolTable.insert(symbol1);
                    }

                }else
                {
                    Symbol symbol1 = symbolTable.searchTable(subindex);
                    if(symbol1 != null)
                    {
                        if(symbol1.getType().equals("int")){
                            if(Integer.parseInt(symbol1.getValue()) < symbol.getDouarr().length && Integer.parseInt(symbol1.getValue()) >=0)
                            {
                                String value = String.valueOf(symbol.getDouarr()[Integer.parseInt(symbol1.getValue())]);
                                Symbol symbol2 = new Symbol(newVariable,"double",layer,value);
                                symbolTable.insert(symbol2);
                            }else{
                                //越界
                                throw new SemanticException("");
                            }

                        }else{
                            //
                        }

                    }
                    else{

                    }
                }

            }
        }else{


        }
    }



    public Double convert2Float(String args) throws Exception
    {
        Double number1 = obtainValue(args);
        return number1;
    }


    public Double obtainValue(String args) throws Exception
    {
        Double number = null;
        if(isDigital(args))//是数字
        {
            number = Double.parseDouble(args);
        }else if(isVariable(args))//是变量
        {
            //在符号表中查看是否存在此变量
            Symbol symbol = symbolTable.searchTable(args);
            if(symbol == null)
            {
                //变量不存在，报错

                String wrong = "do not exist variable "+args;
                mf.reportErr(wrong);
                throw new SemanticException("");
            }else
            {
                if(symbol.getValue() == null)
                {
                    //变量未赋值
                    String wrong = "变量"+args+"还未赋值";
                    mf.reportErr(wrong);
                    throw new SemanticException("");
                }else
                {
                    number = Double.parseDouble(symbol.getValue());
                }
            }
        }
        return number;
    }
    public Boolean isDigital(String args)
    {
        char a = args.charAt(0);
        if(a >= '0' && a <= '9')
        {
            return true;
        }
        return false;
    }

    public Boolean isVariable(String args)
    {
        char a  = args.charAt(0);
        if((a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z'))
        {
            return true;
        }
        return false;
    }
}
