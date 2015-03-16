package ai.subut.kurjun.riparser;


import java.io.IOException;
import java.io.InputStream;

import ai.subut.kurjun.model.index.ReleaseFile;


public interface ReleaseIndexParser
{
    ReleaseFile parse( InputStream is ) throws IOException;
}

