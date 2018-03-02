package library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Maxwell Wang 2017-18 FBLA- Desktop Application Programming
 * 
 * Book Class - keeps track of a book and associated details
 * 
 */

public class Book implements Serializable, Comparable<Book> {

	private static final long serialVersionUID = 1L;

	private String title;
	private String authorFirstName = "";
	private String authorMiddleName = "";
	private String authorLastName;
	private String bookType;
	private long ISBN = 0;
	private String notes;

	private long id;
	private boolean checkedOut;
	private Member currentOwner;
	private int timesCheckedOut;
	private Date dayAdded;
	private Date checkoutDate = null;

	public String getTitle() {
		return title;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public String getAuthorMiddleName() {
		return authorMiddleName;
	}

	public String getAuthorLastName() {
		return authorLastName;
	}

	// Book, Periodical, Reference, or Video
	public String getBookType() {
		return bookType;
	}

	public long getId() {
		return id;
	}

	// The day the book was added into the database
	public Date getDayAdded() {
		return dayAdded;
	}

	public long getISBN() {
		return ISBN;
	}

	public String getNotes() {
		return notes;
	}

	// Times a book has been checked out (to indicate popularity)
	public int timesCheckedOut() {
		return timesCheckedOut;
	}

	// Sets the time that the book was most recently checked out (for due date)
	public void setCheckoutDate(Date d) {
		checkoutDate = d;
	}

	// Returns the day that the book was last checked out "checked out on"
	public Date getCheckoutDate() {
		return checkoutDate;
	}

	// Is it checked out? For the book table
	public String status() {
		if (checkedOut) {
			return "Checked Out";
		} else {
			return "Available";
		}
	}

	// Whether or not the book is overdue, by comparing the current date and the due date.
	public boolean overdue() {
		return (Calendar.getInstance().getTime().after(getDueDate()));
	}

	// Gets the due date by adding the days allowed to day the book was added.
	public Date getDueDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(checkoutDate);
		if (currentOwner.isTeacher()) {
			cal.add(Calendar.DATE, TSHSLibrary.teacherDaysAllowed);
		} else {
			cal.add(Calendar.DATE, TSHSLibrary.studentDaysAllowed);
		}
		return cal.getTime();
	}

	// Return days between the current date and the due date
	// If negative - not overdue, that's the days until the due date
	// If positive, it is that many days overdue
	public int daysOverdue() {
		long change = Calendar.getInstance().getTime().getTime() - getDueDate().getTime();
		return (int) TimeUnit.DAYS.convert(change, TimeUnit.MILLISECONDS);

	}

	// Returns the fines
	public double getFines() {
		if (overdue()) {
			int days = daysOverdue();
			if (bookType.equals("Book")) {
				return TSHSLibrary.bookDailyFine * days;
			} else if (bookType.equals("Periodical")) {
				return TSHSLibrary.periodicalDailyFine * days;
			} else if (bookType.equals("Reference")) {
				return TSHSLibrary.referenceDailyFine * days;
			} else {
				return TSHSLibrary.videoDailyFine * days;
			}
		} else {
			return 0;
		}
	}

	// Constructor used for sorting purposes
	public Book(long id) {
		this.id = id;
	}

	// Constructor for a new book
	public Book(String title, String authorFirstName, String authorMiddleName, String authorLastName, String bookType,
			String category, ArrayList<Book> books, String ISBN, String notes) {
		if (ISBN.length() > 0) {
			this.ISBN = Long.parseLong(ISBN);
		}
		// For title and names, the first character is automatically changed to upper-case
		Character.toUpperCase(title.charAt(0));
		this.title = title;
		if (authorFirstName.length() > 0 && authorFirstName != null) {
			Character.toUpperCase(authorFirstName.charAt(0));
			this.authorFirstName = authorFirstName;
		}
		if (authorMiddleName.length() > 0 && authorMiddleName != null) {
			Character.toUpperCase(authorMiddleName.charAt(0));
			this.authorMiddleName = authorMiddleName;
		}
		Character.toUpperCase(authorLastName.charAt(0));
		this.authorLastName = authorLastName;
		this.bookType = bookType;
		this.notes = notes;
		checkedOut = false;
		timesCheckedOut = 0;
		dayAdded = Calendar.getInstance().getTime();

		// Assign the ID
		id = assignId(category, books);
	}

