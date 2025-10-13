package com.mentorship;

public class Book {
  // Fields (attributes)
  private String title;
  private String author;
  private int yearPublished;

  /**
   * Create a Book with the specified title, author, and publication year.
   *
   * @param title the book's title
   * @param author the author's name
   * @param yearPublished the year the book was published
   */
  public Book(String title, String author, int yearPublished) {
    this.title = title;
    this.author = author;
    this.yearPublished = yearPublished;
  }

  /**
   * Gets the book's title.
   *
   * @return the book's title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Retrieves the book's author's name.
   *
   * @return the name of the book's author
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Gets the publication year of the book.
   *
   * @return the year the book was published
   */
  public int getYearPublished() {
    return yearPublished;
  }

  /**
   * Prints the book's title, author, and publication year to standard output in a labeled format.
   */
  public void printDetails() {
    System.out.println("Title: " + title);
    System.out.println("Author: " + author);
    System.out.println("Published: " + yearPublished);
  }

  /**
   * Demonstrates creating a sample Book and printing its details.
   *
   * Creates a Book instance with title "Clean Code", author "Robert C. Martin",
   * and year 2008, then invokes printDetails() to write its information to
   * standard output.
   */
  public static void main(String[] args) {
    Book myBook = new Book("Clean Code", "Robert C. Martin", 2008);
    myBook.printDetails();
  }
}
