package com.example.cinemas;

public class Transaction {
    private String bookingCode;
    private String cinemaName;
    private String movieTitle;
    private String showtime;
    private String seats;
    private String food;
    private String totalPrice;

    public Transaction() {} // Constructor mặc định cho Firebase

    public Transaction(String bookingCode, String cinemaName, String movieTitle, String showtime, String seats, String food, String totalPrice) {
        this.bookingCode = bookingCode;
        this.cinemaName = cinemaName;
        this.movieTitle = movieTitle;
        this.showtime = showtime;
        this.seats = seats;
        this.food = food;
        this.totalPrice = totalPrice;
    }

    // Getter và Setter
    public String getBookingCode() { return bookingCode; }
    public String getCinemaName() { return cinemaName; }
    public String getMovieTitle() { return movieTitle; }
    public String getShowtime() { return showtime; }
    public String getSeats() { return seats; }
    public String getFood() { return food; }
    public String getTotalPrice() { return totalPrice; }
}