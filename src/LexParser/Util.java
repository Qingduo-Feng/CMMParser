package LexParser;
import java.util.ArrayList;

/**
 * Created by qingduo-feng on 2017/10/10.
 */
public class Util {
    public static void ConsoleOutput(ArrayList<Token> tokens){
        for (Token token:
             tokens) {
            System.out.printf("{%-2d  %-2d  %-12s %-15s}\n", token.getRow(),token.getCol(), token.getType().toString(), token.getValue());
        }
    }
}
