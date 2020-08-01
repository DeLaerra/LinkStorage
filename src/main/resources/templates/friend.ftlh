<#import "parts/common.ftlh" as c>

<@c.page>
    <h5>${friend.uid}</h5>
    <form name="addFriend" action="/addFriend" method="get">
        <input class="form-control" type="hidden" name="addFriend" id="addFriend" value="${friend.uid}">
        <button type="submit" class="btn btn-primary">Добавить</button>
    </form>
</@c.page>