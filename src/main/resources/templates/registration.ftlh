<#import "parts/common.ftlh" as c>
<#import "parts/login.ftlh" as l>

<@c.page>
    ${reference?ifExists}
    <@l.login "/registration" true />
</@c.page>