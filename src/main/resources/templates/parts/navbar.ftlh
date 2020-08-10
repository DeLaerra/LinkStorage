<#include "security.ftlh">
<#import "login.ftlh" as l>

<nav class="navbar navbar-expand-lg navbar-light bg-light" xmlns="http://www.w3.org/1999/html">
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
            <#if isAdmin>
                <li class="nav-item">
                    <a class="nav-link" href="/user">User list</a>
                </li>
            </#if>
        </ul>
        <div class="navbar-text mr-3">${name}</div>
        <#if name == "Анонимус"><a href="/login" class="btn btn-sm btn-primary"
                                   target="_blank">Вход</a><#else>
            <a href="/messages" class="nav-link" title="Сообщения">
                    <span class="iconify" data-icon="mdi:email-outline" data-inline="false"
                          data-height="30"></span>
            </a>
            <@l.logout />
        </#if>
    </div>
</nav>