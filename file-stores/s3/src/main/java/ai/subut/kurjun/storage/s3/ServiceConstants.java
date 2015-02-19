package ai.subut.kurjun.storage.s3;


import ai.subut.kurjun.storage.s3.impl.S3FileStore;


public class ServiceConstants
{
    public static final String SERVICE_PID = S3FileStore.class.getName();

    public static final String BUCKET_NAME = "bucketName";
    public static final String ACCESS_KEY = "accessKey";
    public static final String SECRET_KEY = "secretKey";
    public static final String CREDENTIALS_FILE = "credentialsFile";
}

