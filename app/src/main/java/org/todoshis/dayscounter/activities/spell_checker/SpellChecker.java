package org.todoshis.dayscounter.activities.spell_checker;

public class SpellChecker {
    public static String checkEndingDependingOnQuantity(long value, String firstWord, String secondWord, String thirdWord) {
        boolean exceptions = !(value % 100 == 11 || value % 100 == 12 || value % 100 == 13 || value % 100 == 14);
        if (value % 10 == 1 && exceptions) {
            return firstWord;
        } else if ((value % 10 == 2 || value % 10 == 3 || value % 10 == 4) && exceptions) {
            return secondWord;
        } else {
            return thirdWord;
        }
    }
}
