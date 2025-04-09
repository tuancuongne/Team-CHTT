package com.example.cinemas;

public class DateItem {
    private String displayText; // Văn bản hiển thị (ví dụ: "H.nay\n03")
    private String date; // Định dạng ngày đầy đủ (ví dụ: "2025-04-05")

    public DateItem(String displayText, String date) {
        this.displayText = displayText;
        this.date = date;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getDate() {
        return date;
    }
}