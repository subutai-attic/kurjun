<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="favicon.ico" rel="icon" type="image/x-icon" />
    <!--<base href="/">-->

    <title>Kurjun</title>

    <link rel="stylesheet" href="${contextPath}/assets/css/style.css"/>

    <!-- DIRS -->
    <!-- END DIRS -->
</head>

<body>



<div class="b-login">
    <div class="b-login__form b-login__form_center" style="width: 400px">
        <div class="b-login-form-header">
            <a class="kurjun_login_ico" href="#"><img src="${contextPath}/assets/img/icons/kurjun.png" height="65px" alt=""></a>
            <div class="kurjun_login_text">Kurjun</div>
        </div>
        <div class="b-workspace b-workspace_login">

            <div class="b-workspace__header">
                <h1 class="b-title">Log in</h1>
            </div>

            <div class="b-workspace__content">
                <div class="b-form">
                    <#if flash.error??>
                    <div class="b-form__error-message">
                        ${flash.error}
                    </div>
                    </#if>
                    <form method="POST" action="${contextPath}/login">
                        <div class="b-form__wrapper g-margin-bottom-half">
                            <label class="b-form-label">Fingerprint</label>
                            <input
                                    class="b-form-input b-form-input_full"
                                    id="subt-input__login"
                                    name="fingerprint"
                                    tabindex=1
                                    type="text">
                        </div>
                        <div class="b-form__wrapper g-margin-bottom">
                            <label class="b-form-label">Message</label>
                            <textarea name="message" id="subt-input__password" tabindex=2 class="b-form-input b-form-input_full" style="width: 370px; height: 160px"></textarea>
                        </div>
                        <div class="b-login-form-btn">
                            <button type="submit" class="b-btn b-btn_green b-btn_full-width" id="subt-button__login">
                                Login
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>


<!-- BASE -->
<script src="${contextPath}/assets/js/jquery-2.1.1.min.js"></script>
<script>
  console.log($('form').attr('action'));
</script>

</body>

</html>
