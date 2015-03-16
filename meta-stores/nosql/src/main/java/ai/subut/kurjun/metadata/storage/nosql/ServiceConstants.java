package ai.subut.kurjun.metadata.storage.nosql;


import ai.subut.kurjun.metadata.storage.nosql.impl.NoSqlPackageMetadataStore;



public class ServiceConstants
{

    public static final String SERVICE_PID = NoSqlPackageMetadataStore.class.getName();

    public static final String NODE = "node";
    public static final String PORT = "port";
    public static final String REPLICATION_CONFIG_FILE = "replicationConfigFile";

}

