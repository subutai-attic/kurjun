<div style="display: none">
<div id="js-add-repo">
    <div class="b-popup__header">
        <span>Add repo</span>
        <span onclick="$.colorbox.close();">
            <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <form method="post" action="/repositories/add">
            <div class="b-workspace__content">
                <div class="b-form__wrapper g-margin-bottom-half">
                    <label class="b-form-label">Repo name</label>
                    <input type="text" name="repo_name" class="b-form-input" />
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