package LexParser;
import GUI.IMainForm;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
/**
 * Created by qingduo-feng on 2017/10/9.
 */
public class LexAnalysis {
    private String[] fileStr = null;
    private int lineLength = 0;         // current line length
    private int lineNum = 0;            // total line number
    private int readHead = 0;           // current position (column number)
    private int readLine = 0;           // current line number
    private char[] currentLine;         // current line characters
    private Map<String, TokenType> reservedToken = new HashMap<String, TokenType>();
    private ArrayList<Token> result = new ArrayList<Token>();
    private IMainForm mf;

    //initialize
    public LexAnalysis(String[] file, IMainForm mf){
        int len = file.length;
        fileStr = new String[len];
        fileStr = file;
        initMap();
        lineNum = file.length;
        this.mf = mf;
    }

    public LexAnalysis(String[] file){
        int len = file.length;
        fileStr = new String[len];
        fileStr = file;
        initMap();
        lineNum = file.length;
    }

    //initialize the reservedToken
    private void initMap(){
        reservedToken.put("if", TokenType.IF);
        reservedToken.put("else", TokenType.ELSE);
        reservedToken.put("while", TokenType.WHILE);
        reservedToken.put("read", TokenType.READ);
        reservedToken.put("write", TokenType.WRITE);
        reservedToken.put("int", TokenType.INT);
        reservedToken.put("double", TokenType.DOUBLE);
        reservedToken.put("true", TokenType.TRUE);
        reservedToken.put("false", TokenType.FALSE);
        reservedToken.put("for", TokenType.FOR);
        reservedToken.put("break",TokenType.BREAK);
    }

    private boolean forward(){
        readHead++;
        //turn to the next line
        if ((readHead >= lineLength) && (readLine < lineNum)){
            readLine ++;
            readHead = 0;
            currentLine = fileStr[readLine-1].toCharArray();
            lineLength = currentLine.length;
            return true;
        }
        //readHead has moved to the end
        else if (readLine == lineNum && readHead >= lineLength){
            // forward to the end
            return false;
        }
        else {
            return true;
        }
    }

    private boolean backward(){
        if (readHead == 0){
            readLine--;
            currentLine = fileStr[readLine-1].toCharArray();
            lineLength = currentLine.length;
            readHead = lineLength - 1;
            return true;
        }
        else{
            readHead--;
            return true;
        }
    }

    //judge if the character is blank
    private boolean isBlank(char c){
        if ((c == ' ') || (c == '\t') || (c == '\n') || (c == '\0') || (c == '\r')){
            return true;
        }
        return false;
    }

    private boolean isNum(char c){
        if (c >= '0' && c <='9'){
            return true;
        }
        return false;
    }

    private boolean isChar(char c){
        if (c >= 'a' && c <= 'z' || c >= 'A' && c <='Z'){
            return  true;
        }
        else{
            return false;
        }
    }

    //judge if the character is digit
    private void readDigit() throws Exception{
        StringBuilder strBuilder = new StringBuilder();
        int temp = readHead;

        if (currentLine[temp] == '0') {
            strBuilder.append("0");
            temp++;
            if (currentLine[temp] == 'x' || currentLine[temp] == 'X'){
                do {
                    strBuilder.append(currentLine[temp]);
                    temp++;
                    if (temp == lineLength){
                        break;
                    }
                } while (isNum(currentLine[temp]) || (currentLine[temp] <= 'f' && currentLine[temp] >= 'a') || (currentLine[temp] <= 'F' && currentLine[temp] >= 'A'));
            }
        }
        else if (currentLine[temp] >= '1' && currentLine[temp] <= '9') {
            do {
                strBuilder.append(currentLine[temp]);
                temp++;
                if (temp == lineLength){
                    break;
                }
            } while (isNum(currentLine[temp]));
        }

        if (currentLine[temp] == '.') {
            do {
                strBuilder.append(currentLine[temp]);
                temp++;
                if (temp == lineLength){
                    break;
                }
            } while (isNum(currentLine[temp]));
        }
        else if (isChar(currentLine[temp])){
            //other character exists in the digit string
            String errorInfo = String.format("LexExcepetion: %s  position: row: %s, col: %s","number string can't have letters",  readHead+1, readLine);
            mf.reportErr(errorInfo);
            throw new LexException(readHead+1, readLine, "number string can't have letters");
        }

        //判断数字类型
        String strResult = strBuilder.toString();
        if ((strResult.contains("x") || strResult.contains("X")) && (strResult.contains("."))) {
            result.add(new Token(strResult, TokenType.HEX_FLOAT, readHead+1, readLine));
        }
        else if (strResult.contains(".")) {
            result.add(new Token(strResult, TokenType.FLOAT_VALUE, readHead+1, readLine));
        }
        else if (strResult.contains("x") || strResult.contains("X")) {
            result.add(new Token(strResult, TokenType.HEX_INT, readHead+1, readLine));
        }
        else{
            result.add(new Token(strResult, TokenType.INT_VALUE, readHead+1, readLine));
        }
        temp--;
        readHead = temp;
    }

