<script type="text/javascript">
    window.onload = function() {
        <#if flash.success?? >
            alert('Error:\n\n${flash.success}');
        </#if>

        <#if flash.error?? >
            alert('${flash.error}');
        </#if>
    }
</script>