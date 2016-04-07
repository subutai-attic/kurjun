<div style="display: none">

<div id="js-add-trust-rel">
    <div class="b-popup__header">
        <span>Share template</span>
        <span onclick="$.colorbox.close();">
            <img src="${contextPath}/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <form method="POST" action="${contextPath}/relations/trust">
            <div>
                <div class="b-form__wrapper">
                    <label for="target_fprint" class="b-form-label">With</label>
                    <input type="text" name="target_obj_id" id="target_fprint" placeholder="target fingerprint" />
                </div>
            </div>
            <div style="display: none;">
                <input type="radio" name="trust_obj_type" value="3" checked />
                <input type="text" name="trust_obj_id" id="template_id" />
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
<script type="text/javascript">
    $(document).ready(function(){
        $('input[name=trust_obj_type][type=radio]').change(function(e){
            if (e.target.value=="3") {
                $('#trust_obj_template').show();
                $('#trust_obj_repo').hide();
            }
            else {
                $('#trust_obj_template').hide();
                $('#trust_obj_repo').show();
            }
        });
    });
</script>

</div>