<#import "parts/common.ftlh" as c>
<#import "parts/pager.ftlh" as p>
<#include "parts/security.ftlh">

<@c.page>
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
    <@p.pager url page />
    <#if page??>
        <#list page.content as userRef>
            <#if userRef.name??>
                <a href="${userRef.url}">${userRef.name}</a><br>
            <#else><a href="${userRef.url}">${userRef.url}</a><br>
            </#if>
        </#list>
    </#if>
    <@p.pager url page />
</@c.page>