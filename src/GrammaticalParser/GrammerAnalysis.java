package GrammaticalParser;
import GUI.IMainForm;
import LexParser.Token;
import LexParser.TokenType;
import java.util.ArrayList;
import java.util.ListIterator;

public class GrammerAnalysis {
    private Token currentToken = null;
    private ArrayList<Token> inputTokens = null;
    private ListIterator<Token> tokenIterator = null;       //Token迭代器
    private TreeNode result = null;                         //根节点
    private IMainForm mf;

    //获得下一个token
    public Token getNext() throws Exception{
        if (tokenIterator.hasNext()){
            Token token = tokenIterator.next();
            this.currentToken = token;
            return token;
        }
        else{
            throw new GrammerException( this.currentToken.getCol(), this.currentToken.getRow(),"can not move to the next symbol");
        }
    }

    public void getPrevious(){
        Token token = this.tokenIterator.previous();
        this.currentToken = token;
    }

    public GrammerAnalysis(ArrayList<Token> input, IMainForm mf){
        this.inputTokens = input;
        this.tokenIterator = input.listIterator();
        result = new TreeNode(StmtType.PROGRAM);
        this.mf = mf;
    }

    public GrammerAnalysis(ArrayList<Token> input){
        this.inputTokens = input;
        this.tokenIterator = input.listIterator();
        result = new TreeNode(StmtType.PROGRAM);
    }

    public TreeNode parse() throws Exception{
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        while (this.tokenIterator.hasNext()){
            temp.add(parseStmt());
        }

        this.result.setNext(temp);
        return this.result;
    }

