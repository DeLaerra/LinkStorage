<#assign
known = Session.SPRING_SECURITY_CONTEXT??
>

<#if known>
    <#assign
    user = Session.SPRING_SECURITY_CONTEXT.authentication.principal
    name = user.getUsername()
    userUid = user.getUid()
    isAdmin = user.isAdmin()
    >
<#else>
    <#assign
    name = "Анонимус"
    isAdmin = false
    >
</#if>