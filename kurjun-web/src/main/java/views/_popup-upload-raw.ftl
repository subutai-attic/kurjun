<div style="display: none">
<div id="js-upload-raw">
    <div class="b-popup__header">
        <span>Upload</span>
        <span onclick="$.colorbox.close();">
            <img src="${contextPath}/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
        </span>
    </div>
        <div class="b-form__wrapper g-margin-bottom-half">
            <form method="POST" enctype="multipart/form-data" action="${contextPath}/raw-files/upload">
              <div class="b-workspace__content">
                <div class="b-form__wrapper g-margin-bottom-half">
                  <label class="b-form-label"><input type="radio" name="repo_type" value="existing" checked />Select repository</label>
                  <label class="b-form-label"><input type="radio" name="repo_type" value="new"/>User repository</label>
                  <br/>
                  <select name="repository" id="repository">
                    <option value="" selected></option>
                  <#if repos?? && repos?has_content >
                      <#list repos as repo>
                        <option value="${repo}">${repo}</option>
                      </#list>
                  </#if>
                  </select>
                </div>
              </div>                <div class="b-workspace__content">
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
</div>