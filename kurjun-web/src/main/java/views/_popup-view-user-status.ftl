<div>
    <div class="b-popup__header">
        <span style="padding-right: 10px;">Operation Status</span>
    <span onclick="$.colorbox.close();">
        <img src="${contextPath}/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
    </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <div class="b-workspace__content">
            <div class="b-form__wrapper g-margin-bottom-half">
            <#if user??>
                <table>
                  <tr><td>Status</td><td>User created successfully !!!</td></tr>
                  <tr><td>Email</td><td>${user.emailAddress}</td></tr>
                  <tr><td>Fingerprint</td><td>${user.keyFingerprint}</td></tr>
                  <tr><td>AuthID</td><td>${user.signature}</td></tr>
                </table>
            <#else>
                User registration failed.
            </#if>
            </div>
        </div>
    </div>
</div>