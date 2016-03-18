<div style="display: none">
<div id="js-add-tpl">
    <div class="b-popup__header">
        <span>Add template</span>
        <span onclick="$.colorbox.close();">
            <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
        <div class="b-form__wrapper g-margin-bottom-half">
            <form method="POST" enctype="multipart/form-data" action="/templates">
                <div class="b-workspace__content">
                    <div class="b-form__wrapper g-margin-bottom-half">
                        <input type="file" name="file" id="fileinput" />
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