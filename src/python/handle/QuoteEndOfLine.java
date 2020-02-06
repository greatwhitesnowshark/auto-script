package python.handle;

/**
 * @author Sharky
 */
public class QuoteEndOfLine extends AbstractHandler {

    public static QuoteEndOfLine pInstance = new QuoteEndOfLine();
    
    @Override
    public String Convert(String sScriptLine) {
        if (sScriptLine.contains(" = ")) {
            int nQtCount = 0;
            for (char c : sScriptLine.toCharArray()) {
                if (c == '"') {
                    nQtCount++;
                }
            }
            if (nQtCount == 1 || nQtCount % 2 != 0) {
                if (sScriptLine.contains(";")) {
                    sScriptLine = sScriptLine.replace(";", "\";");
                }
            }
        }
        return sScriptLine;
    }
}
