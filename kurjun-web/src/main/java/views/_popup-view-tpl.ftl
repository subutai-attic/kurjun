<div>
    <div class="b-popup__header">
        <span style="padding-right: 10px;">Template information</span>
    <span onclick="$.colorbox.close();">
        <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
    </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <div class="b-workspace__content">
            <div class="b-form__wrapper g-margin-bottom-half">
            <#if templ_info??>
                <table>
                    <tr><td>ID</td><td>${templ_info.id}</td></tr>
                    <tr><td>name</td><td>${templ_info.name}</td></tr>
                    <tr><td>version</td><td>${templ_info.version}</td></tr>
                    <#--<tr><td>md5</td><td>${templ_info.md5Sum}</td></tr>-->
                    <tr><td>parent</td><td>${templ_info.parent}</td></tr>
                    <tr><td>package name</td><td>${templ_info.package}</td></tr>
                    <tr><td>architecture</td><td>${templ_info.architecture}</td></tr>
                    <tr><td>owner fingerprint</td><td>${templ_info.ownerFprint}</td></tr>
                    <tr><td>size</td><td>${templ_info.size}</td></tr>
                    <#--<tr><td>extra</td><td>${templ_info.extra}</td></tr>-->
                    <#--<tr><td>config contents</td><td>${templ_info.configContents}</td></tr>-->
                    <#--<tr><td>packages contents</td><td>${templ_info.packagesContents}</td></tr>-->
                </table>
            <#else>
                Template not found.
            </#if>
            </div>
        </div>
    </div>
</div>