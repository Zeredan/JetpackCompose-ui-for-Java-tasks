import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

public class ArticleArray implements Businessable<String>
{
    protected ArrayList<Pair<String, Number>> articleArray = new ArrayList<>();
    private Integer maxAnnotationPerArticleCount;
    public String arrayName;

    private static ArticleArray fromString(String sourceStr)
    {
        String[] source = sourceStr.split(":");

        String[] headData = source[0].split(" ");
        String[] books = {};

        try {
            books = source[1].split(";");
        } catch (Exception e) {
        }

        ArticleArray arr = new ArticleArray(headData[0], Integer.parseInt(headData[1]));
        for (String book : books) {
            String[] bookData = book.split(",");
            arr.articleArray.add(new Pair<>(bookData[0], Double.parseDouble(bookData[1])));
        }
        return arr;
    }

    public static ArticleArray read(BufferedReader reader) throws IOException
    {
        String sourceStr = reader.readLine();
        return fromString(sourceStr);
    }

    public static ArticleArray input(InputStream inp) throws IOException
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

    public static ArticleArray readFormat(Scanner s) throws IOException
    {
        return fromString(s.nextLine());
    }

    public void setMaxAnnotationPerArticleCount(int newCount)
    {
        if (newCount < 0) throw new IncorrectDataException("Меньше нуля");
        maxAnnotationPerArticleCount = newCount;
    }
    public Integer getMaxAnnotationPerArticleCount(){
        return maxAnnotationPerArticleCount;
    }
    public void plusAssign(Pair<String, Number> book)
    {
        articleArray.add(book);
    }
    public void set(int index, Pair<String, Number> p)
    {
        articleArray.set(index, p);
    }

    public Pair<String, Number> get(int index)
    {
        return articleArray.get(index);
    }

    @Override
    public String getBusinessInfo()
    {
        return "Название сборника статей: " + arrayName + "\nМаксимальное количество аннотаций: " + maxAnnotationPerArticleCount;
    }

    @Override
    public String business() throws SubZeroElementsException {
        Double sum = 0.0;
        for (Pair<String, Number> pageCount : articleArray)
        {
            sum += pageCount.component2().floatValue();
        }
        if (sum - maxAnnotationPerArticleCount * articleArray.size() < 0) throw new SubZeroElementsException("Количество страниц меньше нуля");
        return "Всего страниц без аннотаций: " + (sum - maxAnnotationPerArticleCount * articleArray.size());
    }

    @Override
    public void output(OutputStream out) throws IOException { //arrayName 12:qwe,20;abc,31;
        out.write(arrayName.getBytes());
        out.write(" ".getBytes());
        out.write(maxAnnotationPerArticleCount.toString().getBytes());
        out.write(":".getBytes());
        for (int i = 0; i < articleArray.size(); ++i) {
            out.write(articleArray.get(i).component1().getBytes());
            out.write(",".getBytes());
            out.write(articleArray.get(i).component2().toString().getBytes());
            if (i < articleArray.size() - 1) out.write(";".getBytes());
        }
    }

    @Override
    public void write(Writer writer) throws IOException { //arrayName 12:qwe,20;abc,31;
        writer.write(arrayName + " " + maxAnnotationPerArticleCount + ":");
        for (int i = 0; i < articleArray.size(); ++i) {
            writer.write(articleArray.get(i).component1());
            writer.write(",");
            writer.write(articleArray.get(i).component2().toString());
            if (i < articleArray.size() - 1) writer.write(";");
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
            ArticleArray other = (ArticleArray) b;
            return (this.arrayName.equals(other.arrayName))
                    &&
                    (this.maxAnnotationPerArticleCount.equals(other.maxAnnotationPerArticleCount))
                    &&
                    (this.articleArray.equals(other.articleArray));
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
        result += maxAnnotationPerArticleCount;
        return result;
    }

    @Override
    public String toString()
    {
        return getBusinessInfo() + '\n' + articleArray.toString();
    }

    @NotNull
    @Override
    public Iterator<Pair<String, Number>> iterator() {
        return articleArray.iterator();
    }

    @SafeVarargs
    public ArticleArray(String arrayName, Integer maxAnnotationPerArticleCount, Pair<? extends String, ? extends Number>... pairs)
    {
        if (maxAnnotationPerArticleCount < 0) throw new IncorrectDataException("Количество аннотаций меньше нуля");
        this.arrayName = arrayName;
        this.maxAnnotationPerArticleCount = maxAnnotationPerArticleCount;
        for (Pair<? extends String, ? extends Number> pair : pairs) {
            plusAssign(new Pair<String, Number>(pair.component1(), pair.component2()));
        }
    }
}


/*
        OutputStream os;
        FileOutputStream fos;
        FilterOutputStream fios;
        PrintStream ps;
        BufferedOutputStream bos;
        DataOutputStream dos;

        //====================================================================
        InputStream is;
        FileInputStream fis;
        FilterInputStream fiis;
        BufferedInputStream bis;
        DataInputStream dis;
        //====================================================================
        Writer w;
        FileWriter fw;
        StringWriter sw;
        PrintWriter pw;
        BufferedWriter bw;
        OutputStreamWriter osw;

        //=====================================================================
        Reader r;
        FileReader fr;
        StringReader sr;
        Scanner s;
        BufferedReader br;
 */