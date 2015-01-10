package ai.subut.kurjun.model;


/**
 * Tag items in the packages index for a Debian Package.
 */
public class TagItem
{
    private String category;

    private String subcategory;


    /**
     * Creates a new TagItem.
     *
     * @param category for lack of better words calling left term category
     * @param subcategory for lack of better words calling right term subcategory
     */
    public TagItem( String category, String subcategory )
    {
        this.category = category;
        this.subcategory = subcategory;
    }


    /**
     * The category (left term) of this TagItem.
     * @return the category for lack of better words
     */
    public String getCategory()
    {
        return category;
    }


    /**
     * The subcategory (right term) of this TagItem.
     * @return the subcategory for lack of better words
     */
    public String getSubcategory()
    {
        return subcategory;
    }
}
