/**
 * Harun Slahaldin Omar 40250981
 * COMP-249
 * Assignment #3
 * 29/03/2023
 */

// -----------------------------------------------------
// Assignment (3)
// Question: (-)
// Written by: (Harun Slahaldin Omar 40250981)
// -----------------------------------------------------

// This assignment entails several steps which end up organizing a catalogue of book records contained within 16 text files.
// It is mainly a demonstration of file input/output, as well as exception handling. Most of the code is contained within
// three methods: do_part1, do_part2, and do_part3. The first part primes the original record files and sorts syntactically
// valid (that is, correctly formatted) records according to their genre, outputting text files to this effect. The next part
// takes these same text files and puts them through another filter, this time checking for semantic errors (invalid values -
// normally unknown genre would've gone here, but since part one sorts by genre, it needs to check for these errors beforehand)
// and writing arrays of valid book objects (formatted through a newly-introduced Book class) to binary files this time through
// serialization. Finally, part three deserializes these arrays and allows the user to view book records through a pretty
// straightforward algorithm.

import java.io.*;
import java.util.Scanner;

// 8 exception classes

class TooManyFieldsException extends Exception{
}

class TooFewFieldsException extends Exception{
}

class MissingFieldException extends Exception{
}

class UnknownGenreException extends Exception{
}

class BadIsbn10Exception extends Exception{
}

class BadIsbn13Exception extends Exception{
}

class BadPriceException extends Exception{
}

class BadYearException extends Exception{
}

class Book implements Serializable{ // book class, straightforward 6 attributes for the 6 fields, and default methods
    String title;
    String authors;
    double price;
    String isbn;
    String genre;
    int year;

    public Book(String title, String authors, double price, String isbn, String genre, int year){
        this.title = title;
        this.authors = authors;
        this.price = price;
        this.isbn = isbn;
        this.genre = genre;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String toString(){
        return "Title: " + title + " | Author(s): " + authors + " | Price: $" + price + " | ISBN: " + isbn + " | Genre: " + genre + " | Year: " + year;
    }
}

public class Main {
    public static void do_part1() throws IOException { // part 1
    BufferedReader reader = new BufferedReader(new FileReader("part1_input_file_names.txt"));
    String[] input = new String[Integer.parseInt(reader.readLine())]; // declaring array with size given in first file ^
    for(int i = 0; i < input.length; i++){ // filling above array with names of files
        input[i] = reader.readLine();
    }
    reader.close();
    String[] genre = {"CCB", "HCB", "MTV", "MRB", "NEB", "OTR", "SSM", "TPA"};
    String[] outputNames = {"Cartoons_Comics_Books.csv.txt", "Hobbies_Collectibles_Books.csv.txt", "Movies_TV_Books.csv.txt",
            "Music_Radio_Books.csv.txt", "Nostalgia_Eclectic_Books.csv.txt", "Old_Time_Radio_Books.csv.txt",
            "Sports_Sports_Memorabilia.csv.txt", "Trains_Planes_Automobiles.csv.txt", "syntax_error_file.txt"}; // hard-coded output names
    FileWriter[] output = new FileWriter[outputNames.length];
    for(int i = 0; i < output.length; i++){ // filling array of filewriters
        output[i] = new FileWriter(outputNames[i]);
    }
    String[] fields = {"title", "authors", "price", "isbn", "genre", "year"};
    BufferedWriter writer = new BufferedWriter(output[0]);
    writer.flush();
    for(int i = 0; i < input.length; i++){ // cycles through each file
        reader = new BufferedReader(new FileReader(input[i]));
        String record = "placeholder";
        while(record != null){ // ensures reading of whole file
            record = reader.readLine();
            if(record == null)
                break;
            String[] recordFields;
            if(record.charAt(0) == '\"') { // splitting record if title has quotes
                String title = record.substring(0, record.indexOf("\"", 1) + 1);
                String[] xFields = record.substring(title.length() + 1).split(",");
                recordFields = new String[xFields.length + 1];
                recordFields[0] = title;
                for(int k = 0; k < recordFields.length - 1; k++){
                    recordFields[k + 1] = xFields[k];
                }
            }
            else{ // way simpler if title does not have quotes
                recordFields = record.split(",");
            }
            String missingField = "";
            try {
                if (recordFields.length > 6) { // very simple exception handling, did not want to break my head over this
                    throw new TooManyFieldsException();
                }
                if (recordFields.length < 6) {
                    throw new TooFewFieldsException();
                }
                for (int k = 0; k < 6; k++) {
                    if (recordFields[k].contentEquals("")) {
                        missingField = fields[k];
                        throw new MissingFieldException();
                    }
                }
                boolean genreKnown = false;
                for (int k = 0; k < genre.length; k++) {
                    if (recordFields[4].contentEquals(genre[k])) {
                        writer = new BufferedWriter(output[k]); // making sure it outputs to right file if genre is known
                        genreKnown = true;
                    }
                }
                if(!genreKnown)
                    throw new UnknownGenreException();
                writer.write(record); // writes record only if try block finishes execution
            }
            catch(TooManyFieldsException | TooFewFieldsException | MissingFieldException | UnknownGenreException e){
                writer = new BufferedWriter(output[8]); // outputting to syntax error file
                writer.write("syntax error in file: " + input[i]);
                writer.newLine(); // formatting errors
                writer.write("====================");
                writer.newLine();
                writer.write("Error: ");
                if(e instanceof TooManyFieldsException)
                    writer.write("too many fields");
                if(e instanceof TooFewFieldsException)
                    writer.write("too few fields");
                if(e instanceof MissingFieldException)
                    writer.write("missing " + missingField);
                if(e instanceof UnknownGenreException)
                    writer.write("invalid genre");
                writer.newLine();
                writer.write("Record: " + record);
                writer.newLine();
                writer.newLine();
                writer.flush();
                continue;
            }
            writer.newLine();
            writer.flush();
        }
        reader.close();
    }
    }

