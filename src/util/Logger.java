/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Five
 */
public class Logger {

    // todo: write these errors to file
    public static void LogAdmin(String sText) {
        System.err.println("[ADMIN] " + sText);
    }

    public static void LogAdmin(String sText, Object... args) {
        System.err.println(String.format("[ADMIN] " + sText, args));
    }

    public static void LogError(String sText) {
        System.err.println("[ERROR] " + sText);
    }

    public static void LogError(String sText, Object... args) {
        System.err.println(String.format("[ERROR] " + sText, args));
    }

    public static void LogReport(String sText) {
        System.err.println("[REPORT] " + sText);
    }

    public static void LogReport(String sText, Object... args) {
        System.err.println(String.format("[REPORT] " + sText, args));
    }

    public static void SendMacroLog(String sText) {
        System.err.println(sText);
    }

    public static void SendMacroLog(String sText, Object... args) {
        System.err.println(String.format(sText, args));
    }

    public static void Print(String sText) {
        System.err.print(sText);
    }

    public static void Print(String sText, Object... args) {
        System.err.print(String.format(sText, args));
    }

    public static void Println(String sText) {
        System.err.println(sText);
    }

    public static void Println(String sText, Object... args) {
        System.err.println(String.format(sText, args));
    }
}
