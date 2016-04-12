package ai.subut.kurjun.db.file;


import java.io.Serializable;


public class JsonWrapper implements Serializable
{
    private String classType;

    private String jsonObject;


    public JsonWrapper( final String classType, final String jsonObject )
    {
        this.classType = classType;
        this.jsonObject = jsonObject;
    }


    public String getClassType()
    {
        return classType;
    }


    public void setClassType( final String classType )
    {
        this.classType = classType;
    }


    public String getJsonObject()
    {
        return jsonObject;
    }


    public void setJsonObject( final String jsonObject )
    {
        this.jsonObject = jsonObject;
    }


    @Override
    public String toString()
    {
        return "JsonWrapper{" +
                "classType='" + classType + '\'' +
                ", jsonObject='" + jsonObject + '\'' +
                '}';
    }


    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof JsonWrapper ) )
        {
            return false;
        }

        final JsonWrapper that = ( JsonWrapper ) o;

        if ( classType != null ? !classType.equals( that.classType ) : that.classType != null )
        {
            return false;
        }
        return jsonObject != null ? jsonObject.equals( that.jsonObject ) : that.jsonObject == null;
    }


    @Override
    public int hashCode()
    {
        int result = classType != null ? classType.hashCode() : 0;
        result = 31 * result + ( jsonObject != null ? jsonObject.hashCode() : 0 );
        return result;
    }
}
