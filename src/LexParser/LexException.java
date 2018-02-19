/**
 * Created by qingduo-feng on 2017/10/11.
 */
package LexParser;
public class LexException extends Exception {
    private static String defaultMessage = "LexException: Unknown exception";

    public LexException(int col, int row, String exceptionMessage){
        super(String.format("LexExcepetion: %s  position: row: %s, col: %s", exceptionMessage, row, col));
    }

    public LexException(){
        super(defaultMessage);
    }

    @Override
    public void printStackTrace(){
        super.printStackTrace();
    }
}
