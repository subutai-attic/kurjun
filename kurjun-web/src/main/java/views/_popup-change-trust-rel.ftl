<div id="js-add-trust-rel">
    <div class="b-popup__header">
        <span>Change trust permissions</span>
        <span onclick="$.colorbox.close();">
            <img src="${contextPath}/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <#if relation?? >
        <form method="POST" action="${contextPath}/relations/${relation.id}/change">
            <div>
                <div class="b-form__wrapper g-margin-bottom-half">
                    <label class="b-form-label">Permissions</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Read"
                        ${(relation.permissions?seq_contains("Read"))?then("checked", "")}/> Read</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Write"
                        ${(relation.permissions?seq_contains("Write"))?then("checked", "")}/> Write</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Update"
                        ${(relation.permissions?seq_contains("Update"))?then("checked", "")}/> Update</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Delete"
                        ${(relation.permissions?seq_contains("Delete"))?then("checked", "")}/> Delete</label>
                </div>
            </div>
            <div class="b-popup__footer">
                <button class="b-btn b-btn_green g-right" type="submit">
                    Save
                </button>
                <div class="clear"></div>
            </div>
        </form>
        <#else>
            Relation not found.
        </#if>
    </div>
</div>