package GrammaticalParser;
import LexParser.Token;
import LexParser.TokenType;

import java.util.ArrayList;

public class TreeNode {
    private ArrayList<TreeNode> nextLayer;
    private StmtType type = null;
    private Token value;   //value类型需要赋值


    public TreeNode(StmtType type) {
        this.type = type;
    }

    public void setNext(ArrayList<TreeNode> next){
        this.nextLayer = next;
    }

    public ArrayList<TreeNode> getNextLayer() {
        return nextLayer;
    }

    public StmtType getType() {
        return type;
    }

    public TokenType getValueType(){
        return value.getType();
    }

    public String toString(){
        switch (this.type) {
            case STMTBLOCK:
                return "STMTBLOCK";
            case TYPE:
                return "TYPE";
            case IFSTMT:
                return "IFSTMT";
            case WHILESTMT:
                return "WHILESTMT";
            case BREAKSTMT:
                return "BRREAKSTMT";
            case ASSIGNSTMT:
                return "ASSIGNSTMT";
            case READSTMT:
                return "READSTMT";
            case WRITESTMT:
                return "WRITESTMT";
            case DECLARE:
                return "DECLARESTMT";
            case VARIABLE:
                return "VARIABLE";
            case EXPR:
                return "EXPRESSION";
            case AddtiveExpr:
                return "ADDTIVEEXPRESION";
            case Term:
                return "TERM";
            case FACTOR:
                return "FACTOR";
            case LOGICALOP:
                return "LOGICALOPERATION";
            case ADDOP:
                return "ADDOPERATION";
            case MULOP:
                return "MULOPARATION";
            case VALUE:
                return value.getValue();
            case STMT:
                return "STATEMENT";
            case PROGRAM:
                return "PROGRAM";
            case VARDEC:
                return "VARDEC";
            case FORSTMT:
                return "FORSTMT";
            default:
                return "NOT KNOWN";
        }
    }

    public void setValue(Token value){
        this.value = value;
    }
}
