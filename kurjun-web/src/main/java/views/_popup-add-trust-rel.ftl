<div style="display: none">
<div id="js-add-trust-rel">
    <div class="b-popup__header">
        <span>Add trust relation</span>
        <span onclick="$.colorbox.close();">
            <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <form method="POST" action="/relations/trust">
            <div>
                <div class="b-form__wrapper">
                    <label for="target_fprint" class="b-form-label">Target fingerprint</label>
                    <input type="text" name="target_fprint" id="target_fprint" />
                </div>
            </div>
            <div>
                <div class="b-form__wrapper">
                    <label for="template_id" class="b-form-label">Template ID</label>
                    <input type="text" name="template_id" id="template_id" />
                </div>
            </div>
            <div>
                <div class="b-form__wrapper g-margin-bottom-half">
                    <label class="b-form-label">Permissions</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Read" /> Read</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Write" /> Write</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Update" /> Update</label><br/>
                    <label class="b-form-label"><input type="checkbox" name="permission" value="Delete" /> Delete</label>
                </div>
            </div>
            <div class="b-popup__footer">
                <button class="b-btn b-btn_green g-right" type="submit">
                    Add
                </button>
                <div class="clear"></div>
            </div>
        </form>
    </div>
</div>
</div>