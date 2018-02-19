/**
 * Created by qingduo-feng on 2017/10/9.
 */
package LexParser;
public enum TokenType {
    //Reserved word
    IF, ELSE, WHILE, READ, WRITE, INT, DOUBLE, TRUE, FALSE,
    //Special symbol
    //Operator
    PLUS, MINUS, MUL, DIV, ASSIGN, //+, -, *, /, =
    //Comparison
    LESS, LESSOREQUAL, GREATER, GREATEROREQUAL, EQUAL, NOTEQUAL, //< <= > >= == !=
    //Bound symbol
    LEFTBRACKET, RIGHTBRACKET, SEMICOLON, COLON, LEFTBRACE, RIGHTBRACE,  //( ) ; , { }
    LEFTINDEX, RIGHTINDEX, //[ ]
    //Note
    LINENOTE, // //
    MULNOTE, // /* */
    //Identifier
    IDENTIFIER,
    //Number value
    INT_VALUE, FLOAT_VALUE,
    HEX_INT, HEX_FLOAT,
    //
    NULL,
    BREAK,
    FOR
}
