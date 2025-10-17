package hu.csega.csv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSV {

    public static List<List<String>> parse(String filename) {
        List<List<String>> table = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
            String line;

            while((line = reader.readLine()) != null) {
                List<String> row = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                boolean thereWasAQuoteAlready = false;

                try (ByteArrayInputStream stream = new ByteArrayInputStream(line.getBytes(StandardCharsets.UTF_8))) {
                    int b;
                    while((b = stream.read()) >= 0) {
                        if(b == ',') {
                            row.add(builder.toString());
                            builder = new StringBuilder();
                            thereWasAQuoteAlready = false;
                        } else if(b == '\"') {
                            if(thereWasAQuoteAlready) {
                                builder.append('\"');
                            }
                            readUntilQuote(stream, builder);
                            thereWasAQuoteAlready = true;
                        } else {
                            builder.append((char) b);
                            thereWasAQuoteAlready = false;
                        }
                    }

                    if(builder.length() > 0) {
                        row.add(builder.toString());
                    }
                }

                table.add(row);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }

        return table;
    }

    private static void readUntilQuote(ByteArrayInputStream stream, StringBuilder builder) {
        int b;
        while((b = stream.read()) >= 0) {
            if(b == '\"') {
                return;
            }

            builder.append((char) b);
        }
    }

}
