package ai.subut.kurjun.index;


import java.io.IOException;
import java.io.OutputStream;


public interface PackagesIndexBuilder
{
    void buildIndex( OutputStream os ) throws IOException;
}

