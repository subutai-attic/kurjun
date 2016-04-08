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
        <#--button id="add_repo_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add repo
        </button-->
        <table id="repos_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Repo name</th>
                <th>Repo Type</th>
                <th>Owner</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if repos?? && repos?has_content >
                <#list repos as r >
                <tr>
                    <td><#if r.id.context??>${r.id.context} - </#if></td>
                    <td><#if r.id.type??>${r.getTypeName()}</#if></td>
                    <td><#if r.owner??>${r.owner}</#if></td>
                    <td><a href="${contextPath}/relations/by-object?id=${r.id.context}&obj_type=4" class="js-colorbox">permissions</a>
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

      $('table.dataTable').on( 'page.dt', function () {
        setTimeout( function () { recreateColorboxes(); }, 1000 );
      } );
      $('table.dataTable').on( 'length.dt', function ( e, settings, len ) {
        setTimeout( function () { recreateColorboxes(); }, 1000 );
      } );

    } );

</script>

</@layout.parentLayout>