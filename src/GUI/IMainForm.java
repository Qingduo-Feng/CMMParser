/**
 * Created by chenqian on 2017/12/9.
 */

package GUI;

import GrammaticalParser.TreeNode;
import LexParser.Token;
import SemanticAnalysis.TAC;

import java.util.ArrayList;

public interface IMainForm {

    public void build_start();

    //获取编辑面板的内容
    public String[] getEditorText();

    //设置编辑面板的内容
    public void setEditorText(String data);

    //设置中间代码板块内容
    public void setMidText(ArrayList<TAC> data);

    //设置词法分析板块内容
    public void setLexText(ArrayList<Token> data);

    //设置语法分析板块内容
    public void setGramText(TreeNode tree);

    //read函数调用
    public String readFuc();

    //write函数调用
    public void writeFunc(String data);

    //报错调用
    public void reportErr(String data);
}
