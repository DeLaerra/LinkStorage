<#include "security.ftlh">
<#import "login.ftlh" as l>

<#macro page>
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Link Storage</title>

        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css"
              integrity="sha384-WskhaSGFgHYWDcbwN70/dfYBj47jz9qbsMId/iRN3ewGhXQFZCSftd1LZCfmhktB"
              crossorigin="anonymous">
        <link rel="stylesheet" href="static/css/backgroundImage.css">
        <link rel="stylesheet" href="static/css/style.css">

    </head>
    <body>

    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <img src="https://raw.githubusercontent.com/RomanJavaDev/Test2/master/logo5.jpg" class="rounded"/>
        <a class="navbar-brand" href="/">Link Storage</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                 <#if name == "Анонимус"><#else>
                     <li class="nav-item">
                         <a class="nav-link" href="/userHome">Home</a>
                     </li>
                     <li class="nav-item">
                         <a class="nav-link" href="/myAccount">Личный кабинет</a>
                     </li>
                 </#if>
            </ul>
            <div class="navbar-text mr-3">
                ${name}</div>
            <#if name == "Анонимус">
                <a href="/login" class="btn btn-sm btn-primary mr-2" target="_blank">Вход</a>
                <a href="/registration" class="btn btn-sm btn-primary" target="_blank">Регистрация</a>
            <#else>
                <a href="/messages" class="nav-link" title="Сообщения">
                    <span class="iconify" data-icon="mdi:email-outline" data-inline="false"
                          data-height="30"></span>
                </a>
                <@l.logout />
            </#if>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="imgcon">
            <#nested>
        </div>
    </div>
    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
            integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
            integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"
            integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T"
            crossorigin="anonymous"></script>
    </body>
    </html>
</#macro>