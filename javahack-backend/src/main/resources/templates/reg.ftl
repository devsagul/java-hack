<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="styles/reg.css" type="text/css">
</head>
<body>
<div class="body-container">
    <form class="login-form" method="post">
        <div class="login-form-row form-header">
            Физюрики
        </div>
        <div class="adm-login-row">
            <input class="login-input" type="text" placeholder="Телефон" id="login" name="login">
        </div>
                    <div class="adm-login-row">
                        <input class="login-input" type="text" placeholder="Фамилия" id="FIO" name="lastname">
                    </div>
                    <div class="adm-login-row">
                        <input class="login-input" type="text" placeholder="Имя" id="FIO" name="firstname">
                    </div>
                    <div class="adm-login-row">
                        <input class="login-input" type="text" placeholder="Отчество" id="FIO" name="middlename">
                    </div>
                    <div class="adm-login-row">
                        <input class="login-input" type="text" placeholder="ИНН" id="INN" name="INN">
                    </div>
                    <div class="adm-login-row">
                        <input class="login-input" type="text" placeholder="Код налогового органа" id="CodeNalog" name="CodeNalog">
                    </div>

                    <div class="adm-login-row">
                        <span class="custom-dropdown big">
                            <select>
                              <option value="" disabled selected> Вид деятельности</option>
                              <option>Пидор</option>
                              <option>Пидор</option>
                              <option>Пидор</option>
                              <option>Пидор</option>
                              <option>Пидор</option>
                            </select>
                        </span>
                    </div>

                    <div class="adm-login-row">
                        <input class="login-input" type="password" placeholder="Пароль" id="pwd" name="pwd">
                    </div>
        <div class="login-form-row login-form-bottom">
            <a href="localhost/reg" class="no-style bottom-button-wrap""><button class="bottom-button ">Регистрация</button></a>
        </div>
    </form>
</div>
</body>
</html>