    public static void do_part2() throws IOException { // part 2, mainly same concept as part 1, except serializing this time
        String[] outputNames = {"Cartoons_Comics_Books.csv.ser", "Hobbies_Collectibles_Books.csv.ser", "Movies_TV_Books.csv.ser",
                "Music_Radio_Books.csv.ser", "Nostalgia_Eclectic_Books.csv.ser", "Old_Time_Radio_Books.csv.ser",
                "Sports_Sports_Memorabilia.csv.ser", "Trains_Planes_Automobiles.csv.ser"}; // hard-coded binary files
        String[] inputNames = {"Cartoons_Comics_Books.csv.txt", "Hobbies_Collectibles_Books.csv.txt", "Movies_TV_Books.csv.txt",
                "Music_Radio_Books.csv.txt", "Nostalgia_Eclectic_Books.csv.txt", "Old_Time_Radio_Books.csv.txt",
                "Sports_Sports_Memorabilia.csv.txt", "Trains_Planes_Automobiles.csv.txt"};
        FileReader[] input = new FileReader[inputNames.length];
        for(int i = 0; i < input.length; i++){
            input[i] = new FileReader(inputNames[i]);
        }
        FileOutputStream[] output = new FileOutputStream[outputNames.length]; // fileoutputstream for serialization
        for(int i = 0; i < input.length; i++){
            output[i] = new FileOutputStream(outputNames[i]);
        }
        BufferedReader reader = new BufferedReader(new FileReader("part1_input_file_names.txt"));
        BufferedWriter exceptionWriter = new BufferedWriter(new FileWriter("semantic_error_file.txt"));
        ObjectOutputStream serWriter;
        reader.close();
        for(int i = 0; i < input.length; i++) { // same idea as part 1
            Book[] books = new Book[0];
            reader = new BufferedReader(input[i]);
            serWriter = new ObjectOutputStream(output[i]);
            String record = "placeholder";
            int recordCount = 0;
            while (record != null) {
                record = reader.readLine();
                if (record == null)
                    break;
                String[] recordFields;
                if (record.charAt(0) == '\"') {
                    String title = record.substring(0, record.indexOf("\"", 1) + 1);
                    String[] xFields = record.substring(title.length() + 1).split(",");
                    recordFields = new String[xFields.length + 1];
                    recordFields[0] = title;
                    for (int k = 0; k < 5; k++) {
                        recordFields[k + 1] = xFields[k];
                    }
                } else
                    recordFields = record.split(",");
                int[] numbers = new int[recordFields[3].length()];
                int valid = 0;
                try { // this time checking for semantic errors
                    for (int k = 0; k < numbers.length; k++) {
                        numbers[k] = Integer.parseInt(recordFields[3].substring(k, k + 1));
                    }
                    if (numbers.length == 10) {
                        int count = 10;
                        for (int j = 0; j < numbers.length; j++) {
                            valid += count * numbers[j];
                            count--;
                        } // checking isbn's through their arbitrary algorithms
                        if (!(valid % 11 == 0))
                            throw new BadIsbn10Exception();
                    }
                    if (numbers.length == 13) {
                        for (int j = 0; j < numbers.length; j++) {
                            if (j % 2 == 0)
                                valid += numbers[j];
                            else
                                valid += 3 * numbers[j];
                        }
                        if (!(valid % 10 == 0))
                            throw new BadIsbn13Exception();
                    }
                    if (Double.parseDouble(recordFields[2]) < 0) // checking for negative price
                        throw new BadPriceException();
                    if (Integer.parseInt(recordFields[5]) < 1995 || Integer.parseInt(recordFields[5]) > 2010)
                        throw new BadYearException(); // checking for valid year
                    Book[] temp;
                    if (recordCount == books.length) { // resizing array by doubling length
                        if (books.length == 0)
                            temp = new Book[1];
                        else {
                            temp = new Book[2 * books.length];
                            System.arraycopy(books, 0, temp, 0, books.length);
                        }
                        books = temp;
                    }
                    books[recordCount] = new Book(recordFields[0], recordFields[1], Double.parseDouble(recordFields[2]), recordFields[3], recordFields[4], Integer.parseInt(recordFields[5]));
                    recordCount++; // keeping track of records to resize array if needed ^^ creates new book every time, straightforward
                } catch (BadIsbn10Exception | BadIsbn13Exception | BadPriceException | BadYearException |
                         NumberFormatException e) { // did not want to check for X's myself, so just made it catch nfe exceptions as well
                    exceptionWriter.write("semantic error in file: " + inputNames[i]);
                    exceptionWriter.newLine();
                    exceptionWriter.write("====================");
                    exceptionWriter.newLine();
                    exceptionWriter.write("Error: "); // similar formatting to part 1
                    if (e instanceof BadIsbn10Exception || e instanceof NumberFormatException)
                        exceptionWriter.write("invalid ISBN 10");
                    if (e instanceof BadIsbn13Exception)
                        exceptionWriter.write("invalid ISBN 13");
                    if (e instanceof BadPriceException)
                        exceptionWriter.write("invalid price");
                    if (e instanceof BadYearException)
                        exceptionWriter.write("invalid year");
                    exceptionWriter.newLine();
                    exceptionWriter.write("Record: " + record);
                    exceptionWriter.newLine();
                    exceptionWriter.newLine();
                    exceptionWriter.flush();
                }
            }
            reader.close();
            serWriter.writeObject(books); // writes whole array to binary file
            serWriter.flush();
        }
    }