	// Constructor for a new book, overloaded to allow a predetermined ID (used in editing, because edited books retain the same ID)
	public Book(String title, String authorFirstName, String authorMiddleName, String authorLastName, String bookType,
			String category, ArrayList<Book> books, String ISBN, String notes, long id) {
		if (ISBN.length() > 0) {
			this.ISBN = Long.parseLong(ISBN);
		}
		// For title and names, the first character is automatically changed to upper-case
		Character.toUpperCase(title.charAt(0));
		this.title = title;
		if (authorFirstName.length() > 0 && authorFirstName != null) {
			Character.toUpperCase(authorFirstName.charAt(0));
			this.authorFirstName = authorFirstName;
		}
		if (authorMiddleName.length() > 0 && authorMiddleName != null) {
			Character.toUpperCase(authorMiddleName.charAt(0));
			this.authorMiddleName = authorMiddleName;
		}
		Character.toUpperCase(authorLastName.charAt(0));
		this.authorLastName = authorLastName;
		this.bookType = bookType;
		this.notes = notes;
		checkedOut = false;
		dayAdded = Calendar.getInstance().getTime();

		// Assign the ID
		this.id = id;
	}

	/*
	 * Generates the unique ID number for each book. The ID is created as
	 * follows:
	 * 
	 * 1 1 3 2 3 2 5 2 0 2 
	 * - - - - - - - - - - 
	 * | |---| |-| |-| |-| 
	 * a   b    c   d   e
	 * 
	 * a: null (1) 
	 * b: 3-digit Dewey Call Number, without decimal s
	 * c: 2-digit ascii of First Name 
	 * d: 2-digit ascii of Last Name
	 * 		c is first character of author's last name
	 * 		d is first character of title 
	 * 		will not allow special symbols in author's name, such as {, }, |, ~
	 * 		if characters with 3-digit long ascii is inputed, the first digit will be cut
	 * e: Copy identifier (allows for up to 100 copies of any given book)
	 * 
	 */
	// ASSIGN_ID
	private long assignId(String category, ArrayList<Book> books) {
		String id = "1";
		// Adds dewey call number to ID
		System.out.println(category);
		if (!category.equals("null")) {
			id += category.substring(0, 3);
		} else {
			id += "000";
		}

		// Generates Initials
		int firstInitial = authorLastName.charAt(0);

		int secondInitial = title.charAt(0);
		// Adds Initials to ID
		id += String.format("%02d", Character.getNumericValue(firstInitial) % 100);
		id += String.format("%02d", Character.getNumericValue(secondInitial) % 100);

		// Finds and adds copy identifier
		Collections.sort(books);
		// Searches all books to find the first instance of the same index (another book that is identical to this one except in its copy identifier)
		// Note: Some books that are not identical may be listed as the same copy, if author initials and category are the same.
		int startLocation = Collections.binarySearch(books, new Book(Long.parseLong(id) * 1000));
		System.out.println(startLocation + " startLocation");
		int copies = 0;
		if (startLocation >= 0) {
			copies++;
			while (startLocation < books.size() - 1 && books.get(startLocation).getId() / 1000 == Long.parseLong(id)) {
				//System.out.println(books.get(startLocation).getId());
				startLocation++;
				copies++;
			}
		}
		System.out.println("This is copy number " + copies);
		id += String.format("%03d", copies);

		System.out.println("ID Generated: " + id);
		//	Catch all - if id is duplicate, increases by one
		long bookID = Long.parseLong(id);
		while (Collections.binarySearch(books, new Book(bookID)) >= 0) {
			bookID++;
		}
		return bookID;
	}

	// checks the book out to m
	public void checkedOut(Member m) {
		checkedOut = true;
		currentOwner = m;
		timesCheckedOut++;
	}

	// returns the book;
	public void returned() {
		checkedOut = false;
		currentOwner = null;
	}

	public String toString() {
		return "\"" + title + "\" by " + authorFirstName + " " + authorMiddleName + " " + authorLastName + ", " + id;
	}

	public String authorName() {
		if (authorMiddleName.length() > 0 && authorFirstName.length() > 0) {
			return authorLastName + ", " + authorFirstName + " " + authorMiddleName.charAt(0) + ".";
		} else if (authorFirstName.length() > 0) {
			return authorLastName + ", " + authorFirstName;
		} else {
			return authorLastName;
		}
	}

	@Override
	public int compareTo(Book o) {
		return Long.compare(this.id, o.id);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
