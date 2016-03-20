<div>
    <div class="b-popup__header">
        <span style="padding-right: 10px;">System owner info</span>
    <span onclick="$.colorbox.close();">
        <img src="/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
    </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <div class="b-workspace__content">
            <div class="b-form__wrapper g-margin-bottom-half">
            <#if sys_owner??>
                <table>
                    <tr><td>Email</td><td>${sys_owner.emailAddress}</td></tr>
                    <tr><td>Fingerprint</td><td>${sys_owner.keyFingerprint}</td></tr>
                </table>
            <#else>
                System owner not found.
            </#if>
            </div>
        </div>
    </div>
</div>