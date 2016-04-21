package ai.subut.kurjun.model.index;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class TagItemTest
{
    private TagItem tagItem;

    @Before
    public void setUp() throws Exception
    {
        tagItem = new TagItem( "category", "subcategory" );
    }


    @Test
    public void getCategory() throws Exception
    {
        assertNotNull( tagItem.getCategory() );
    }


    @Test
    public void getSubcategory() throws Exception
    {
        assertNotNull( tagItem.getSubcategory() );
    }
}