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
public class QuestResultType {
    
    public static final int StartQuestTimer = 7,
                        EndQuestTimer = 8,
                        StartTimeKeepQuestTimer = 9,
                        EndTimeKeepQuestTimer = 10,
                        Success = 11,
                        FailedUnknown = 12,
                        FailedInventory = 13,
                        FailedMeso = 14,
                        FailedOverflowMeso = 15,
                        FailedPet = 16,
                        FailedEquipped = 17,
                        FailedOnlyItem = 18,
                        FailedTimeOver = 19,
                        FailedState = 20,
                        FailedQuest = 21,
                        FailedBlock = 22,
                        FailedUniverse = 23,
                        ResetQuestTimer = 24;
}
