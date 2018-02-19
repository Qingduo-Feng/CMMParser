package GrammaticalParser;

public class GrammerException extends Exception {
    private static String defaultMessage = "GrammerException: Unknown exception";

    public GrammerException(int col, int row, String exceptionMessage){
        super(String.format("GrammerExcepetion: %s  position: row: %s, col: %s", exceptionMessage, row, col));
    }

    public GrammerException(){
        super(defaultMessage);
    }

    @Override
    public void printStackTrace(){
        super.printStackTrace();
    }
}