    public static void do_part3() throws Exception{ // part three, mainly user interface
        Scanner in = new Scanner(System.in);
        String[] inputNames = {"Cartoons_Comics_Books.csv.ser", "Hobbies_Collectibles_Books.csv.ser", "Movies_TV_Books.csv.ser",
                "Music_Radio_Books.csv.ser", "Nostalgia_Eclectic_Books.csv.ser", "Old_Time_Radio_Books.csv.ser",
                "Sports_Sports_Memorabilia.csv.ser", "Trains_Planes_Automobiles.csv.ser"};
        FileInputStream[] input = new FileInputStream[inputNames.length];
        for(int i = 0; i < input.length; i++){
            input[i] = new FileInputStream(inputNames[i]);
        }
        Book[][] books = new Book[input.length][]; // 2d array, with first dimension being files, and second being records
        ObjectInputStream reader;
        for(int i = 0; i < input.length; i++){ // took a while to make this work, all i needed was to close the reader each time
            reader = new ObjectInputStream(input[i]);
            books[i] = (Book[])reader.readObject();
            reader.close(); // CLOSE, not RESET()
        }
        String user = "";
        int selection = 0;
        while(!(user.toLowerCase().contentEquals("x"))){ // main menu
            System.out.println();
            System.out.println("----------------------------------");
            System.out.println("             Main Menu            ");
            System.out.println("----------------------------------");
            System.out.println(" v View the selected file: " + inputNames[selection]);
            System.out.println(" s Select a file to view");
            System.out.println(" x Exit");
            System.out.println("----------------------------------");
            System.out.println();
            System.out.print("Enter Your Choice: ");
            user = in.next();
            if(user.contentEquals("s")){
                System.out.println("----------------------------------");
                System.out.println("           File Sub-Menu          ");
                System.out.println("----------------------------------");
                for(int i = 0; i< input.length; i++){ // shows all files and record numbers
                    System.out.println(" " + (i + 1) + " " + String.format("%-35s", inputNames[i]) + "(" + getLength(books[i]) + " records)");
                }
                System.out.println(" 9 Exit");
                System.out.println("----------------------------------");
                System.out.println();
                System.out.print("Enter Your Choice: ");
                selection = in.nextInt() - 1;
                if(selection == 8)
                    break;
            }
            if(user.contentEquals("v")){
                System.out.println("viewing : " + String.format("%-35s", inputNames[selection]) + "(" + getLength(books[selection]) + " records)");
                System.out.print("Enter number of records to view: ");
                int n = in.nextInt();
                int current = 0;
                while(n != 0){
                    if(n > 0){ // arbitrary viewing algorithm, self-explanatory
                        for(int i = current; i < n + current; i++){
                            if(i > books[selection].length || books[selection][i] == null){
                                System.out.println("EOF has been reached.");
                                System.out.println("Enter 0 to return");
                                break; // ^^ makes sure not to throw any outofbounds or nullpointer exceptions
                            }
                            System.out.println(books[selection][i].toString());
                        }
                        current = n + current - 1;
                    }
                    if(n < 0){ // same exact thing for negative numbers, showing previous records
                        for(int i = current; i > current + n; i--){
                            if(i < 0){
                                System.out.println("BOF has been reached.");
                                System.out.println("Enter 0 to return");
                                break;
                            }
                            System.out.println(books[selection][i].toString());
                        }
                        current = current + n + 1;
                    }
                    System.out.println();
                    System.out.print("Enter number of records to view: ");
                    n = in.nextInt();
                }
            }
        }
    }

    public static int getLength(Book[] arr){
        int count = 0; // extra method to make sure i am displaying actual number of records and not array lengths (which may be null)
        for(int i = 0; i < arr.length; i++){
            if(arr[i] != null)
                count++;
        }
        return count;
    }

    public static void main(String[] args) throws Exception { // DONE
        do_part1();
        do_part2();
        do_part3();
    }
}


