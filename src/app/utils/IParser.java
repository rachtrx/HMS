package app.utils;

import java.util.ArrayList;

public interface IParser {
    
    public void read(String path, ArrayList<String[]> data); // read multiple lines
    public void write(String path, ArrayList<String> data); // write 1 line
}
