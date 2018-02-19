package SemanticAnalysis;
import GUI.IMainForm;
import GrammaticalParser.TreeNode;
import GrammaticalParser.StmtType;
import LexParser.Token;
import LexParser.TokenType;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SemanticAnalysis {
    //语法分析得到的根节点
    private TreeNode root;
    //符号表
    private SymbolTable table;
    private Integer level = 0;
    //用于申明临时变量
    private Integer tempindex = 0;
    //产生的中间语句
    private ArrayList<TAC> interStatement = new ArrayList<TAC>();
    private IMainForm mf;
    private int breakIndex;


    public SemanticAnalysis(TreeNode root, IMainForm mf){
        this.root = root;
        this.mf = mf;
    }

    public static final String JMP = "jmp";
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String INTARR = "intarr";
    public static final String DOUBLEARR = "doublearr";
    public static final String ASSIGN = "assign";
    public static final String NONE = "_";

    public ArrayList<TAC> analysis(){
        ArrayList<TreeNode> nextLayer = root.getNextLayer();
        for (TreeNode temp : nextLayer){
            statementAnalysis(temp);
        }
        return interStatement;
    }

    public void statementAnalysis(TreeNode node){
        ArrayList<TreeNode> nextStmt = node.getNextLayer();
        if (nextStmt.size() == 0){
                //跳过注释等空白的statement
        }
        else{
            TreeNode nextStmtNode = nextStmt.get(0);
            if (nextStmtNode.getType() == StmtType.IFSTMT){
                ifStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.WHILESTMT){
                whileStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.ASSIGNSTMT){
                assignStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.READSTMT){
                readStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.WRITESTMT){
                    writeStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.DECLARE){
                declareStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.FORSTMT){
                forStmt(nextStmtNode);
            }
            else if (nextStmtNode.getType() == StmtType.BREAKSTMT){
                forBreak(nextStmtNode);
            }
            else{
            }
        }

    }

    private void forBreak(TreeNode node){
        ArrayList<TreeNode> nextlayer = node.getNextLayer();
        breakIndex = interStatement.size();
        TAC outTac = new TAC(JMP, NONE, NONE, String.valueOf(breakIndex));
        interStatement.add(outTac);
    }

    private void ifStmt(TreeNode node){
        //if(condition){statements}
        ArrayList<TreeNode> nextlayer = node.getNextLayer();

        TAC inTac = new TAC(IN, NONE, NONE, NONE);
        interStatement.add(inTac);

        TreeNode conditionNode = nextlayer.get(2);
        String conditionResult = forExpression(conditionNode);
        //这里不知道具体跳转到哪里，先记录下这一条中间语句的位置，知道位置后进行回填
        TAC conditionTac = new TAC(JMP, conditionResult, NONE, NONE);
        interStatement.add(conditionTac);
        Integer currentIndex = interStatement.size()-1;

        TreeNode statementNode = nextlayer.get(4);
        stmtBlock(statementNode);
        TAC ifTac = new TAC(JMP, NONE, NONE, NONE);
        Integer ifIndex = interStatement.size();
        interStatement.add(ifTac);
        //进行回填
        Integer elseOrBreakIndex = interStatement.size();
        interStatement.get(currentIndex).backfill(elseOrBreakIndex.toString());

        if (nextlayer.size() > 5){
            //存在else
            TreeNode elseNode = nextlayer.get(6);
            stmtBlock(elseNode);
        }

        //回填
        TAC outTac = new TAC(OUT, NONE, NONE, NONE);
        Integer outIndex = interStatement.size();
        interStatement.add(outTac);

        interStatement.get(ifIndex).backfill(outIndex.toString());
    }

    private void whileStmt(TreeNode node){
        //while(condition) { statements}
        //进入
        Integer currentIndex = interStatement.size();
        ArrayList<TreeNode> nextlayer = node.getNextLayer();

        //条件判断
        TreeNode conditionNode = nextlayer.get(2);
        String conditionResult = forExpression(conditionNode);

        //这里不知道具体跳转到哪里，先记录下这一条中间语句的位置，知道位置后进行回填
        TAC conditionTac = new TAC(JMP, conditionResult, NONE, NONE);
        interStatement.add(conditionTac);
        Integer conditionIndex = interStatement.size() - 1;

        TAC inTac = new TAC(IN, NONE, NONE, NONE);
        interStatement.add(inTac);

        TreeNode statementNode = nextlayer.get(4);
        stmtBlock(statementNode);

        TAC outTac = new TAC(OUT, NONE, NONE, NONE);
        interStatement.add(outTac);

        //回到条件语句进行判断
        TAC backTac = new TAC(JMP, NONE, NONE, currentIndex.toString());
        interStatement.add(backTac);
        Integer outIndex = interStatement.size();
        if (breakIndex != 0){
            interStatement.get(breakIndex).backfill(outIndex.toString());
        }
        interStatement.get(conditionIndex).backfill(outIndex.toString());
    }

    private void forStmt(TreeNode node){
        //for(stmt; expr; assign) stmtblock

        ArrayList<TreeNode> nextlayer = node.getNextLayer();

        //初值
        TreeNode decNode = nextlayer.get(2);
        statementAnalysis(decNode);
        //判断
        Integer currentIndex = interStatement.size();
        TreeNode judgeNode = nextlayer.get(3);
        String judgeStr = forExpression(judgeNode);
        TAC judgeTac = new TAC(JMP, judgeStr, NONE, NONE);
        Integer conditionIndex = interStatement.size();
        interStatement.add(judgeTac);

        TAC inTac = new TAC(IN, NONE, NONE, NONE);
        interStatement.add(inTac);

        TreeNode statementNode = nextlayer.get(7);
        stmtBlock(statementNode);

        TreeNode modifyNode = nextlayer.get(5);
        assignStmt(modifyNode);

        TAC outTac = new TAC(OUT, NONE, NONE, NONE);
        interStatement.add(outTac);

        TAC backTac = new TAC(JMP, NONE, NONE, currentIndex.toString());
        interStatement.add(backTac);
        Integer outIndex = interStatement.size();
        if (breakIndex != 0){
            interStatement.get(breakIndex).backfill(outIndex.toString());
        }
        interStatement.get(conditionIndex).backfill(outIndex.toString());

    }

    private void writeStmt(TreeNode node){
        //write ( expression ) ;
        ArrayList<TreeNode> nextlayer = node.getNextLayer();
        //获得 experssion
        TreeNode exprNode = nextlayer.get(2);
        //获得 下一层，此层判断有无逻辑运算
        String symbolValue = forExpression(exprNode);
        //转换成中间代码
        TAC writeTac = new TAC(WRITE, NONE, NONE, symbolValue);
        interStatement.add(writeTac);
    }

    private void readStmt(TreeNode node){
        //read ( varia
        ArrayList<TreeNode> nextlayer = node.getNextLayer();
        //获得 variable;
        ArrayList<TreeNode> varList = nextlayer.get(2).getNextLayer();
        String symbolValue = null;
        if (varList.size() == 1){
            symbolValue = varList.get(0).toString();
            TAC writeTac = new TAC(READ, NONE, NONE, symbolValue);
            interStatement.add(writeTac);
        }
        else{
            //数组
            String arrName = varList.get(0).toString();
            TreeNode exprNode = varList.get(2);
            String exprSymbol = forExpression(exprNode);
            String tempVar = "TempVariable"+tempindex.toString();
            tempindex++;
            symbolValue =  tempVar;
            TAC writeTac = new TAC(READ, NONE, exprSymbol, arrName);
            interStatement.add(writeTac);
        }
        //转换成中间代码
    }
    private void declareStmt(TreeNode node){
        //declare-stmt -> TYPE VARDEC {, VARDEC};
        //type -> (int | double) [ [factor] ]
        ArrayList<TreeNode> nextlayer = node.getNextLayer();

        //读取type
        TreeNode typeNode = nextlayer.get(0);
        ArrayList<TreeNode> typeLayer = typeNode.getNextLayer();
        String typeStr = null;
        String arrSize = NONE;
        if (typeLayer.size() == 1){
            //int或者double
            typeStr = typeLayer.get(0).toString();
        }
        else{
            //数组
            //(INTARR, value, size, a)
            if (typeLayer.get(0).toString().equals("int")){
                typeStr = INTARR;
            }
            else{
                typeStr = DOUBLEARR;
            }
            TreeNode factorNode = typeLayer.get(2);
            arrSize = forFactor(factorNode);
        }

        //读取vardec
        //IDENTIFIER [= EXPRESSION]
        int lastIndex = nextlayer.size();
        for (int i = 1; i < lastIndex;i++){
            //循环读取vardec
            ArrayList<TreeNode> vardecLayer = nextlayer.get(i).getNextLayer();
            if(vardecLayer.size() == 1){
                String identifier = vardecLayer.get(0).toString();
                TAC decTac = new TAC(typeStr, NONE, arrSize, identifier);
                interStatement.add(decTac);
            }
            else{
                String identifier = vardecLayer.get(0).toString();
                String exprSymbol = forExpression(vardecLayer.get(2));
                TAC desTac = new TAC(typeStr, exprSymbol, arrSize, identifier);
                interStatement.add(desTac);
            }
            i++;//跳过逗号
        }
    }

    private void assignStmt(TreeNode node){
        //Variable = Expr
        ArrayList<TreeNode> nextlayer = node.getNextLayer();

        //获取expression
        TreeNode exprNode = nextlayer.get(2);
        String exprSymbol = forExpression(exprNode);

        //获取variable
        TreeNode varNode = nextlayer.get(0);
        ArrayList<TreeNode> varList = varNode.getNextLayer();
        if (varList.size() == 1){
            String varName = varList.get(0).toString();
            TAC assignTac = new TAC(ASSIGN, exprSymbol, NONE, varName);
            interStatement.add(assignTac);
        }
        else{
            //variable -> identifier [ [ exp ] ]
            String arrName = varList.get(0).toString();
            TreeNode varExprNode = varList.get(2);
            String varExprSymbol = forExpression(varExprNode);
            //(assign, value, index, arrname)
            TAC varTac = new TAC(ASSIGN, exprSymbol, varExprSymbol, arrName);
            interStatement.add(varTac);
        }

    }

    private void stmtBlock(TreeNode node){
        ArrayList<TreeNode> nextlayer = node.getNextLayer();
        //去掉 { 和 }
        nextlayer.remove(0);
        nextlayer.remove(nextlayer.size()-1);

        for (TreeNode tempNode : nextlayer){
            statementAnalysis(tempNode);
        }
    }

    //解析expression，返回临时变量值,传入expression的node
    private String forExpression(TreeNode node){
        //expression：addtive logic addtive | addtive
        //传入expression节点，获取下一层,判断该层有没有逻辑运算
        ArrayList<TreeNode> exprLayer = node.getNextLayer();
        if (exprLayer.size() == 1){
            //没有逻辑运算符
            TreeNode nextNode = exprLayer.get(0);
            String addtiveNode = forAddtive(nextNode);
            return addtiveNode;
        }
        else{
            //逻辑运算符，运算得到bool值
            TreeNode addtiveOne = exprLayer.get(0);
            String addtiveSymbol = forAddtive(addtiveOne);
            //获取逻辑运算符
            TreeNode logicalNode = exprLayer.get(1);
            String logicalValue = logicalNode.getNextLayer().get(0).toString();
            TreeNode addtiveTwo = exprLayer.get(2);
            String addtiveSymbol2 = forAddtive(addtiveTwo);
            String tempNameToReturn = "tempVariable"+tempindex.toString();
            tempindex++;
            TAC exprTac = new TAC(logicalValue, addtiveSymbol, addtiveSymbol2, tempNameToReturn);
            interStatement.add(exprTac);
            return tempNameToReturn;
        }
    }

    private String forAddtive(TreeNode node){
        //addtive: term | term addop addtive
        //传入addtive节点，获取下一层，判断该层有没有加减运算
        ArrayList<TreeNode> exprLayer = node.getNextLayer();
        if (exprLayer.size() == 1){
            //没有加减运算
            TreeNode nextNode = exprLayer.get(0);
            String termNode = forTerm(nextNode);
            return termNode;
        }
        else{
            //有加减运算
            TreeNode termNode = exprLayer.get(0);
            String termSymbol = forTerm(termNode);
            //获取加减运算
            String finalTerm = null;
            while (exprLayer.size() == 3){
                TreeNode addNode = exprLayer.get(1);
                String addValue = addNode.getNextLayer().get(0).toString();
                TreeNode term2Node = exprLayer.get(2).getNextLayer().get(0);
                String factor2 = forTerm(term2Node);
                String tempNameToReturn = "TempVariable"+tempindex.toString();
                tempindex++;
                TAC addtiveTac = new TAC(addValue, termSymbol, factor2, tempNameToReturn);
                interStatement.add(addtiveTac);

                TreeNode addtiveNode = exprLayer.get(2);
                termSymbol = tempNameToReturn;
                exprLayer = addtiveNode.getNextLayer();
                finalTerm = tempNameToReturn;
            }
            return finalTerm;

        }
    }

    private String forTerm(TreeNode node){
        //term: factor mulop term | factor
        //传入term节点，获取下一层，判断该层有没有乘除运算
        ArrayList<TreeNode> exprLayer = node.getNextLayer();
        if (exprLayer.size() == 1){
            //没有乘除运算
            TreeNode nextNode = exprLayer.get(0);
            String termNode = forFactor(nextNode);
            return termNode;
        }
        else{
            //有乘除运算
            TreeNode factorNode = exprLayer.get(0);
            String factorSymbol = forFactor(factorNode);
            //获取乘除运算

            //修改加入中间代码的位置，从左到右计算
            String finalTerm = null;
            while (exprLayer.size() == 3){
                TreeNode mulNode = exprLayer.get(1);
                String mulValue = mulNode.getNextLayer().get(0).toString();
                TreeNode factor2Node = exprLayer.get(2).getNextLayer().get(0);
                String factor2 = forFactor(factor2Node);
                String tempNameToReturn = "TempVariable"+tempindex.toString();
                tempindex++;
                TAC addtiveTac = new TAC(mulValue, factorSymbol, factor2, tempNameToReturn);
                interStatement.add(addtiveTac);

                TreeNode termNode = exprLayer.get(2);
                factorSymbol = tempNameToReturn;
                exprLayer = termNode.getNextLayer();
                finalTerm = tempNameToReturn;
            }
            return finalTerm;
        }
    }

    private String forFactor(TreeNode node){
        //factor: (expression) | number | variable | addop expression
        //传入factor节点，判断是哪种类型的value
        ArrayList<TreeNode> factorLayer = node.getNextLayer();
        if (factorLayer.size() == 2){
            //负数
            TreeNode exprNode = factorLayer.get(1);
            String exprSymbol = forExpression(exprNode);
            TreeNode addNode = factorLayer.get(0);
            String addValue = addNode.getNextLayer().get(0).toString();
            String tempNameToReturn = "TempVariable"+tempindex.toString();
            tempindex++;
            TAC factorTac = new TAC(addValue, "0", exprSymbol, tempNameToReturn);
            interStatement.add(factorTac);
            return tempNameToReturn;
        }
        else if (factorLayer.size() == 3){
            //括号运算
            TreeNode exprNode = factorLayer.get(1);
            String exprSymbol = forExpression(exprNode);
            return exprSymbol;
        }
        else{
            TreeNode varOrNumNode = factorLayer.get(0);
            if (varOrNumNode.getType() == StmtType.VARIABLE){
                //变量
                //variable -> identifier [ [ exp ] ]
                ArrayList<TreeNode> varList = varOrNumNode.getNextLayer();
                if (varList.size() == 1){
                    return varList.get(0).toString();
                }
                else{
                    //数组
                    //获取数组元素，赋值给一个临时变量，添加TAC(ASSIGN, ARRNAME, INDEX, TEMPVAR)，返回临时变量
                    String arrName = varList.get(0).toString();
                    TreeNode exprNode = varList.get(2);
                    String exprSymbol = forExpression(exprNode);
                    String tempVar = "TempVariable"+tempindex.toString();
                    tempindex++;
                    TAC varTac = new TAC(NONE, arrName, exprSymbol, tempVar);
                    interStatement.add(varTac);

                    return tempVar;
                }

            }
            else{
                //数字
                TokenType numToken = varOrNumNode.getValueType();
                if (numToken == TokenType.INT_VALUE){
                    return varOrNumNode.toString();
                }
                else if (numToken == TokenType.FLOAT_VALUE){
                    return varOrNumNode.toString();
                }
                else if (numToken == TokenType.HEX_FLOAT){
                    return varOrNumNode.toString();
                }
                else{
                    return varOrNumNode.toString();
                }
            }
        }
    }

    //测试用
    public void output(){
        for (int i = 0; i < interStatement.size(); i++){
            System.out.println(String.valueOf(i) + "    " + interStatement.get(i).toString());
        }
    }

}
