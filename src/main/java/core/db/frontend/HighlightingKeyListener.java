package core.db.frontend;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;
import javax.swing.text.*;

final class HighlightingKeyListener extends KeyAdapter{
	
    private StyledDocument doc;
    private JTextPane textPane;
    private int caretPastePosition;
    private static final String KEYWORDS[] = {
        "SELECT", "FROM", "WHERE", "JOIN", "INNER", "OUTER", "CROSS", "GROUP", "ORDER", "BY", 
        "HAVING", "INSERT", "UPDATE", "INTO", "VALUES"
    };
    protected HighlightingKeyListener(JTextPane textPane) {
        this.textPane = textPane;
        doc = textPane.getStyledDocument();
        addStylesToDocument(doc);
    }

    @Override
	public void keyPressed(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == 17)
            caretPastePosition = textPane.getCaretPosition();
    }

    @Override
	public void keyReleased(KeyEvent keyEvent) {
        try
        {
            if(keyEvent.getKeyCode() == 86 && keyEvent.isControlDown() || keyEvent.getKeyCode() == 17)
                coloringWords(caretPastePosition, new Integer(textPane.getCaretPosition() - caretPastePosition));
            if(keyEvent.getKeyCode() != 10) {
                coloringWords(textPane.getCaretPosition(), null);
                if(keyEvent.getKeyCode() == 32)
                    coloringWords(textPane.getCaretPosition() - 1, null);
            }
        }
        catch(BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void coloringWords(int initPos, Integer length) throws BadLocationException {
        int word[] = new int[2];
        if(length == null){
            if(textPane.getText(initPos, 1).equals(" ") || textPane.getText(initPos, 1).equals("\n")) {
                word = getLastWord(initPos);
                if(checkWord(textPane.getText(word[0], word[1])))
                    doc.setCharacterAttributes(word[0], word[1], doc.getStyle("blue"), true);
                else
                    doc.setCharacterAttributes(word[0], word[1], doc.getStyle("regular"), true);
            }
            if(textPane.getText(initPos <= 0 ? 0 : initPos - 1, 1).equals(" ") || textPane.getText(initPos <= 0 ? 0 : initPos - 1, 1).equals("\n") || initPos == 0) {
                word = getNextWord(initPos);
                if(checkWord(textPane.getText(word[0], word[1])))
                    doc.setCharacterAttributes(word[0], word[1], doc.getStyle("blue"), true);
                else
                    doc.setCharacterAttributes(word[0], word[1], doc.getStyle("regular"), true);
            }
            if(!textPane.getText(initPos <= 0 ? 0 : initPos - 1, 1).equals(" ") && (!textPane.getText(initPos, 1).equals("\n") && !textPane.getText(initPos, 1).equals(" ") && initPos != 0))
            {
                word = getCurrentWord(initPos);
                if(checkWord(textPane.getText(word[0], word[1])))
                    doc.setCharacterAttributes(word[0], word[1], doc.getStyle("blue"), true);
                else
                    doc.setCharacterAttributes(word[0], word[1], doc.getStyle("regular"), true);
            }
        } else {
            for(int pos = initPos; pos < initPos + length.intValue(); pos = pos + word[1] + 1) {
                word = getNextWord(pos);
                coloringWords(pos, null);
            }

        }
    }

    private int[] getCurrentWord(int initPos) throws BadLocationException {
        int word[] = new int[2];
        int min = initPos <= 0 ? initPos : initPos - 1;
        int max = initPos;
        if(textPane.getText(min, 1).equals(" ") || textPane.getText(min, 1).equals("\n") || textPane.getText(max, 1).equals(" ") || textPane.getText(max, 1).equals("\n"))
            return word;
        while(min > 0) 
        {
            min--;
            if(textPane.getText(min, 1).equals(" ") || min == 0 || textPane.getText(min, 1).equals("\n"))
                break;
        }
        for(; max < textPane.getStyledDocument().getLength(); max++)
            if(textPane.getText(max, 1).equals(" ") || textPane.getText(max, 1).equals("\n"))
                break;

        word[0] = min != 0 ? min + 1 : min;
        word[1] = min != 0 ? max - min - 1 : max - min;
        return word;
    }

    private int[] getLastWord(int initPos) throws BadLocationException {
        int word[] = new int[2];
        int min = initPos;
        int length = 0;
        boolean foundChar = false;
        while(min > 0) 
        {
            min--;
            if(textPane.getText(min, 1).equals(" ") || min == 0 || textPane.getText(min, 1).equals("\n"))
            {
                if(foundChar)
                    break;
            } else
            {
                length++;
                foundChar = true;
            }
        }
        word[0] = min != 0 ? min + 1 : 0;
        word[1] = min != 0 ? length : length + 1;
        return word;
    }

    private synchronized int[] getNextWord(int initPos) throws BadLocationException {
        int word[] = new int[2];
        int max = initPos >= 1 ? initPos : 0;
        int length = 0;
        boolean foundChar = false;
        for(; max < textPane.getStyledDocument().getLength(); max++)
            if(textPane.getText(max, 1).equals(" ") || textPane.getText(max, 1).equals("\n"))
            {
                if(foundChar)
                    break;
            } else
            {
                if(!foundChar)
                    initPos = max;
                length++;
                foundChar = true;
            }

        word[0] = initPos;
        word[1] = length != 0 ? length : length + 1;
        return word;
    }

    private boolean checkWord(String word)
    {
        for(int i = 0; i < KEYWORDS.length; i++)
            if(word.equalsIgnoreCase(KEYWORDS[i]))
                return true;

        return false;
    }

    protected void addStylesToDocument(StyledDocument sdoc) {
        javax.swing.text.Style def = StyleContext.getDefaultStyleContext().getStyle("default");
        javax.swing.text.Style regular = sdoc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");
        javax.swing.text.Style s = sdoc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);
        s = sdoc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);
        s = sdoc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);
        s = sdoc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
        s = sdoc.addStyle("blue", regular);
        StyleConstants.setForeground(s, Color.blue);
        s = sdoc.addStyle("red", regular);
        StyleConstants.setForeground(s, Color.red);
    }



}