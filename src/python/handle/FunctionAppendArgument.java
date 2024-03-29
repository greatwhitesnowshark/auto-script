package python.handle;

import util.StringMatch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Sharky
 */
public class FunctionAppendArgument extends AbstractHandler {

    public static FunctionAppendArgument pInstance = new FunctionAppendArgument();
    public static Map<String, String> mFunctionArgumentAppend = new LinkedHashMap<>(); //appends arguments to funcs - "myFunc(arg1)->myFunc(arg1, true)"

    @Override
    public String Convert(String sScriptLine) {
        for (String sKey : mFunctionArgumentAppend.keySet()) {
            if (sScriptLine.contains(sKey)) {
                if (sScriptLine.contains(")") && !sScriptLine.contains(mFunctionArgumentAppend.get(sKey))) {
                    sScriptLine = sScriptLine.substring(0, sScriptLine.lastIndexOf(")")) + mFunctionArgumentAppend.get(sKey) + ")";
                    break;
                }
            }
        }
        return sScriptLine;
    }


    static {
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
        //mFunctionArgumentAppend.put("sendPrev",", true");
        //mFunctionArgumentAppend.put("sendNext", ", true");

    }
}
