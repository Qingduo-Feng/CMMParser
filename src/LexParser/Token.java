/**
 * Created by qingduo-feng on 2017/10/9.
 */
package LexParser;
public class Token {
    // position of token
    int row;
    int col;
    TokenType type;
    String value;

    public Token(String value, TokenType type, int col, int row) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.type = type;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public TokenType getType(){
        return type;
    }

    public String getValue(){
        return value;
    }

}
