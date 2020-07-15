<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<#include "parts/security.ftlh">

<@c.page>
    <form name="searchAll" action="/userHome" method="GET">
        <input class="form-control mb-4" type="text" name="searchAll" id="searchAll"
               placeholder="Поиск ссылок">
        <#if search??>
            <div class="container">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">Имя</th>
                        <th scope="col">Добавлено</th>
                        <th scope="col">Источник</th>
                        <th scope="col">Рейтинг</th>
                        <th scope="col">#</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list searchAll as s>
                        <tr>
                            <td> <#if s.name??><a href="${s.url}">${s.name}</a><br><#else><a
                                    href="${s.url}">${s.url}<br></a></#if>
                                <#if s.tag??>Теги: ${s.tag}<br></#if>

                                <!-- Button trigger modal -->
                                <button type="button" class="btn btn-light btn-sm" data-toggle="modal"
                                        data-target="#${s.uid}">
                                    Подробнее...
                                </button>
                                <!-- Modal -->
                                <div class="modal fade" id="${s.uid}" tabindex="-1" role="dialog"
                                     aria-labelledby="${s.uid}Label" aria-hidden="true">
                                    <div class="modal-dialog" role="document">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="${s.uid}Label"><#if s.name??><a
                                                        href="${s.url}">${s.name}</a><br><#else><a
                                                        href="${s.url}">${s.url}<br></a></#if></h5>
                                                <button type="button" class="close" data-dismiss="modal"
                                                        aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <a href="${s.url}">${s.url}</a><br>
                                                <#if s.tag??>Теги: ${s.tag}<br></#if>
                                                <#if s.description??><b>Описание: </b>${s.description}<br></#if>
                                                <b>Уровень доступа: </b> <#if s.uidAccessLevel == 0>публичная
                                                    <br><#else>личная
                                                    <br></#if>
                                                <b>Тип: </b>
                                                <#if s.uidReferenceType == 0>текст
                                                    <br><#elseif s.uidReferenceType == 1>видео
                                                    <br>
                                                <#elseif s.uidReferenceType == 2>файл
                                                    <br><#elseif s.uidReferenceType == 3>картинка
                                                    <br></#if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td><#if s.additionDate??>${s.additionDate}<br></#if></td>
                            <td><a href="${s.source}">${s.source}</a></td>
                            <td><#if s.rating??>${s.rating}<br></#if></td>
                            <td>
                                <button type="button" class="btn btn-info btn-sm">Добавить в Home</button>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            </div>
            <br>
        </#if>
        <hr align="center"/>
    </form>
    <form name="sorting" action="/userHome" method="GET">
        <div class="dropdown">
            <button class="btn btn-light btn-sm dropdown-toggle" type="" id="dropdownMenu2" data-toggle="dropdown"
                    aria-haspopup="true" aria-expanded="false">
                Сортировать по:
            </button>
            <div class="dropdown-menu" aria-labelledby="dropdownMenu2">
                <button class="dropdown-item" type="submit" name="sortBy" value="">дате добавления (по-возрастанию)
                </button>
                <button class="dropdown-item" type="submit" name="sortBy" value="rating">рейтингу (по-убыванию)</button>
                <button class="dropdown-item" type="submit" name="sortBy" value="name">имени (в алфавитном порядке)
                </button>
                <button class="dropdown-item" type="submit" name="sortBy" value="source">источнику (в алфавитном
                    порядке)
                </button>
            </div>
        </div>
    </form>
<#--    <@p.pager url page />-->
    <#if page??>
        <div class="container">
        <table class="table table-striped">
        <thead>
        <tr>
            <th scope="col">Имя</th>
            <th scope="col">Добавлено</th>
            <th scope="col">Источник</th>
            <th scope="col">Рейтинг</th>
            <th scope="col">#</th>
        </tr>
        </thead>
        <#list page.content as userRef>
            <tr>
                <td> <#if userRef.name??><a href="${userRef.url}">${userRef.name}</a><br><#else><a
                        href="${userRef.url}">${userRef.url}<br></a></#if>
                    <#if userRef.tag??>Теги: ${userRef.tag}<br></#if>

                    <!-- Button trigger modal -->
                    <button type="button" class="btn btn-light btn-sm" data-toggle="modal"
                            data-target="#${userRef.uid}">
                        Подробнее...
                    </button>
                    <button type="button" class="btn btn-info btn-sm">Поделиться</button>
                    <!-- Modal -->
                    <div class="modal fade" id="${userRef.uid}" tabindex="-1" role="dialog"
                         aria-labelledby="${userRef.uid}Label" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="${userRef.uid}Label"><#if userRef.name??><a
                                            href="${userRef.url}">${userRef.name}</a><br><#else><a
                                            href="${userRef.url}">${userRef.url}<br></a></#if></h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <a href="${userRef.url}">${userRef.url}</a><br>
                                    <#if userRef.tag??>Теги: ${userRef.tag}<br></#if>
                                    <#if userRef.description??><b>Описание: </b>${userRef.description}<br></#if>
                                    <b>Уровень доступа: </b> <#if userRef.uidAccessLevel == 0>публичная
                                        <br><#else>личная
                                        <br></#if>
                                    <b>Тип: </b>
                                    <#if userRef.uidReferenceType == 0>текст
                                        <br><#elseif userRef.uidReferenceType == 1>видео
                                        <br>
                                    <#elseif userRef.uidReferenceType == 2>файл
                                        <br><#elseif userRef.uidReferenceType == 3>картинка
                                        <br></#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </td>
                <td><#if userRef.additionDate??>${userRef.additionDate}<br></#if></td>
                <td><a href="${userRef.source}">${userRef.source}</a></td>
                <td><#if userRef.rating??>${userRef.rating}<br></#if></td>
                <td>
                    <button type="button" class="btn btn-success btn-sm">Изменить</button>
                    <button type="button" class="btn btn btn-danger btn-sm">Удалить</button>
                    <br>
                </td>
            </tr>
        </#list>
    </#if>
    </table>
    </div>
    <@p.pager url page />
</@c.page>