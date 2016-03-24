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
        <a href="${contextPath}/relations/trust" id="add_trust_btn" class="b-btn b-btn_green b-btn_search-field-level js-colorbox">
            <i class="fa fa-plus"></i> Add trust relation
        </a>
        <table id="relations_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Source</th>
                <th>Target</th>
                <th>Object</th>
                <th>Permissions</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <#if relations?? && relations?has_content >
                <#list relations as r >
                <tr>
                    <td>${r.source.id}</td>
                    <td>${r.target.id}</td>
                    <td>${r.trustObject.id}</td>
                    <td>
                     ${(r.permissions?seq_contains("Read"))?then("R"," ")}
                     ${(r.permissions?seq_contains("Update"))?then("U"," ")}
                     ${(r.permissions?seq_contains("Write"))?then("W"," ")}
                     ${(r.permissions?seq_contains("Delete"))?then("D"," ")}
                    </td>
                    <td><a href="${contextPath}/relations/${r.id}/change" class="js-colorbox">change</a> | <a href="#" onclick="removeRelation('${r.id}')">remove</a></td>
                </tr>
                </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeRelationForm" method="post" action></form>
    </div>
</div>

<script>
    function removeRelation(id)
    {
        var confirmed = confirm("Are you sure want to delete it?");
        if (confirmed) {
            $('#removeRelationForm').attr('action', '${contextPath}/relations/' + id + '/delete');
            $('#removeRelationForm').submit();
        }
    }

    $(document).ready( function () {

        $('li#hdr_relations_tab').addClass("b-tabs-menu__item_active");
        //$('#add_trust_btn').colorbox({href:"#js-add-trust-rel", inline: true});

        $('#relations_tbl').DataTable();
    } );

</script>

<#include "flashscope.ftl"/>

</@layout.parentLayout>