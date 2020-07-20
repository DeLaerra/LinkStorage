<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<#include "parts/security.ftlh">

<@c.page>
    <form name="search" action="/searchResult" method="GET">
        <div class="row">
            <div class="col">
                <input class="form-control mb-4" type="text" name="search" id="search" placeholder="Поиск ссылок">
            </div>
            <div class="col">
                <input type="checkbox" class="form-check-input" id="area" name="area" value="all">
                <label class="form-check-label" for="area">искать по ссылкам всех пользователей</label>
            </div>
        </div>
    </form>
    <#if page??>
        <div class="container">
        <table class="table table-striped">
        <thead>
        <tr>
           <form name="sorting" action="/searchResult" method="GET">
            <th scope="col">
                <button class="btn btn-light" type="submit" name="sortBy" value="nameDesc">
                    <span class="iconify" data-icon="gg:sort-az" data-inline="false"></span></button>
                <button class="btn btn-light" type="submit" name="sortBy" value="nameAsc">
                    <span class="iconify" data-icon="gg:sort-za" data-inline="false"></span></button>
                Имя</th>
            <th scope="col">
                <button class="btn btn-light" type="submit" name="sortBy" value="sourceDesc">
                    <span class="iconify" data-icon="gg:sort-az" data-inline="false"></span></button>
                <button class="btn btn-light" type="submit" name="sortBy" value="sourceAsc">
                    <span class="iconify" data-icon="gg:sort-za" data-inline="false"></span></button>
                Источник</th>
            <th scope="col">
                <button class="btn btn-light" type="submit" name="sortBy" value="ratingDesc">
                    <span class="iconify" data-icon="gg:sort-az" data-inline="false"></span></button>
                <button class="btn btn-light" type="submit" name="sortBy" value="ratingAsc">
                    <span class="iconify" data-icon="gg:sort-za" data-inline="false"></span></button>
                Рейтинг</th>
            </form>
        </tr>
        </thead>
        <#list page.content as userRef>
            <tr>
                <td> <#if userRef.name??><a href="${userRef.url}">${userRef.name}</a><br><#else><a
                        href="${userRef.url}">${userRef.url}<br></a></#if>
                    <#if userRef.tag??>Теги: ${userRef.tag}<br></#if>

                    <button type="button" class="btn btn-success btn-sm"><span class="iconify" data-icon="mdi:circle-edit-outline" data-inline="false"></span></button>
                    <button type="button" class="btn btn btn-danger btn-sm"><span class="iconify" data-icon="mdi:delete-circle-outline" data-inline="false"></span></button>
                    <button type="button" class="btn btn-info btn-sm"><span class="iconify" data-icon="el:download" data-inline="false"></span></button>
                    <!-- Button trigger modal -->
                    <button type="button" class="btn btn-light btn-sm" data-toggle="modal"
                    data-target="#${userRef.uid}"><span class="iconify" data-icon="ion:ellipsis-horizontal-circle" data-inline="false"></span>
                    </button>
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
                                    <#if userRef.tag??><b>Теги</b>: ${userRef.tag}<br></#if>
                                     <#if userRef.source??><b>Источник</b>: <a href="${userRef.source}">${userRef.source}</a><br></#if>
                                    <#if userRef.additionDate??><b>Дата добавления</b>: ${userRef.additionDate}<br></#if>
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
                                    <#if userRef.rating??><b>Рейтинг: </b>${userRef.rating}<br></#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </td>
                <td><#if userRef.source??><a href="${userRef.source}">${userRef.source}</a></#if></td>
                <td><#if userRef.rating??>${userRef.rating}</#if></td>
            </tr>
        </#list>
    </#if>
    </table>
    </div>
    <@p.pager url page />
</@c.page>