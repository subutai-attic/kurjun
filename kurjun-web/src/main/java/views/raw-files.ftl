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
        <a href="#js-upload-raw" class="b-btn b-btn_green b-btn_search-field-level js-colorbox-inline">
            <i class="fa fa-plus"></i> Upload file
        </a>
        <table id="raw_files_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>Size</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if files?? && files?has_content >
            <#list files as f >
            <tr>
                <td><#--a href="${contextPath}/raw-files/info" class="js-colorbox"></a-->${f.name}</td>
                <td>${f.size}</td>
                <td><a href="${contextPath}/raw-files/${f.id}/download" target="_blank">download</a>  |  <a href="#" onclick="removeFile('${f.id}')">remove</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeRawFileForm" method="post" action></form>
    </div>
</div>

<script>
    function removeFile(fileId)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeRawFileForm').attr('action', '${contextPath}/raw-files/' + fileId + '/delete');
            $('#removeRawFileForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_raw_tab').addClass("b-tabs-menu__item_active");

        $('#raw_files_tbl').DataTable();

      $('table.dataTable').on( 'page.dt', function () {
        setTimeout( function () { recreateColorboxes(); }, 1000 );
      } );
      $('table.dataTable').on( 'length.dt', function ( e, settings, len ) {
        setTimeout( function () { recreateColorboxes(); }, 1000 );
      } );

    } );


</script>

<#include "_popup-upload-raw.ftl"/>

</@layout.parentLayout>