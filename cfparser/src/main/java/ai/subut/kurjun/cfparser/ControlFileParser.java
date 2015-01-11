package ai.subut.kurjun.cfparser;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import ai.subut.kurjun.model.metadata.PackageMetadata;


/**
 * A parser interface for control files.
 */
public interface ControlFileParser
{
    PackageMetadata parse( Map<String,Object> params, File controlFile ) throws ParseException, IOException;
}
