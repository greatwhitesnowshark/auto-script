package python;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Sharky
 */
public class FunctionAppend {

    public static final Map<String, String> mFunctionArgumentAppend = new LinkedHashMap<>(); //appends arguments to funcs - "myFunc(arg1)->myFunc(arg1, true)"

    public static final String ConvertFunctionAppend(String sScriptLine) {
        for (String sLine : mFunctionArgumentAppend.keySet()) {
            if (sScriptLine.contains(sLine)) {
                if (sScriptLine.contains(")") && !sScriptLine.contains(mFunctionArgumentAppend.get(sLine))) {
                    sScriptLine = sScriptLine.substring(0, sScriptLine.lastIndexOf(")")) + mFunctionArgumentAppend.get(sLine) + ")";
                    break;
                }
            }
        }
        return sScriptLine;
    }

    public static final void InitFunctionArgumentAppendMap() {
        //These methods get passed before the FuncReplaceConvert so using swordie-func names
        mFunctionArgumentAppend.put("addAP",", true");
        mFunctionArgumentAppend.put("addLevel",", true");
        mFunctionArgumentAppend.put("addMaxHP",", true");
        mFunctionArgumentAppend.put("addMaxMP",", true");
        mFunctionArgumentAppend.put("addSP",", true");
        mFunctionArgumentAppend.put("addSp",", true");
        mFunctionArgumentAppend.put("chr.setSpToCurrentJob",", true");
        mFunctionArgumentAppend.put("giveExp",", true");
        mFunctionArgumentAppend.put("giveMesos",", true");
        mFunctionArgumentAppend.put("sendPrev",", true");
    }
}
