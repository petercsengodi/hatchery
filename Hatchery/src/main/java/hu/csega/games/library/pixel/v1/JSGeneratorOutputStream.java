package hu.csega.games.library.pixel.v1;

import java.io.OutputStream;

public class JSGeneratorOutputStream implements AutoCloseable {
    public JSGeneratorOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void close() throws Exception {
        this.outputStream.close();
    }

    public void writeObject(PixelLibrary pixelSheets) {
    }

    private final OutputStream outputStream;
}
