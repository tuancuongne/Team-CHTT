package com.example.cinemas;

import java.util.List;

public class Cinema {
    private String area;
    private String name;
    private String subtitle;
    private List<String> showtimes;
    private boolean expanded;

    public Cinema(String area, String name, String subtitle, List<String> showtimes) {
        this.area = area;
        this.name = name;
        this.subtitle = subtitle;
        this.showtimes = showtimes;
        this.expanded = false; // Mặc định không mở rộng
    }

    public String getArea() {
        return area;
    }

    public String getName() {
        return name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<String> getShowtimes() {
        return showtimes;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}