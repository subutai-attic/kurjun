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
        <a href="${contextPath}/templates/upload" class="b-btn b-btn_green b-btn_search-field-level js-colorbox">
            <i class="fa fa-plus"></i> Upload Template
        </a>
        <div style="margin-left: 200px">

            <form method="get" actoin="${contextPath}/">
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
        <table id="templates_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Name</th>
                <th>Owner</th>
                <th>Context</th>
                <th>Arch</th>
                <th>Parent</th>
                <th>Version</th>
                <th>Size</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if templates?? && templates?has_content >
            <#list templates as t >
            <tr>
                <td><a href="${contextPath}/templates/info?repository=${t.id.context}&md5=${t.id.md5Sum}" class="js-colorbox">${t.name}</a></td>
                <td><#if t.owner??>${t.owner}</#if></td>
                <td>${t.id.context}</td>
                <td>${t.architecture}</td>
                <td>${t.parent}</td>
                <td><#if t.version??>${t.version}</#if></td>
                <td><#if t.size??>${t.size}</#if></td>
                <td><a href="${contextPath}/templates/download?repository=${t.id.context}&md5=${t.id.md5Sum}" target="_blank">download</a>
                    |  <a href="${contextPath}/relations/by-object?id=${t.id.context+"."+t.id.md5Sum}&obj_type=3" class="js-colorbox">permissions</a>
                    <#if !( isPublic?? && isPublic ) >
                    |  <a href="#" onclick="removeTemplate('repository=${t.id.context}&md5=${t.id.md5Sum}')">remove</a>
                    |  <a href="#js-add-trust-rel" onclick="$('#template_id').val('${t.id.context+"."+t.id.md5Sum}')" class="js-colorbox-inline">share</a>
                    </#if>
                </td>
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
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed)
        {
            $('#removeTemplForm').attr('action', '${contextPath}/templates/delete?'+templId);
            $('#removeTemplForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_templates_tab').addClass("b-tabs-menu__item_active");

        $('#templates_tbl').DataTable();

        $('#repo-filter').on('change', function(e){
            //$(this).parent().submit();
        });

        $('table.dataTable').on( 'page.dt', function () {
            setTimeout( function () { recreateColorboxes(); }, 1000 );
        } );
        $('table.dataTable').on( 'length.dt', function ( e, settings, len ) {
            setTimeout( function () { recreateColorboxes(); }, 1000 );
        } );
    } );

</script>

<#include "_popup-share-template.ftl"/>

</@layout.parentLayout>