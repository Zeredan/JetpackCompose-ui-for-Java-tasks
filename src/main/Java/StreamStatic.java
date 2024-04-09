import java.io.*;
import java.util.Scanner;

class StreamStatic {
    public static void output(Businessable<String> b, OutputStream o) throws IOException {
        b.output(o);
    }
    public static void write(Businessable<String> b, Writer w) throws IOException {
        b.write(w);
    }
    public static BookArray readBookArray(BufferedReader br) throws IOException {
        return BookArray.read(br);
    }
    public static ArticleArray readArticleArray(BufferedReader br) throws IOException {
        return ArticleArray.read(br);
    }
    public static BookArray inputBookArray(InputStream inp) throws IOException {
        return BookArray.input(inp);
    }
    public static ArticleArray inputArticleArray(InputStream inp) throws IOException {
        return ArticleArray.input(inp);
    }
    public static void serialize(Businessable<String> b, ObjectOutputStream os) throws IOException {
        os.writeObject(b);
    }
    public static Businessable<String> deserialize(ObjectInputStream os) throws IOException, ClassNotFoundException {
        return (Businessable<String>) os.readObject();
    }
    public static BookArray readFormatBookArray(Scanner s) throws IOException {
        return BookArray.readFormat(s);
    }
    public static ArticleArray readFormatArticleArray(Scanner s) throws IOException {
        return ArticleArray.readFormat(s);
    }

}
