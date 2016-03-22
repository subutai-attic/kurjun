<#import "layout-main.ftl" as layout>

<#assign parentHead = styles in layout>
<#assign parentScripts = scripts in layout>

<#macro styles>
</#macro>

<#macro scripts>
</#macro>

<@layout.parentLayout>

<div class="b-workspace__content">
    <div class="b-workspace-content__row">
        <button id="set-sys-owner" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Set system owner
        </button>
        <button id="get-sys-owner" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Get system owner
        </button>
        <br/><br/>
        <button id="add_user_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add User
        </button>
        <table id="users_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>email</th>
                <th>fingerprint</th>
            </tr>
            </thead>
            <tbody>
            <#if users?? && users?has_content >
                <#list users as u >
                <tr>
                    <td><#if u.emailAddress??>${u.emailAddress}</#if></td>
                    <td>${u.keyFingerprint}</td>
                </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</div>

<script>

    $(document).ready( function () {

        $('li#hdr_users_tab').addClass("b-tabs-menu__item_active");
        $('#add_user_btn').colorbox({href:"#js-add-user", inline: true});
        $('#set-sys-owner').colorbox({href:"#set-system-owner", inline: true});
        $('#get-sys-owner').colorbox({href:"${contextPath}/system/owner"});

        $('#users_tbl').DataTable();
    } );

</script>

<#include "_popup-add-user.ftl"/>
<#include "_popup-set-system-owner.ftl"/>
<#include "flashscope.ftl"/>

</@layout.parentLayout>