    private TreeNode parseStmt() throws Exception{
        //statement -> if-stmt | while-stmt | assign-stmt | read-stmt | write-stmt | declare-stmt
        TreeNode result = new TreeNode(StmtType.STMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token = getNext();
        TokenType type = token.getType();
        getPrevious();
        switch (type) {
            case IF:
                temp.add(parseIfStmt());
                break;
            case WHILE:
                temp.add(parseWhileStmt());
                break;
            case LEFTBRACE:
                temp.add(parseStmtBlock());
                break;
            case READ:
                temp.add(parseReadStmt());
                break;
            case WRITE:
                temp.add(parseWriteStmt());
                break;
            case IDENTIFIER:
                temp.add(parseAssignStmt());
                break;
            case BREAK:
                temp.add(parseBreakStmt());
                break;
            case INT:
                temp.add(parseDeclareStmt());
                break;
            case DOUBLE:
                temp.add(parseDeclareStmt());
                break;
            case LINENOTE:
                getNext();
                break;
            case MULNOTE:
                getNext();
                break;
            case FOR:
                temp.add(parseForStmt());
                break;
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseStmtBlock() throws Exception{
        //ok
        //{{Stmt}}
        TreeNode result = new TreeNode(StmtType.STMTBLOCK);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();
        if (token1.getType() != TokenType.LEFTBRACE){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","Expect {",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException( token1.getCol(), token1.getRow(),"Expect {");
        }
        TreeNode braceTemp = new TreeNode(StmtType.VALUE);
        braceTemp.setValue(token1);
        temp.add(braceTemp);
        //读stmt
        Token token2 = getNext();
        while (token2.getType() != TokenType.RIGHTBRACE) {
            getPrevious();
            TreeNode stmtTemp = parseStmt();
            temp.add(stmtTemp);
            token2 = getNext();
        }
        TreeNode brace2Temp = new TreeNode(StmtType.VALUE);
        brace2Temp.setValue(token2);
        temp.add(brace2Temp);

        result.setNext(temp);
        return result;
    }

    private TreeNode parseIfStmt() throws Exception{
        //ok
        //if ( exp ) stmt-block | if ( exp ) stmt-block else stmt-block
        TreeNode result = new TreeNode(StmtType.IFSTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();
        if (token1.getType() != TokenType.IF){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect if",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token1.getCol(), token1.getRow(), "expect if");
        }
        TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
        tempNode1.setValue(token1);
        temp.add(tempNode1);
        //读（
        Token token2 = getNext();
        if (token2.getType() != TokenType.LEFTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect (",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect (");
        }
        TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
        tempNode2.setValue(token2);
        temp.add(tempNode2);
        //读expr
        TreeNode exprTemp = parseExpr();
        temp.add(exprTemp);
        //读）
        Token token3 = getNext();
        if (token3.getType() != TokenType.RIGHTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect )",  token3.getRow(), token3.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token3.getCol(), token3.getRow(), "expect )");
        }
        TreeNode tempNode3 = new TreeNode(StmtType.VALUE);
        tempNode3.setValue(token3);
        temp.add(tempNode3);
        //读Stmt-block
        TreeNode stmtTemp = parseStmtBlock();
        temp.add(stmtTemp);
        //读else
        if (tokenIterator.hasNext()) {
            Token token4 = getNext();
            if (token4.getType() == TokenType.ELSE) {
                TreeNode tempNode4 = new TreeNode(StmtType.VALUE);
                tempNode4.setValue(token4);
                temp.add(tempNode4);
                //读stmtblock
                TreeNode elseTemp = parseStmtBlock();
                temp.add(elseTemp);
            } else {
                getPrevious();
            }
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseWhileStmt() throws Exception{
        //ok
        //while (Expr) Stmt-block
        TreeNode result = new TreeNode(StmtType.WHILESTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();
        if (token1.getType() != TokenType.WHILE){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect while",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token1.getCol(), token1.getRow(), "expect while");
        }
        TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
        tempNode1.setValue(token1);
        temp.add(tempNode1);
        //读（
        Token token2 = getNext();
        if (token2.getType() != TokenType.LEFTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect (",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect (");
        }
        TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
        tempNode2.setValue(token2);
        temp.add(tempNode2);
        //读expr
        TreeNode exprTemp = parseExpr();
        temp.add(exprTemp);
        //读）
        Token token3 = getNext();
        if (token3.getType() != TokenType.RIGHTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect )",  token3.getRow(), token3.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token3.getCol(), token3.getRow(), "expect )");
        }
        TreeNode tempNode3 = new TreeNode(StmtType.VALUE);
        tempNode3.setValue(token3);
        temp.add(tempNode3);
        //读stmt-block
        TreeNode stmtTemp = parseStmtBlock();
        temp.add(stmtTemp);
        result.setNext(temp);
        return result;
    }

    private TreeNode parseForStmt() throws Exception{
        //forstmt -> for ( stmt ; stmt; stmt ) stmtblock
        TreeNode result = new TreeNode(StmtType.FORSTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();
        if (token1.getType() != TokenType.FOR){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect for",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token1.getCol(), token1.getRow(), "expect while");
        }
        TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
        tempNode1.setValue(token1);
        temp.add(tempNode1);

        //读(
        Token token2 = getNext();
        if (token2.getType() != TokenType.LEFTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect (",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect (");
        }
        TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
        tempNode2.setValue(token2);
        temp.add(tempNode2);
        //读stmt
        TreeNode stmtTemp1 = parseStmt();
        temp.add(stmtTemp1);

        //读stmt
        TreeNode stmtTemp2 = parseExpr();
        temp.add(stmtTemp2);

        //读;
        Token token3 = getNext();
        if (token3.getType() != TokenType.SEMICOLON){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ;",  token3.getRow(), token3.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token3.getCol(), token3.getRow(), "expect ;");
        }
        TreeNode semiTemp = new TreeNode(StmtType.VALUE);
        semiTemp.setValue(token3);
        temp.add(semiTemp);

        //读stmt
        TreeNode resultb = new TreeNode(StmtType.ASSIGNSTMT);
        ArrayList<TreeNode> tempb = new ArrayList<TreeNode>();
        //读variable
        TreeNode valueTemp = parseVariable();
        tempb.add(valueTemp);
        //读=
        Token token2b = getNext();
        if (token2b.getType() != TokenType.ASSIGN){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect =",  token2b.getRow(), token2b.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2b.getCol(), token2b.getRow(), "expect =");
        }
        TreeNode assignTemp = new TreeNode(StmtType.VALUE);
        assignTemp.setValue(token2b);
        tempb.add(assignTemp);
        //读expr
        TreeNode exprTemp = parseExpr();
        tempb.add(exprTemp);

        resultb.setNext(tempb);

        temp.add(resultb);

        //读）
        Token token5 = getNext();
        if (token5.getType() != TokenType.RIGHTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect )",  token5.getRow(), token5.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token5.getCol(), token5.getRow(), "expect )");
        }
        TreeNode tempNode5 = new TreeNode(StmtType.VALUE);
        tempNode5.setValue(token5);
        temp.add(tempNode5);

        //读stmtblock
        TreeNode stmtTemp4 = parseStmtBlock();
        temp.add(stmtTemp4);

        result.setNext(temp);
        return result;
    }

    private TreeNode parseBreakStmt() throws Exception{
        //ok
        //break ;
        TreeNode result = new TreeNode(StmtType.BREAKSTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();
        if (token1.getType() != TokenType.BREAK){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect break",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token1.getCol(), token1.getRow(), "expect break");
        }
        TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
        tempNode1.setValue(token1);
        temp.add(tempNode1);
        //读；
        Token token2 = getNext();
        if (token2.getType() != TokenType.SEMICOLON){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ;",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect ;");
        }
        TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
        tempNode2.setValue(token2);
        temp.add(tempNode2);
        result.setNext(temp);
        return result;
    }

    private TreeNode parseReadStmt() throws Exception{
        //ok
        //read(variable);
        TreeNode result = new TreeNode(StmtType.READSTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();
        if (token1.getType() != TokenType.READ){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect read",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token1.getCol(), token1.getRow(), "expect read");
        }
        TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
        tempNode1.setValue(token1);
        temp.add(tempNode1);
        //读（
        Token token2 = getNext();
        if (token2.getType() != TokenType.LEFTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect (",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect (");
        }
        TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
        tempNode2.setValue(token2);
        temp.add(tempNode2);
        //读expr
        TreeNode exprTemp = parseVariable();
        temp.add(exprTemp);
        //读）
        Token token3 = getNext();
        if (token3.getType() != TokenType.RIGHTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect }",  token3.getRow(), token3.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token3.getCol(), token3.getRow(), "expect )");
        }
        TreeNode tempNode3 = new TreeNode(StmtType.VALUE);
        tempNode3.setValue(token3);
        temp.add(tempNode3);

        //读；
        Token token4= getNext();
        if (token4.getType() != TokenType.SEMICOLON){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ;",  token4.getRow(), token4.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token4.getCol(), token4.getRow(), "expect ;");
        }
        TreeNode tempNode4 = new TreeNode(StmtType.VALUE);
        tempNode4.setValue(token4);
        temp.add(tempNode4);

        result.setNext(temp);
        return result;
    }

    private TreeNode parseWriteStmt() throws Exception{
        //ok
        //write(Expr);
        TreeNode result = new TreeNode(StmtType.WRITESTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token1 = getNext();//获得write
        if (token1.getType() != TokenType.WRITE){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect WRITE",  token1.getRow(), token1.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token1.getCol(), token1.getRow(), "expect WRITE");
        }
        TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
        tempNode1.setValue(token1);
        temp.add(tempNode1);

        //读（
        Token token2 = getNext();
        if (token2.getType() != TokenType.LEFTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect (",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect (");
        }
        TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
        tempNode2.setValue(token2);
        temp.add(tempNode2);
        //读expr
        TreeNode exprTemp = parseExpr();
        temp.add(exprTemp);
        //读）
        Token token3 = getNext();
        if (token3.getType() != TokenType.RIGHTBRACKET){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect )",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect )");
        }
        TreeNode tempNode3 = new TreeNode(StmtType.VALUE);
        tempNode3.setValue(token3);
        temp.add(tempNode3);

        //读；
        Token token4= getNext();
        if (token4.getType() != TokenType.SEMICOLON){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ;",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect ;");
        }
        TreeNode tempNode4 = new TreeNode(StmtType.VALUE);
        tempNode4.setValue(token4);
        temp.add(tempNode4);

        result.setNext(temp);
        return result;
    }

    private TreeNode parseAssignStmt() throws Exception{
        //ok
        // Variable = Expr ;
        TreeNode result = new TreeNode(StmtType.ASSIGNSTMT);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        //读variable
        TreeNode valueTemp = parseVariable();
        temp.add(valueTemp);
        //读=
        Token token2 = getNext();
        if (token2.getType() != TokenType.ASSIGN){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect =",  token2.getRow(), token2.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token2.getCol(), token2.getRow(), "expect =");
        }
        TreeNode assignTemp = new TreeNode(StmtType.VALUE);
        assignTemp.setValue(token2);
        temp.add(assignTemp);
        //读expr
        TreeNode exprTemp = parseExpr();
        temp.add(exprTemp);
        //读；
        Token token3 = getNext();
        if (token3.getType() != TokenType.SEMICOLON){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ;",  token3.getRow(), token3.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token3.getCol(), token3.getRow(), "expect ;");
        }
        TreeNode semiTemp = new TreeNode(StmtType.VALUE);
        semiTemp.setValue(token3);
        temp.add(semiTemp);

        result.setNext(temp);
        return result;
    }

    private TreeNode parseDeclareStmt() throws Exception{
        //???
        //declare-stmt -> TYPE VARDEC {, VARDEC};
        TreeNode result = new TreeNode(StmtType.DECLARE);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        TreeNode typeTemp = parseType();
        temp.add(typeTemp);

        TreeNode varTemp = parseVardec();
        temp.add(varTemp);

        Token token = getNext();
        while (token.getType() == TokenType.COLON){
            TreeNode colonNode = new TreeNode(StmtType.VALUE);
            colonNode.setValue(token);
            temp.add(colonNode);

            TreeNode varNode = parseVardec();
            temp.add(varNode);
            token = getNext();
        }

        //读；
        if (token.getType() != TokenType.SEMICOLON){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ;",   this.currentToken.getRow(),  this.currentToken.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(this.currentToken.getCol(), this.currentToken.getRow(), "expect ;");
        }
        TreeNode SemiTemp = new TreeNode(StmtType.VALUE);
        SemiTemp.setValue(token);
        temp.add(SemiTemp);
        result.setNext(temp);
        return result;
    }

    private TreeNode parseVardec() throws Exception{
        //IDENTIFIER [= EXPRESSION]
        TreeNode result = new TreeNode(StmtType.VARDEC);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();

        //读IDENTIFIER
        Token token = getNext();
        if (token.getType() != TokenType.IDENTIFIER){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect identifier",  token.getRow(), token.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token.getCol(), token.getRow(), "expect identifier");
        }
        TreeNode identiTemp = new TreeNode(StmtType.VALUE);
        identiTemp.setValue(token);
        temp.add(identiTemp);
        //读 =
        Token token2 = getNext();
        if (token2.getType() == TokenType.ASSIGN){
            TreeNode tempNode = new TreeNode(StmtType.VALUE);
            tempNode.setValue(token2);
            temp.add(tempNode);
            TreeNode tempNode2 = parseExpr();
            temp.add(tempNode2);
        }
        else{
            getPrevious();
        }

        result.setNext(temp);
        return result;
    }

    private TreeNode parseType() throws Exception{
        //ok
        //(int | double) [ [factor] ]
        TreeNode result = new TreeNode(StmtType.TYPE);
        Token token = getNext();
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        //读type
        TreeNode typeTemp = new TreeNode(StmtType.VALUE);
        if (token.getType() == TokenType.INT){
            typeTemp.setValue(token);
        }
        else if (token.getType() == TokenType.DOUBLE){
            typeTemp.setValue(token);
        }
        else{
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect correct type",  token.getRow(), token.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token.getCol(), token.getRow(), "expect correct type");
        }
        temp.add(typeTemp);

        Token token2 = getNext();
        if (token2.getType() == TokenType.LEFTINDEX){
            TreeNode tempNode2 = new TreeNode(StmtType.VALUE);
            tempNode2.setValue(token2);
            temp.add(tempNode2);
            TreeNode tempNode3 = parseFactor();
            temp.add(tempNode3);
            Token token4 = getNext();
            if (token4.getType() != TokenType.RIGHTINDEX){
                String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect ]",  token4.getRow(), token4.getCol());
                mf.reportErr(errorInfo);
                throw new GrammerException(token4.getCol(), token4.getRow(), "expect ]");
            }
            TreeNode tempNode4 = new TreeNode(StmtType.VALUE);
            tempNode4.setValue(token4);
            temp.add(tempNode4);
        }
        else{
            getPrevious();
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseVariable() throws Exception{
        //variable -> identifier [ [ exp ] ]
        TreeNode result = new TreeNode(StmtType.VARIABLE);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        //读IDENTIFIER
        Token token = getNext();
        if (token.getType() != TokenType.IDENTIFIER){
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect identifier",  token.getRow(), token.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token.getCol(), token.getRow(), "expect identifier");
        }
        TreeNode valueTemp = new TreeNode(StmtType.VALUE);
        valueTemp.setValue(token);
        temp.add(valueTemp);
        //读[
        Token token2 = getNext();
        if (token2.getType() == TokenType.LEFTINDEX){
            TreeNode valueTemp2 = new TreeNode(StmtType.VALUE);
            valueTemp2.setValue(token2);
            temp.add(valueTemp2);
            TreeNode exprTemp = parseExpr();
            temp.add(exprTemp);
            Token token3 = getNext();
            if (token3.getType() != TokenType.RIGHTINDEX){
                String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect )",  token3.getRow(), token3.getCol());
                mf.reportErr(errorInfo);
                throw new GrammerException(token3.getCol(), token3.getRow(), "expect )");
            }
            TreeNode valueTemp3 = new TreeNode(StmtType.VALUE);
            valueTemp3.setValue(token3);
            temp.add(valueTemp3);
        }
        else{
            getPrevious();
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseExpr() throws Exception{
        //ok
        //exp -> addtive-exp logical-op addtive-exp | addtive-exp
        TreeNode result = new TreeNode(StmtType.EXPR);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        TreeNode addtiveOp = parseAddtiveExpr();
        temp.add(addtiveOp);
        Token token = getNext();
        if (token.getType() == TokenType.GREATER || token.getType() == TokenType.LESS || token.getType() == TokenType.GREATEROREQUAL || token.getType() == TokenType.LESSOREQUAL || token.getType() == TokenType.EQUAL || token.getType() == TokenType.NOTEQUAL){
            getPrevious();
            TreeNode logicalOp = parseLogicalOp();
            temp.add(logicalOp);
            TreeNode addtiveOp2 = parseAddtiveExpr();
            temp.add(addtiveOp2);
        }
        else{
            getPrevious();
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseAddtiveExpr() throws Exception{
        //ok
        //addtive-exp -> term add-op additive-exp | term
        TreeNode result = new TreeNode(StmtType.AddtiveExpr);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        TreeNode termTemp = parseTerm();
        temp.add(termTemp);
        Token token = getNext();
        if (token.getType() == TokenType.MINUS || token.getType() == TokenType.PLUS){
            getPrevious();
            TreeNode addTemp = parseAddOp();
            temp.add(addTemp);
            TreeNode addtiveTemp = parseAddtiveExpr();
            temp.add(addtiveTemp);
        }
        else{
            getPrevious();
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseTerm() throws Exception{
        //ok
        //term -> factor mul-op term | factor
        TreeNode result = new TreeNode(StmtType.Term);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        TreeNode factorTemp = parseFactor();
        temp.add(factorTemp);
        Token token = getNext();
        if (token.getType() == TokenType.MUL || token.getType() == TokenType.DIV){
            getPrevious();
            TreeNode mulTemp = parseMulOp();
            temp.add(mulTemp);
            TreeNode termTemp = parseTerm();
            temp.add(termTemp);
        }
        else{
            getPrevious();
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseFactor() throws Exception{
        //ok
        //factor -> ( exp ) | number | variable | Add-op exp
        TreeNode result = new TreeNode(StmtType.FACTOR);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();

        //读(expr)
        Token token = getNext();
        if (token.getType() == TokenType.LEFTBRACKET){
            TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
            tempNode1.setValue(token);
            temp.add(tempNode1);
            //读expr
            TreeNode exprTemp = parseExpr();
            temp.add(exprTemp);
            //读）
            Token token3 = getNext();
            if (token3.getType() != TokenType.RIGHTBRACKET){
                String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect )",  token3.getRow(), token3.getCol());
                mf.reportErr(errorInfo);
                throw new GrammerException(token3.getCol(), token3.getRow(), "expect )");
            }
            TreeNode tempNode3 = new TreeNode(StmtType.VALUE);
            tempNode3.setValue(token3);
            temp.add(tempNode3);
        }
        //读number
        else if (token.getType() == TokenType.INT_VALUE || token.getType() ==  TokenType.FLOAT_VALUE ||
                token.getType() == TokenType.HEX_INT || token.getType() ==  TokenType.HEX_FLOAT){
            TreeNode tempNode1 = new TreeNode(StmtType.VALUE);
            tempNode1.setValue(token);
            temp.add(tempNode1);
        }
        //读variable
        else if (token.getType() == TokenType.IDENTIFIER){
            getPrevious();
            TreeNode variableTemp = parseVariable();
            temp.add(variableTemp);
        }
        //读add-op expr
        else if (token.getType() == TokenType.MINUS || token.getType() == TokenType.PLUS){
            getPrevious();
            TreeNode addTemp = parseAddOp();
            TreeNode exprTemp = parseExpr();
            temp.add(addTemp);
            temp.add(exprTemp);
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseLogicalOp() throws Exception{
        //ok
        //logical-op -> > | < | >= | <= | <> | ==
        TreeNode result = new TreeNode(StmtType.LOGICALOP);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();

        Token token = getNext();
        if (token.getType() == TokenType.LESS){
//            if (getNext().getType() == TokenType.GREATER){
//                //<>
//                TreeNode valueTemp = new TreeNode(StmtType.VALUE);
//                valueTemp.setValue(new Token("<>", TokenType.NOTEQUAL, token.getCol(), token.getRow()));
//                temp.add(valueTemp);
//            }
//            else{
//                getPrevious();
//            }
//            //<
            TreeNode valueTemp = new TreeNode(StmtType.VALUE);
            valueTemp.setValue(token);
            temp.add(valueTemp);
        }
        else if (token.getType() == TokenType.GREATER){
            //>
            TreeNode valueTemp = new TreeNode(StmtType.VALUE);
            valueTemp.setValue(token);
            temp.add(valueTemp);
        }
        else if (token.getType() == TokenType.GREATEROREQUAL){
            //>=
            TreeNode valueTemp = new TreeNode(StmtType.VALUE);
            valueTemp.setValue(token);
            temp.add(valueTemp);
        }
        else if (token.getType() == TokenType.LESSOREQUAL){
            //<=
            TreeNode valueTemp = new TreeNode(StmtType.VALUE);
            valueTemp.setValue(token);
            temp.add(valueTemp);
        }
        else if (token.getType() == TokenType.EQUAL){
            //==
            TreeNode valueTemp = new TreeNode(StmtType.VALUE);
            valueTemp.setValue(token);
            temp.add(valueTemp);
        }
        else if (token.getType() == TokenType.NOTEQUAL){
            TreeNode valueTemp = new TreeNode(StmtType.VALUE);
            valueTemp.setValue(token);
            temp.add(valueTemp);
        }
        else{
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect logical operation",  token.getRow(), token.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token.getCol(), token.getRow(), "expect logical operation");
        }

        result.setNext(temp);
        return result;
    }

    private TreeNode parseAddOp() throws Exception{
        //add-op -> + | -
        TreeNode result = new TreeNode(StmtType.ADDOP);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token = getNext();
        TreeNode valueNode = new TreeNode(StmtType.VALUE);
        if (token.getType() == TokenType.PLUS){
            valueNode.setValue(token);
            temp.add(valueNode);
        }
        else if (token.getType() == TokenType.MINUS){
            valueNode.setValue(token);
            temp.add(valueNode);
        }
        else{
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect + or -",  token.getRow(), token.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token.getCol(), token.getRow(), "expect + or -");
        }
        result.setNext(temp);
        return result;
    }

    private TreeNode parseMulOp() throws Exception{
        //mul-op -> * | /

        TreeNode result = new TreeNode(StmtType.MULOP);
        ArrayList<TreeNode> temp = new ArrayList<TreeNode>();
        Token token = getNext();
        TreeNode valueNode = new TreeNode(StmtType.VALUE);
        if (token.getType() == TokenType.MUL){
            valueNode.setValue(token);
            temp.add(valueNode);
        }
        else if (token.getType() == TokenType.DIV){
            valueNode.setValue(token);
            temp.add(valueNode);
        }
        else{
            String errorInfo = String.format("GrammerExcepetion: %s  position: row: %s, col: %s","expect * or /",  token.getRow(), token.getCol());
            mf.reportErr(errorInfo);
            throw new GrammerException(token.getCol(), token.getRow(), "expect * or /");
        }
        result.setNext(temp);
        return result;
    }

}
