<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="favicon.ico" rel="icon" type="image/x-icon">
    <!--<base href="/">-->

    <title>Kurjun</title>

    <link rel="stylesheet" href="/assets/css/libs/datatables.css">

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
        <button class="b-btn b-btn_green b-btn_search-field-level">
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
                <tr>
                    <td>test</td>
                    <td>test</td>
                    <td>test</td>
                    <td>test</td>
                    <td>1.0-test</td>
                    <td><a href="">remove</a></td>
                </tr>
            </tbody>
        </table>
    </div>
    </div>
<!-- BASE -->
<script src="/assets/js/jquery-2.1.1.min.js"></script>
<script src="/assets/js/datatables.min.js"></script>
<script>
    $(document).ready( function () {
        $('#templates_tbl').DataTable();
    } );
</script>
</body>
</html>