package ai.subut.kurjun.index;


import java.io.File;
import java.io.IOException;
import java.util.List;

import ai.subut.kurjun.model.index.IndexPackageMetaData;


public interface PackagesIndexParser
{

    List<IndexPackageMetaData> parse( File indexFile ) throws IOException;

}

