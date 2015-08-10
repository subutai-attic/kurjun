package ai.subut.kurjun.index.service;


import java.io.IOException;
import java.io.OutputStream;


public interface PackagesIndexBuilder
{
    void buildIndex( OutputStream os ) throws IOException;
}

