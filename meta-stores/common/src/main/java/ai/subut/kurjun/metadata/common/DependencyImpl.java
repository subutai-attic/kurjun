/*
 * Copyright 2015 azilet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.subut.kurjun.metadata.common;


import java.util.List;

import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.RelationOperator;


public class DependencyImpl implements Dependency
{
    private String packageName;
    private List<Dependency> alternatives;
    private String version;
    private RelationOperator relationOperator;


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
        return relationOperator;
    }


    public void setRelationOperator( RelationOperator relationOperator )
    {
        this.relationOperator = relationOperator;
    }


}

