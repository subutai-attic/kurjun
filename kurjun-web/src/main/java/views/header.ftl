<div class="b-workspace__header b-workspace__header_tabs">
    <img src="/assets/img/icons/kurjun.png" height="50px" alt="">
    <div class="b-nav-menu__add">
        <#if userInfo?? && userInfo.emailAddress?? && userInfo.emailAddress?length gt 0 >
        Hello ${userInfo.emailAddress} | <a href="#" id="js-logout">Logout</a>
        <#else>
        Hello anonymous | <a href="/login" id="js-login">Login</a>
        </#if>
    </div>
    <form id="logoutForm" method="post" action="/logout" ></form>
</div>
<div class="b-workspace__header b-workspace__header_tabs">

    <div class="b-tabs-menu b-tabs-menu_header">

        <ul>
            <li id="hdr_templates_tab" class="b-tabs-menu__item">
                <a href="/">Templates</a>
            </li>
            <li id="hdr_apt_tab" class="b-tabs-menu__item">
                <a href="/apt">Apt</a>
            </li>
            <li id="hdr_raw_tab" class="b-tabs-menu__item">
                <a href="/raw-files">Raw files</a>
            </li>
            <li id="hdr_repos_tab" class="b-tabs-menu__item">
                <a href="/repositories">Repositories</a>
            </li>
            <li id="hdr_relations_tab" class="b-tabs-menu__item">
                <a href="/relations">Relations</a>
            </li>
            <li id="hdr_users_tab" class="b-tabs-menu__item">
                <a href="/users">Users</a>
            </li>
        </ul>
    </div>
</div>