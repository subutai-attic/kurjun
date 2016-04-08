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
        <a href="#js-upload-apt" class="b-btn b-btn_green b-btn_search-field-level js-colorbox-inline">
            <i class="fa fa-plus"></i> Upload
        </a>
      <div style="margin-left: 200px">

        <form method="get" actoin="${contextPath}/apt">
          <label>Show by repo: </label><select name="repository" id="repo-filter">
            <#list repos as repo >
              <option value="${repo}" ${(sel_repo?? && sel_repo == repo)?string("selected", "")}>${repo}</option>
            </#list>
        </select>
          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          <label><input type="radio" name="node" value="local" ${(node=="local")?string("checked","")}> Local node</label>
          <label><input type="radio" name="node" value="all" ${(node=="all")?string("checked","")}> All nodes</label>
          <button type="submit">Search</button>
        </form>
      </div>
        <table id="apt_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>Arch</th>
                <th>Version</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if apts?? && apts?has_content >
            <#list apts as a >
            <tr>
                <td><#--a href="${contextPath}/apt/${a.id}/info" class="js-colorbox"></a-->${a.name}</td>
                <td>${a.architecture}</td>
                <td>${a.version}</td>
                <td><a href="${contextPath}/apt/download?repository=${a.id.context}&md5=${a.id.md5Sum}" target="_blank">download</a>
                    |  <a href="#" onclick="removeApt('${a.id.context}','${a.id.md5Sum}')">remove</a>
                    |  <a href="#js-add-trust-rel" onclick="openSharePopup(3, '${a.id.context}', '${a.id.md5Sum}')" class="js-colorbox-inline">share</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeAptForm" method="post" action></form>
    </div>
</div>

<script>
    function removeApt(context, md5sum)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeAptForm').attr('action', '${contextPath}/apt/delete?repository='+context+'&md5='+md5sum);
            $('#removeAptForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_apt_tab').addClass("b-tabs-menu__item_active");

        $('#apt_tbl').DataTable();

      $('table.dataTable').on( 'page.dt', function () {
        setTimeout( function () { recreateColorboxes(); }, 1000 );
      } );
      $('table.dataTable').on( 'length.dt', function ( e, settings, len ) {
        setTimeout( function () { recreateColorboxes(); }, 1000 );
      } );

    } );


</script>

<#include "_popup-upload-apt.ftl"/>
<#include "_popup-share-metadata.ftl"/>

</@layout.parentLayout>