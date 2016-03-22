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
        <button id="add_repo_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add repo
        </button>
        <table id="repos_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Repo name</th>
                <th>Owner</th>
            </tr>
            </thead>
            <tbody>
            <#if repos?? && repos?has_content >
                <#list repos as r >
                <tr>
                    <td>${r}</td>
                    <td></td>
                </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</div>

<script>

    $(document).ready( function () {

        $('li#hdr_repos_tab').addClass("b-tabs-menu__item_active");
        $('#add_repo_btn').colorbox({href:"#js-add-repo", inline: true});

        $('#repos_tbl').DataTable();
    } );

</script>

<#include "_popup-add-repo.ftl"/>
<#include "flashscope.ftl"/>

</@layout.parentLayout>