/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

/**
 *
 * @author Sharky
 */
public class QuestMessageType {
    
    public static final int LostItem = 0,
                            AcceptQuest = 1,
                            CompleteQuest = 2,
                            ResignQuest = 3,
                            OpeningScript = 4,
                            CompleteScript = 5,
                            LaterStep = 6;
    public static String[] sType = new String[] { "LostItem", "AcceptQuest", "CompleteQuest", "ResignQuest", "OpeningScript", "CompleteScript", "LaterStep" };
}
