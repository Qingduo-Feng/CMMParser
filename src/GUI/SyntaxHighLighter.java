/**
 * Created by chenqian on 2017/12/9.
 */

package GUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class SyntaxHighLighter implements DocumentListener {
    private Set<String> keywords;
    private Set<String> types;
    private Set<String> functions;
    private Style keywordStyle;
    private Style functionStyle;
    private Style normalStyle;
    private Style numberStyle;
    private Style typeStyle;

    public SyntaxHighLighter(JTextPane editor) {
        keywordStyle = ((StyledDocument) editor.getDocument()).addStyle("Keyword_Style", null);
        numberStyle = ((StyledDocument) editor.getDocument()).addStyle("Number_Style", null);
        normalStyle = ((StyledDocument) editor.getDocument()).addStyle("Normal_Style", null);
        typeStyle = ((StyledDocument) editor.getDocument()).addStyle("Type_Style", null);
        functionStyle = ((StyledDocument) editor.getDocument()).addStyle("Function_Style", null);
        StyleConstants.setForeground(keywordStyle, new Color(104, 166, 174));
        StyleConstants.setForeground(normalStyle, Color.BLACK);
        StyleConstants.setForeground(numberStyle, new Color(121, 91, 180));
        StyleConstants.setForeground(typeStyle, new Color(213, 0, 92));
        StyleConstants.setForeground(functionStyle, new Color(161, 120, 29));
        keywords = new HashSet<String>();
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("for");
        keywords.add("break");
        types = new HashSet<String>();
        types.add("int");
        types.add("double");
        functions = new HashSet<String>();
        functions.add("read");
        functions.add("write");
    }

    public void colouring(StyledDocument doc, int pos, int len) throws BadLocationException {
        int start = indexOfWordStart(doc, pos);
        int end = indexOfWordEnd(doc, pos + len);

        char ch;
        while (start < end) {
            ch = getCharAt(doc, start);
            if (Character.isLetter(ch) || ch == '_' || Character.isDigit(ch) || ch =='.') {
                start = colouringWord(doc, start);
            } else {
                SwingUtilities.invokeLater(new ColouringTask(doc, start, 1, normalStyle));
                ++start;
            }
        }
    }
    public int colouringWord(StyledDocument doc, int pos) throws BadLocationException {
        int wordEnd = indexOfWordEnd(doc, pos);
        String word = doc.getText(pos, wordEnd - pos);

        if (keywords.contains(word)) {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, keywordStyle));
        } else if(types.contains(word)) {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, typeStyle));
        }else if(functions.contains(word)) {
            SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, functionStyle));
        }else {
            boolean a=true;
            for(int i=0;i<word.length();i++){
                if(!Character.isDigit(word.charAt(i))&&word.charAt(i)!='.'){
                    a=false;
                    break;
                }
            }
            if(a){
                SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, numberStyle));
            }else
                SwingUtilities.invokeLater(new ColouringTask(doc, pos, wordEnd - pos, normalStyle));

        }

        return wordEnd;
    }

    public char getCharAt(Document doc, int pos) throws BadLocationException {
        return doc.getText(pos, 1).charAt(0);
    }
    public int indexOfWordStart(Document doc, int pos) throws BadLocationException {
        for (; pos > 0 && isWordCharacter(doc, pos - 1); --pos);
        return pos;
    }
    public int indexOfWordEnd(Document doc, int pos) throws BadLocationException {
        for (; isWordCharacter(doc, pos); ++pos);

        return pos;
    }
    public boolean isWordCharacter(Document doc, int pos) throws BadLocationException {
        char ch = getCharAt(doc, pos);
        if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_' || ch=='.') { return true; }
        return false;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), e.getLength());
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            colouring((StyledDocument) e.getDocument(), e.getOffset(), 0);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
    }

    private class ColouringTask implements Runnable {
        private StyledDocument doc;
        private Style style;
        private int pos;
        private int len;

        public ColouringTask(StyledDocument doc, int pos, int len, Style style) {
            this.doc = doc;
            this.pos = pos;
            this.len = len;
            this.style = style;
        }

        public void run() {
            try {
                doc.setCharacterAttributes(pos, len, style, true);
            } catch (Exception e) {

            }
        }
    }
}
