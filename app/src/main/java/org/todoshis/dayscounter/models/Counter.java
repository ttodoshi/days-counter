package org.todoshis.dayscounter.models;
public class Counter {

    private String startDate;
    private int daysShowMode;
    private String phrase;

    public Counter(String startDate, int daysShowMode, String phrase) {
        this.startDate = startDate;
        this.daysShowMode = daysShowMode;
        this.phrase = phrase;
    }

    public String getDate() {
        return startDate;
    }

    public void setDate(String newStartDate) {
        this.startDate = newStartDate;
    }

    // 1 - only days
    // 0 - years, weeks, days
    public int getDaysShowMode() {
        return daysShowMode;
    }

    public void setDaysShowMode() {
        this.daysShowMode = (daysShowMode == 1) ? 0 : 1;
    }

    public String getPhrase() {
        return phrase;
    }

    public boolean setPhrase(String newPhrase) {
        if (newPhrase.trim().length() == 0 || newPhrase.length() > 100) {
            return false;
        } else {
            this.phrase = newPhrase;
            return true;
        }
    }
}
