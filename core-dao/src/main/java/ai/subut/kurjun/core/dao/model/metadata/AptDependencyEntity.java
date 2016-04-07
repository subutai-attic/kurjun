package ai.subut.kurjun.core.dao.model.metadata;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ai.subut.kurjun.model.metadata.apt.Dependency;
import ai.subut.kurjun.model.metadata.apt.RelationOperator;


/**
 *
 */
@Entity
@Table( name = "apt_dependencies" )
@Access( AccessType.FIELD )
public class AptDependencyEntity implements Dependency
{

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    @Column( name = "id" )
    private long id;

    @Column( name = "package_name" )
    private String packageName;


    @OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = AptDependencyEntity.class)
    @Column( name = "alternatives" )
    private List<Dependency> alternatives = new ArrayList<>();


    @Column( name = "version" )
    private String version;


    @Enumerated( EnumType.STRING )
    private RelationOperator dependencyOperator;


    public long getId()
    {
        return id;
    }


    public void setId( final long id )
    {
        this.id = id;
    }


    public String getPackageName()
    {
        return packageName;
    }


    public void setPackageName( final String packageName )
    {
        this.packageName = packageName;
    }


    @Override
    public String getPackage()
    {
        return packageName;
    }


    public void setPackage( String packageName )
    {
        this.packageName = packageName;
    }


    @Override
    public List<Dependency> getAlternatives()
    {
        return alternatives;
    }


    public void setAlternatives( List<Dependency> alternatives )
    {
        this.alternatives = alternatives;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public RelationOperator getDependencyOperator()
    {
        return dependencyOperator;
    }


    public void setDependencyOperator( RelationOperator relationOperator )
    {
        this.dependencyOperator = relationOperator;
    }
}
