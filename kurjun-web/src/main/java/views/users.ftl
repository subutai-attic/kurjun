<#import "layout-main.ftl" as layout>

<#assign parentHead = styles in layout>
<#assign parentScripts = scripts in layout>

<#macro styles>
</#macro>

<#macro scripts>
</#macro>

<@layout.parentLayout "Users">

<div class="b-workspace__content">
    <div class="b-workspace-content__row">
        <button id="add_user_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add User
        </button>
        <#if users?? >User size: ${users?size}</#if>
        <table id="users_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
            </tr>
            </thead>
            <tbody>
            <#if users?? && users?has_content >
                <#list users as u >
                <tr>
                    <td>u</td>
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

        $('#users_tbl').DataTable();
    } );

</script>

<#include "_popup-add-user.ftl"/>
<#include "flashscope.ftl"/>

</@layout.parentLayout>