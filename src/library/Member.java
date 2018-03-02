package library;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Maxwell Wang 2017-18 FBLA- Desktop Application Programming
 * 
 * Member Class - keeps track of a student or teacher and associated details
 * 
 */

public class Member implements Serializable, Comparable<Member> {

	private static final long serialVersionUID = 1L;
	private boolean teacher;
	private String firstName;
	private String middleName;
	private String lastName;
	private String notes;
	private Date dayAdded;
	private ArrayList<Book> books;

	public boolean isTeacher() {
		return teacher;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getStatus() {
		if (teacher) {
			return "Teacher";
		} else {
			return "Student";
		}
	}

	public String getNotes() {
		return notes;
	}

	// Day the member was added.
	public Date dayAdded() {
		return dayAdded;
	}

	// Returns array of books that the member has checked out.
	public ArrayList<Book> books() {
		return books;
	}

	// Amount of books the member has checked out
	public int getBookCount() {
		return books.size();
	}

	// Returns maximum amount of books allowed.
	public int getBookLimit() {
		if (teacher) {
			return TSHSLibrary.teacherBooksAllowed;
		} else {
			return TSHSLibrary.studentBooksAllowed;
		}
	}

	// Returns maximum number of days a book can be checked out for before overdue.
	public int getTimeLimit() {
		if (teacher) {
			return TSHSLibrary.teacherDaysAllowed;
		} else {
			return TSHSLibrary.studentDaysAllowed;
		}
	}

	// Used for binarySort - defines a member with just a dateAdded field
	public Member(Date dayAdded) {
		this.dayAdded = dayAdded;
	}

	public Member(String firstName, String middleName, String lastName, boolean teacher, String notes) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.teacher = teacher;
		this.notes = notes;
		books = new ArrayList<Book>();
		dayAdded = Calendar.getInstance().getTime();
	}

	// Constructor for editing, keeps dayAdded of original although it has been changed slightly, also retains booklist.
	public Member(String firstName, String middleName, String lastName, boolean teacher, String notes, Date dayAdded,
			ArrayList<Book> b) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.teacher = teacher;
		this.notes = notes;
		books = b;
		this.dayAdded = dayAdded;
	}

	// Checks out book b. TSHSLibrary makes sure that the book has not already been checked out.
	public void checkOut(Book b) {
		books.add(b);
		b.checkedOut(this);
	}

	// Returns book b
	public void makeReturn(Book b) {
		books.remove(b);
		b.returned();
	}

	// For testing and general purposes, returns String representation of a member
	public String toString() {
		String member = "";
		if (teacher) {
			member += "Teacher: ";
		} else {
			member += "Student: ";
		}
		return member + getName();
	}

	// Returns the natural format of the name
	public String getName() {
		if (middleName.length() > 0 && firstName.length() > 0) {
			return lastName + ", " + firstName + " " + middleName.charAt(0) + ".";
		} else if (firstName.length() > 0) {
			return lastName + ", " + firstName;
		} else {
			return lastName;
		}
	}

	@Override
	public int compareTo(Member arg0) {
		return this.dayAdded.compareTo(arg0.dayAdded);
	}

	// Testing of code
	public static void main(String[] args) {

	}
}
