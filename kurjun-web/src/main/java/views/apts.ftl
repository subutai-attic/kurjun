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
        <a href="${contextPath}/apt/upload" class="b-btn b-btn_green b-btn_search-field-level js-colorbox">
            <i class="fa fa-plus"></i> Upload
        </a>
        <table id="apt_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>id</th>
                <th>md5</th>
                <th>Name</th>
                <th>Context</th>
                <th>Arch</th>
                <th>Version</th>
                <th>size</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if apts?? && apts?has_content >
            <#list apts as a >
            <tr>
                <td>${a.id}</td>
                <td></td>
                <td><a href="${contextPath}/apt/${a.id}/info" class="js-colorbox">${a.name}</a></td>
                <td>${a.id?split(".")[0]}</td>
                <td>${a.architecture}</td>
                <td>${a.version}</td>
                <td>${a.installedSize}</td>
                <td><a href="${contextPath}/apt/${md5sums[a.id]}/download" target="_blank">download</a>  |  <a href="#" onclick="remove('${md5sums[a.id]}')">remove</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <#list md5sums?keys as key>
            <li>${key} val = ${md5sums[key]}</li>
        </#list>
        <form id="removeAptForm" method="post" action></form>
    </div>
</div>

<script>
    function remove(md5sum)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeAptForm').attr('action', '${contextPath}/apt/' + md5sum + '/delete');
            $('#removeAptForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_apt_tab').addClass("b-tabs-menu__item_active");

        $('#apt_tbl').DataTable();
    } );


</script>

<#include "flashscope.ftl"/>

</@layout.parentLayout>