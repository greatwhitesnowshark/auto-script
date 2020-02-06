package python.handle;

import base.util.Logger;
import base.util.Pointer;
import util.StringMatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Sharky
 */
public class IncorrectTranslations extends AbstractHandler {

    public static IncorrectTranslations pInstance = new IncorrectTranslations();
    public static LinkedHashMap<String, PredicateValueSet> mTranslations = new LinkedHashMap<>();
    public static Pointer<Integer> nCount = new Pointer<>(1);
    private static volatile boolean bLog;
    private static volatile String sPreviousLine;
    private List<String> lLog = new ArrayList<>();

    @Override
    public String Convert(String sScriptLine) {
        for (String sKey : mTranslations.keySet()) {
            if (StringMatch.Match(sScriptLine, true, true, false, sKey)) {
                PredicateValueSet<String> pPredicateValue = mTranslations.get(sKey);
                if (pPredicateValue != null) {
                    if (pPredicateValue.pPredicate != null) {
                        if (pPredicateValue.pPredicate.test(sScriptLine)) {
                            sScriptLine = StringMatch.ReplaceAll(sScriptLine, sKey, pPredicateValue.sValue, true, true, false);
                            nCount.setElement(nCount.element() + 1);
                        }
                    }
                }
            }
        }
        return sScriptLine;
    }

    public void Log(String sFileName, String sScriptLine) {
        if (bLog && !lLog.contains(sFileName) && !sScriptLine.contains("{")) {
            String sSimpleName = sFileName.substring(0, sFileName.lastIndexOf('\\'));
            if (!sPreviousLine.isBlank()) {
                //Logger.LogReport("Incorrect Translation: ");
                //Logger.LogReport("\tFile Name:  %s", sFileName.split(Pattern.quote("\\"))[sFileName.split(Pattern.quote("\\")).length - 1]);
                //Logger.LogReport("\tPackage:    %s", sSimpleName);
                //Logger.LogReport("\tPrev Line:  \'%s\'", sPreviousLine);
                //Logger.LogReport("\tLine:       \'%s\'", sScriptLine);
                lLog.add(sFileName);
                bLog = false;
            } else {
                //Logger.LogError("IncorrectTranslations error: `sPreviousLine` was blank for file: ");
                //Logger.LogError("\t\t%s", sFileName.split(Pattern.quote("\\"))[sFileName.split(Pattern.quote("\\")).length - 1]);
                //Logger.LogError("\t\t%s", sSimpleName);
            }
        }
    }


    public static class PredicateValueSet<String> {

        public Predicate<String> pPredicate;
        public String sValue;

        PredicateValueSet(Predicate<String> pPredicate, String sValue) {
            this.pPredicate = pPredicate;
            this.sValue = sValue;
        }
    }


    static {
        mTranslations.put("self.Say", new PredicateValueSet<>(((sText) -> sText.split("#L\\d*#").length > 1), "self.AskMenu"));
        mTranslations.put("self.AskMenu", new PredicateValueSet<>(((sText) -> {
            if (sText.split("#L\\d*#").length > 1) {
                if (sText.split("= self").length <= 1) {
                    bLog = true;
                    sPreviousLine = sText;
                }
            }
            return bLog;
        }), "nRet = self.AskMenu"));
    }
}
