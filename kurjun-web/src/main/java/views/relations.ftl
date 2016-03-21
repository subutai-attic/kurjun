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
        <button id="add_trust_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add trust relation
        </button>
        <table id="relations_tbl" class="b-data-table">
            <thead>
            <tr>
                <th>Source</th>
                <th>Target</th>
                <th>Object</th>
                <th>Permissions</th>
            </tr>
            </thead>
            <tbody>
            <#if relations?? && relations?has_content >
                <#list relations as r >
                <tr>
                    <td>${r.source.id}</td>
                    <td>${r.target.id}</td>
                    <td>${r.trustObject.id}</td>
                    <td><ul>
                        <#list r.permissions as p >
                        <li>${p}</li>
                        </#list>
                    </ul></td>
                </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</div>

<script>

    $(document).ready( function () {

        $('li#hdr_relations_tab').addClass("b-tabs-menu__item_active");
        $('#add_trust_btn').colorbox({href:"#js-add-trust-rel", inline: true});

        $('#relations_tbl').DataTable();
    } );

</script>

<#include "_popup-add-trust-rel.ftl"/>
<#include "flashscope.ftl"/>

</@layout.parentLayout>