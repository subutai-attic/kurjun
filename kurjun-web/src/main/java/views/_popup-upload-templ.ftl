<div>
    <div class="b-popup__header">
        <span>Upload template</span>
        <span onclick="$.colorbox.close();">
            <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
        <div class="b-form__wrapper g-margin-bottom-half">
            <form method="POST" enctype="multipart/form-data" action="/templates/upload">
                <div class="b-workspace__content">
                    <div class="b-form__wrapper g-margin-bottom-half">
                        <label class="b-form-label"><input type="radio" name="repo_type" value="existing" checked />Select repository</label>
                        <label class="b-form-label"><input type="radio" name="repo_type" value="new"/>New repository</label>
                        <br/>
                        <select name="repository" id="repository">
                            <#if repos?? && repos?has_content >
                                <#list repos as repo>
                                <option value="${repo}">${repo}</option>
                                </#list>
                            </#if>
                        </select>
                        <input type="text" name="repo_name" id="repo_name" placeholder="Enter name for new repo..." style="display:none" />
                    </div>
                </div>
                <div class="b-workspace__content">
                    <div class="b-form__wrapper g-margin-bottom-half">
                        <input type="file" name="file" id="fileinput" />
                    </div>
                </div>
                <div class="b-popup__footer">
                    <button class="b-btn b-btn_green g-right" type="submit">
                        Upload
                    </button>
                    <div class="clear"></div>
                </div>
            </form>
        </div>
</div>
<script type="text/javascript">
    $(document).ready(function(){
        $('input[name=repo_type][type=radio]').change(function(e){
            if (e.target.value=="existing") {
                $('#repository').show();
                $('#repo_name').hide();
            }
            else {
                $('#repository').hide();
                $('#repo_name').show();
            }
        });
    });
</script>