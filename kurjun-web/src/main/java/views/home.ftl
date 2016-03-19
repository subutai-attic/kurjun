<#import "layout-main.ftl" as layout>

<#assign parentHead = styles in layout>
<#assign parentScripts = scripts in layout>

<#macro styles>
</#macro>

<#macro scripts>
</#macro>

<@layout.parentLayout "Templates">

<div class="b-workspace__content">
    <div class="b-workspace-content__row">
        <button id="add_tpl_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add Template
        </button>
        <table id="templates_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>Context</th>
                <th>Arch</th>
                <th>Parent</th>
                <th>Version</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if templates?? && templates?has_content >
            <#list templates as t >
            <tr>
                <td><a href="/templates/${t.id}/info" class="js-colorbox">${t.name}</a></td>
                <td></td>
                <td>${t.architecture}</td>
                <td>${t.parent}</td>
                <td>${t.version}</td>
                <td><a href="#" onclick="removeTemplate('${t.id}')">remove</a>  |  <a href="/templates/${t.id}/download" target="_blank">download</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeTemplForm" method="post" action></form>
    </div>
</div>

<script>
    function removeTemplate(templId)
    {
        $('#removeTemplForm').attr('action', '/templates/'+templId);
        $('#removeTemplForm').submit();
    }

    $(document).ready( function () {

        $('li#hdr_templates_tab').addClass("b-tabs-menu__item_active");

        $('#add_tpl_btn').colorbox({href:"#js-add-tpl", inline: true});

        $('#templates_tbl').DataTable();
    } );


</script>

<#include "_popup-add-tpl.ftl"/>
<#include "flashscope.ftl"/>

</@layout.parentLayout>