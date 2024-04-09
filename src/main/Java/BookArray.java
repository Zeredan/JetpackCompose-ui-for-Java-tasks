import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class BookArray implements Businessable<String>
{
    protected ArrayList<Pair<String, Number>> bookArray = new ArrayList<>();

    private Integer startingPagesCount;
    public String arrayName;

    private static BookArray fromString(String sourceStr)
    {
        String[] source = sourceStr.split(":");

        String[] headData = source[0].split(" ");
        String[] books = {};

        try {
            books = source[1].split(";");
        } catch (Exception e) {
        }

        BookArray arr = new BookArray(headData[0], Integer.parseInt(headData[1]));
        for (String book : books) {
            String[] bookData = book.split(",");
            arr.bookArray.add(new Pair<>(bookData[0], Double.parseDouble(bookData[1])));
        }
        return arr;
    }

    public static BookArray read(BufferedReader reader) throws IOException
    {
        String sourceStr = reader.readLine();
        return fromString(sourceStr);
    }

    public static BookArray input(InputStream inp) throws IOException
    {
        StringWriter sw = new StringWriter();
        while(true)
        {
            int ch = inp.read();
            if (ch == '\n' || ch == -1) break;
            sw.write(ch);
        }
        return fromString(sw.getBuffer().toString());
    }

    public static BookArray readFormat(Scanner s) throws IOException
    {
        return fromString(s.nextLine());
    }

    public void setStartingPagesCount(int newCount)
    {
        if (newCount < 0) throw new IncorrectDataException("Меньше нуля");
        startingPagesCount = newCount;
    }
    public Integer getStartingPagesCount(){
        return startingPagesCount;
    }
    public void plusAssign(Pair<String, Number> book)
    {
        bookArray.add(book);
    }
    public void set(int index, Pair<String, Number> p)
    {
        bookArray.set(index, p);
    }

    public Pair<String, Number> get(int index)
    {
        return bookArray.get(index);
    }

    @Override
    public String getBusinessInfo()
    {
        return "Название серии книг: " + arrayName + "\nКоличество вводных страниц: " + startingPagesCount;
    }
    @Override
    public String business() throws SubZeroElementsException {
        Double sum = 0.0;
        for (Pair<String, Number> pageCount : bookArray)
        {
            sum += pageCount.component2().floatValue();
        }
        if (sum - startingPagesCount * bookArray.size() < 0) throw new SubZeroElementsException("Значимых страниц меньше 0");
        return "Всего страниц без вводных: " + (sum - startingPagesCount * bookArray.size());
    }

    @Override
    public void output(OutputStream out) throws IOException { //arrayName 12:qwe,20;abc,31;
        out.write(arrayName.getBytes());
        out.write(" ".getBytes());
        out.write(startingPagesCount.toString().getBytes());
        out.write(":".getBytes());
        for (int i = 0; i < bookArray.size(); ++i) {
            out.write(bookArray.get(i).component1().getBytes());
            out.write(",".getBytes());
            out.write(bookArray.get(i).component2().toString().getBytes());
            if (i < bookArray.size() - 1) out.write(";".getBytes());
        }
    }

    @Override
    public void write(Writer writer) throws IOException { //arrayName 12:qwe,20;abc,31;
        writer.write(arrayName + " " + (startingPagesCount.toString()) + ":");
        for (int i = 0; i < bookArray.size(); ++i) {
            writer.write(bookArray.get(i).component1());
            writer.write(",");
            writer.write(bookArray.get(i).component2().toString());
            if (i < bookArray.size() - 1) writer.write(";");
        }
    }

    @Override
    public void print(@NotNull PrintWriter file) throws IOException {
        write(file);
    }

    @Override
    public void println(@NotNull PrintWriter file) throws IOException {
        print(file);
        file.println();
    }

    @Override
    public boolean equals(Object b)
    {
        try {
            BookArray other = (BookArray) b;
            return (this.arrayName.equals(other.arrayName))
                    &&
                    (this.startingPagesCount.equals(other.startingPagesCount))
                    &&
                    (this.bookArray.equals(other.bookArray));
        }
        catch(Exception e)
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        int result = 0;

        for (char ch : arrayName.toCharArray())
        {
            result += ch;
        }
        result += startingPagesCount;
        return result;
    }

    @Override
    public String toString()
    {
        return getBusinessInfo() + '\n' + bookArray.toString();
    }

    @NotNull
    @Override
    public Iterator<Pair<String, Number>> iterator() {
        return bookArray.iterator();
    }

    @SafeVarargs
    public BookArray(String arrayName, Integer startingPagesCount, Pair<? extends String, ? extends Number>... pairs)
    {
        if (startingPagesCount < 0) throw new IncorrectDataException("Количество страниц меньше нуля");
        this.arrayName = arrayName;
        this.startingPagesCount = startingPagesCount;
        for (Pair<? extends String, ? extends Number> pair : pairs) {
            plusAssign(new Pair<String, Number>(pair.component1(), pair.component2()));
        }
    }
}
