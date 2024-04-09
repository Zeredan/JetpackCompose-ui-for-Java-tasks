import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;

public interface Businessable<T> extends Iterable<Pair<String, Number>>, Serializable {
    default String getBusinessInfo() throws SubZeroElementsException {
        return getBusinessInfo() + "\n" + business();
    }
    T business() throws SubZeroElementsException;

    void set(int index, Pair<String, Number> p);

    Pair<String, Number> get(int index);

    void output(OutputStream out) throws IOException;

    void write(Writer writer) throws IOException;

    void print(@NotNull PrintWriter file) throws IOException;

    void println(@NotNull PrintWriter file) throws IOException;
}

