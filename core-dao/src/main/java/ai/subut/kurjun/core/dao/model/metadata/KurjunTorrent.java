package ai.subut.kurjun.core.dao.model.metadata;


import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table( name = KurjunTorrent.TABLE_NAME )
@Access( AccessType.FIELD )
public class KurjunTorrent
{
    public static final String TABLE_NAME = "kurjun_torrent";

    private String id;
    private String absolutePath;
    private String templateId;
}
