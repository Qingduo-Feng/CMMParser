package SemanticAnalysis;

public class SemanticException extends Exception {
    private static String defaultMessage = "SemanticException: Unknown exception";

    public SemanticException(int col, int row, String exceptionMessage){
        super(String.format("GrammerExcepetion: %s  position: row: %s, col: %s", exceptionMessage, row, col));
    }

    public SemanticException(String errorMessage){
        super(errorMessage);
    }

    public SemanticException(){
        super(defaultMessage);
    }

    @Override
    public void printStackTrace(){
        super.printStackTrace();
    }
}