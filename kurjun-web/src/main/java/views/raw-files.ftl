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
      <div style="margin-left: 200px">

        <form method="get" actoin="${contextPath}/raw-files">
          <label>Show by repo: </label><select name="repository" id="repo-filter">
            <#list repos as repo >
              <option value="${repo}" ${(sel_repo?? && sel_repo == repo)?string("selected", "")} >${repo}</option>
            </#list>
        </select>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <label><input type="radio" name="node" value="local" ${(node=="local")?string("checked","")}> Local node</label>
          <label><input type="radio" name="node" value="all" ${(node=="all")?string("checked","")}> All nodes</label>
          <button type="submit">Search</button>
        </form>
      </div>
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
                <td><a href="${contextPath}/raw-files/download?repository=${f.id.context}&md5=${f.id.md5Sum}" target="_blank">download</a>
                    |  <a href="#" onclick="removeFile('${f.id.context}','${f.id.md5Sum}')">remove</a>
                    |  <a href="#js-add-trust-rel" onclick="openSharePopup(3, '${f.id.context}', '${f.id.md5Sum}')" class="js-colorbox-inline">share</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeRawFileForm" method="post" action></form>
    </div>
</div>

<script>
    function removeFile(context, md5)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeRawFileForm').attr('action', '${contextPath}/raw-files/delete?repository='+context+'&md5='+md5);
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
<#include "_popup-share-metadata.ftl"/>

</@layout.parentLayout>