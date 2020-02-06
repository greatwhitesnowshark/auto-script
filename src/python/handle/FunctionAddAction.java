package python.handle;

import util.StringMatch;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sharky
 */
public class FunctionAddAction extends AbstractHandler {

    public static FunctionAddAction pInstance = new FunctionAddAction();
    public static Map<String, String> mFunctionFollowInsert = new HashMap<>(); //adds lines after specific patterns are read

    @Override
    public String Convert(String sScriptLine) {
        for (String sKey : mFunctionFollowInsert.keySet()) {
            if (StringMatch.Match(sScriptLine, true, true, true, sKey)) {
                return mFunctionFollowInsert.get(sKey);
            }
        }
        return "";
    }


    static {
        //Adds necessary actions as subsequent lines on the discovery of a given pattern-key

        mFunctionFollowInsert.put("InGameDirectionEvent.Delay", "self.Wait();");

    }
}
