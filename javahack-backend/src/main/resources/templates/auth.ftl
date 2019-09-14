<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="styles/auth.css" type="text/css">
</head>
<body>
<div class="body-container">

    <div class="auth-page-container">

        <div class="main-form front">
            <div class="main-form-inner">
                <div class="engine-name"></div>
                <form method="post" class="adm-form login-form">
                    <div class="adm-login-row">
                        <input class="login-input" type="text" placeholder="username" id="login" name="login">
                    </div>

                    <div class="adm-login-row">

                        <input class="login-input" type="password" placeholder="password" id="pwd" name="pwd">
                    </div>
                    <div class="adm-login-row adm-clearfix adm-margin">
                        <button class="adm-button-reverse adm-float-left toggler adm-button-small" type="button">restore password</button>
                        <button class="adm-button-action adm-float-right" type="submit">Sig-in</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="main-form back">
            <div class="main-form-inner">
                <div class="engine-name"></div>
                <form method="post" action="#" class="adm-form">
                    <div class="adm-login-row restore-pass">
                        <input type="text" id="email" name="email" placeholder="your email" class="login-input">

                    </div>

                    <div class="adm-login-row adm-clearfix adm-margin">
                        <button class="adm-button adm-button-reverse adm-float-left toggler adm-button-small" type="button">back</button>
                        <button class="adm-button adm-button-action adm-float-right" type="button">send</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    </div>
</body>
</html>