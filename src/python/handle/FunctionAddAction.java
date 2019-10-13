package python.handle;

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
            if (sScriptLine.contains(sKey)) {
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
