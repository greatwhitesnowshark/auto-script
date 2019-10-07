package python;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sharky
 */
public class ForceComment {

    public static final List<String> aForceCommentLine = new LinkedList<>(); //ignores a read line if it contains specific text

    public static final String ConvertForceComment(String sScriptLine) {
        for (String sKeyword : aForceCommentLine) {
            if (sScriptLine.contains(sKeyword)) {
                return ("//" + sScriptLine);
            }
        }
        return sScriptLine;
    }

    public static final void InitForceCommentLineList() {
        aForceCommentLine.add(".setInstanceTime");
        aForceCommentLine.add("field.setProperty");
        aForceCommentLine.add("waitForMobDeath");
        aForceCommentLine.add("field.hasProperty");
        aForceCommentLine.add("field.getProperty");
        aForceCommentLine.add(".getAvatarData().getCharacterStat().setSubJob");
    }
}
