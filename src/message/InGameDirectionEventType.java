/*
 * To change this license header), choose License Headers in Project Properties.
 * To change this template file), choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.lang.reflect.Field;

/**
 *
 * @author Sharky
 */
public class InGameDirectionEventType { 
    
    public static final int ForcedAction = 0,
                        Delay = 1,
                        EffectPlay = 2,
                        ForcedInput = 3,
                        PatternInputRequest = 4,
                        CameraMove = 5,
                        CameraOnCharacter = 6,
                        CameraZoom = 7,
                        CameraReleaseFromUserPoint = 8,
                        VansheeMode = 9,
                        FaceOff = 10,
                        Monologue = 11,
                        MonologueScroll = 12,
                        AvatarLookSet = 13,
                        RemoveAdditionalEffect = 14,
                        // Unknown
                        ForcedMove = 16,
                        ForcedFlip = 17,
                        // Unknown
                        InputUI = 19,
                        CloseUI = 20;
    
    public static String GetName(int nVal) {
        try {
            for (Field pField : InGameDirectionEventType.class.getDeclaredFields()) {
                if (pField.isAccessible()) {
                    if (pField.getInt(null) == nVal) {
                        return pField.getName();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
