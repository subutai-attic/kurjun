CREATE TABLE IF NOT EXISTS {keyspace}.{table}
(
    {checksum} text,
    {name} text,
    {version} text,
    {data} text,
    primary key({checksum})
);
