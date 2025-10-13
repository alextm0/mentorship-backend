package com.mentorship;

public class Book {
  // Fields (attributes)
  private String title;
  private String author;
  private int yearPublished;

  // Constructor
  public Book(String title, String author, int yearPublished) {
    this.title = title;
    this.author = author;
    this.yearPublished = yearPublished;
  }

  // Getter methods
  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public int getYearPublished() {
    return yearPublished;
  }

  // Method to display book details
  public void printDetails() {
    System.out.println("Title: " + title);
    System.out.println("Author: " + author);
    System.out.println("Published: " + yearPublished);
  }

  // Main method for testing
  public static void main(String[] args) {
    Book myBook = new Book("Clean Code", "Robert C. Martin", 2008);
    myBook.printDetails();
  }
}

