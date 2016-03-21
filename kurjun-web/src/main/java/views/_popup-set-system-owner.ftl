<div style="display: none">
    <div id="set-system-owner">
        <div class="b-popup__header">
            <span>Set system owner</span>
        <span onclick="$.colorbox.close();">
            <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
        </div>
        <div class="b-form__wrapper g-margin-bottom-half">
            <form method="post" action="/system/owner">
                <div class="b-workspace__content">
                    <div class="b-form__wrapper g-margin-bottom-half">
                        <label class="b-form-label">Owner Fingerprint</label>
                        <input type="text" name="fingerprint" class="b-form-input " />
                    </div>
                </div>
                <div class="b-workspace__content">
                    <div class="b-form__wrapper g-margin-bottom-half">
                        <label class="b-form-label">Owner Public Key</label>
                        <textarea name="key" class="b-form-input b-form-input_full bp-set-pub-key" style="width: 370px; height: 160px"></textarea>
                    </div>
                </div>
                <div class="b-popup__footer">
                    <button class="b-btn b-btn_green g-right" type="submit">
                        Set
                    </button>
                    <div class="clear"></div>
                </div>
            </form>
        </div>
    </div>
</div>