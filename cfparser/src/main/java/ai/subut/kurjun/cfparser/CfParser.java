package ai.subut.kurjun.cfparser;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import ai.subut.kurjun.model.PkgMeta;


/**
 * A parser interface for control files.
 */
public interface CfParser
{
    PkgMeta parse( File controlFile ) throws ParseException, IOException;
}
