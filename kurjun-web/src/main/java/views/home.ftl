<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="favicon.ico" rel="icon" type="image/x-icon">

    <title>Kurjun</title>

    <link rel="stylesheet" href="/assets/css/libs/datatables.css">
    <link rel="stylesheet" href="/assets/css/colorbox.css">

    <link rel="stylesheet" href="/assets/css/style.css">
</head>

<body>

<div class="b-workspace__header b-workspace__header_tabs">
    <img src="/assets/img/icons/kurjun.png" height="50px" alt="">
    <div class="b-nav-menu__add"><a href="#">Logout</a></div>
</div>
<div class="b-workspace__header b-workspace__header_tabs">

    <div class="b-tabs-menu b-tabs-menu_header">

        <ul>
            <li class="b-tabs-menu__item b-tabs-menu__item_active">
                Templates
            </li>
            <li class="b-tabs-menu__item">
                Users
            </li>
        </ul>
    </div>
</div>

<div class="b-workspace__content">
    <div class="b-workspace-content__row">
        <button id="add_tpl_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add Template
        </button>
        <button id="add_user_btn" class="b-btn b-btn_green b-btn_search-field-level">
            <i class="fa fa-plus"></i> Add User
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
                <td>${t.name}</td>
                <td></td>
                <td>${t.architecture}</td>
                <td>${t.parent}</td>
                <td>${t.version}</td>
                <td><a href="#" onclick="removeTemplate('${t.id}')">remove</a></td>
            </tr>
            </#list>
            </#if>
            </tbody>
        </table>
        <form id="removeTemplForm" method="post" action></form>
    </div>
</div>
<!-- BASE -->
<script src="/assets/js/jquery-2.1.1.min.js"></script>
<script src="/assets/js/datatables.min.js"></script>
<script src="/assets/js/jquery.colorbox-min.js"></script>
<script>
    function removeTemplate(templId)
    {
        $('#removeTemplForm').attr('action', '/templates/'+templId);
        $('#removeTemplForm').submit();
    }

    $(document).ready( function () {
        $('#add_tpl_btn').colorbox({href:"#js-add-tpl", inline: true});
        $('#add_user_btn').colorbox({href:"#js-add-user", inline: true});

        $('#templates_tbl').DataTable();
    } );


</script>
</body>
</html>

<#include "_popup-add-tpl.ftl"/>
<#include "_popup-add-user.ftl"/>
<#include "flashscope.ftl"/>