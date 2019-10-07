package python;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sharky
 */
public class FunctionInsert {

    public static final Map<String, String> mFunctionFollowInsert = new HashMap<>(); //adds lines after specific patterns are read

    public static final String GetFunctionFollowInsert(String sScriptLine) {
        for (String sKey : mFunctionFollowInsert.keySet()) {
            if (sScriptLine.contains(sKey)) {
                return mFunctionFollowInsert.get(sKey);
            }
        }
        return "";
    }

    public static final void InitFunctionFollowInsertMap() {
        //Adds necessary actions as subsequent lines on the discovery of a given pattern-key
        mFunctionFollowInsert.put("InGameDirectionEvent.Delay", "self.Wait();");
    }
}
