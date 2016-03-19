<#macro parentHead>
</#macro>

<#macro parentScripts>
</#macro>

<#macro parentLayout title = "Kurjun">
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="favicon.ico" rel="icon" type="image/x-icon">

    <title>Kurjun</title>

    <!-- CSS styles -->
    <link rel="stylesheet" href="/assets/css/libs/datatables.css">
    <link rel="stylesheet" href="/assets/css/colorbox.css">
    <link rel="stylesheet" href="/assets/css/style.css">

    <!-- JavaScript -->
    <script src="/assets/js/jquery-2.1.1.min.js"></script>
    <script src="/assets/js/datatables.min.js"></script>
    <script src="/assets/js/jquery.colorbox-min.js"></script>

    <@parentHead/>
</head>

<body>
    <#include "header.ftl"/>

    <#-- TODO: add flash.error and flash.success message handler here -->

    <input id="bp-plugin-version" value="" type="hidden">

    <#nested/>

    <script>
        $(document).ready(function(){
            $('#js-logout').click(function(){
                $('#logoutForm').submit();
            });

            $('.js-colorbox').colorbox({});
        });
    </script>

    <@parentScripts/>
</body>
</html>

</#macro>