    private void readString() throws Exception{
        int temp = readHead;
        StringBuilder strBuilder = new StringBuilder();
        do {
            strBuilder.append(currentLine[temp]);
            temp++;
            if (temp == lineLength){
                break;
            }
        } while (!isBlank(currentLine[temp]) && temp < lineLength && (isChar(currentLine[temp]) || currentLine[temp] == '_' || isNum(currentLine[temp])));

        if (strBuilder.charAt(strBuilder.length() - 1) == '_'){
            //string can't be ended with '_'
            String errorInfo = String.format("LexExcepetion: %s  position: row: %s, col: %s","string can't be ended with '_'",  readHead+1, readLine);
            mf.reportErr(errorInfo);
            throw new LexException(readHead+1, readLine, "string can't be ended with '_'");
        }

        String strResult = strBuilder.toString();
        TokenType resultType = judgeReservedWord(strResult);
        result.add(new Token(strResult, resultType, readHead+1, readLine));

        temp--;
        readHead = temp;
    }

    private void readLess(){
        int temp = readHead;
        temp++;
        if (currentLine[temp] == '='){
            result.add(new Token("<=", TokenType.LESSOREQUAL, readHead+1, readLine));
            forward();
        }
        else{
            result.add(new Token("<", TokenType.LESS, readHead+1, readLine));
        }
    }

    private void readGreater(){
        int temp = readHead;
        temp++;
        if (currentLine[temp] == '='){
            result.add(new Token(">=", TokenType.GREATEROREQUAL, readHead+1, readLine));
            forward();
        }
        else{
            result.add(new Token(">", TokenType.GREATER, readHead+1, readLine));
        }
    }

    private void readNotEqual() throws Exception{
        int temp = readHead;
        temp++;
        if (currentLine[temp] == '='){
            result.add(new Token("!=", TokenType.NOTEQUAL, readHead+1, readLine));
            forward();
        }
        else{
            //error
            String errorInfo = String.format("LexExcepetion: %s  position: row: %s, col: %s","expect =",  readHead+1, readLine);
            mf.reportErr(errorInfo);
            throw new LexException(readHead+1, readLine, "expect =");
        }
    }

    private void readEqual() {
        int temp = readHead;
        temp++;
        if (currentLine[temp] == '='){
            result.add(new Token("==", TokenType.EQUAL, readHead+1, readLine));
            forward();
        }
        else{
            result.add(new Token("=", TokenType.ASSIGN, readHead+1, readLine));
        }
    }

    private void readSlash() throws Exception{
        int temp = readHead;
        temp++;
        if (temp < lineLength) {
            if (currentLine[temp] == '/') {
                //single line note
                StringBuilder strBuilder = new StringBuilder();
                do {
                    strBuilder.append(currentLine[readHead]);
                    forward();
                } while (readHead != 0);
                //exit the while circle, back the readHead to the last line
                backward();
                result.add(new Token(strBuilder.toString(), TokenType.LINENOTE, temp, readLine - 1));
            } else if (currentLine[temp] == '*') {
                //multi note
                int tempLine = readLine;
                StringBuilder strBuilder = new StringBuilder();
                strBuilder.append("/*");
                readHead = temp + 1;//ignore the symbol of "/"
                do {
                    if (!forward()) {
                        //note does not end with */
                        String errorInfo = String.format("LexExcepetion: %s  position: row: %s, col: %s","Mulnote does not end with */",  readHead+1, readLine);
                        mf.reportErr(errorInfo);
                        throw  new LexException(readHead+1, readLine, "Mulnote does not end with */");
                    }
                    if (lineLength == 0){
                        continue;
                    }

                    if (lineLength >= 2 && readHead <= lineLength - 2) {
                        if (currentLine[readHead] == '*' && currentLine[readHead + 1] == '/') {
                            strBuilder.append("*/");
                            forward();
                            break;
                        }
                    }
                    strBuilder.append(currentLine[readHead]);
                } while (true);
                result.add(new Token(strBuilder.toString(), TokenType.MULNOTE, temp, tempLine));
            } else {
                result.add(new Token("/", TokenType.DIV, readHead+1, readLine));
            }
        }
        else{
            result.add(new Token("/", TokenType.DIV, readHead+1, readLine));
        }
    }

    private TokenType judgeReservedWord(String word){
        if (reservedToken.containsKey(word)){
            return reservedToken.get(word);
        }
        else{
            return TokenType.IDENTIFIER;
        }
    }

    public ArrayList<Token> analysis() throws Exception{
        if (lineNum > 0){
            lineLength = fileStr[0].length();
            currentLine = new char[lineLength];
            currentLine = fileStr[0].toCharArray();
            readLine = 1;
        }
        else{
            return result;
        }

        do {
            if (lineLength > 0) {
                switch (currentLine[readHead]) {
                    case '+':
                        result.add(new Token("+", TokenType.PLUS, readHead + 1, readLine));
                        break;
                    case '-':
                        result.add(new Token("-", TokenType.MINUS, readHead + 1, readLine));
                        break;
                    case '*':
                        result.add(new Token("*", TokenType.MUL, readHead + 1, readLine));
                        break;
                    case '/':
                        readSlash();
                        break;
                    case '=':
                        readEqual();
                        break;
                    case '<':
                        readLess();
                        break;
                    case '>':
                        readGreater();
                        break;
                    case '!':
                        readNotEqual();
                        break;
                    case '(':
                        result.add(new Token("(", TokenType.LEFTBRACKET, readHead + 1, readLine));
                        break;
                    case ')':
                        result.add(new Token(")", TokenType.RIGHTBRACKET, readHead + 1, readLine));
                        break;
                    case ';':
                        result.add(new Token(";", TokenType.SEMICOLON, readHead + 1, readLine));
                        break;
                    case ',':
                        result.add(new Token(",", TokenType.COLON, readHead + 1, readLine));
                        break;
                    case '{':
                        result.add(new Token("{", TokenType.LEFTBRACE, readHead + 1, readLine));
                        break;
                    case '}':
                        result.add(new Token("}", TokenType.RIGHTBRACE, readHead + 1, readLine));
                        break;
                    case '[':
                        result.add(new Token("[", TokenType.LEFTINDEX, readHead + 1, readLine));
                        break;
                    case ']':
                        result.add(new Token("]", TokenType.RIGHTINDEX, readHead + 1, readLine));
                        break;
                    default:
                        if (isNum(currentLine[readHead])) {
                            readDigit();
                        } else if (isChar(currentLine[readHead])) {
                            readString();
                        } else if (isBlank(currentLine[readHead])) {
                            continue;
                        } else {
                            //Unknown character
                            String errorInfo = String.format("LexExcepetion: %s  position: row: %s, col: %s","Uknown character",  readHead+1, readLine);
                            mf.reportErr(errorInfo);
                            throw new LexException(readHead+1, readLine, "Uknown character");
                        }
                }
            }

        } while(forward());

        return  result;
    }

}
