<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户购买记录查询</title>
    <style>
        body { font-family: sans-serif; margin: 40px; }
        .wrap { position: relative; display: inline-block; }
        input[type=text] { width: 220px; padding: 6px 10px; font-size: 15px; }
        button { padding: 7px 16px; font-size: 15px; cursor: pointer; }
        table { border-collapse: collapse; margin-top: 20px; width: 100%; max-width: 850px; }
        th, td { border: 1px solid #999; padding: 8px 12px; text-align: center; }
        th { background: #eee; }
        .msg { color: #c00; margin-top: 16px; }

        /* 建议下拉 */
        .suggest-box {
            display: none;
            position: absolute; top: 100%; left: 0; right: 0;
            border: 1px solid #bbb; border-top: none;
            background: #fff; z-index: 99; max-height: 200px; overflow-y: auto;
        }
        .suggest-box .item {
            padding: 8px 10px; cursor: pointer; border-bottom: 1px solid #eee;
        }
        .suggest-box .item:hover, .suggest-box .item.active { background: #eef4ff; }
    </style>
</head>
<body>

<h2>根据手机号查询购买记录</h2>

<form action="query" method="get" id="queryForm">
    <div class="wrap">
        <input type="text" id="phoneInput" name="phone"
               placeholder="输入手机号，自动匹配" autocomplete="off"
               value="${phone}" />
        <div class="suggest-box" id="suggestBox"></div>
    </div>
    <button type="submit">查询</button>
</form>

<c:if test="${not empty msg}">
    <div class="msg">${msg}</div>
</c:if>

<c:if test="${not empty records}">
    <table>
        <tr>
            <th>用户名</th><th>手机号</th><th>耳机型号</th>
            <th>品牌</th><th>单价(元)</th><th>数量</th><th>下单时间</th>
        </tr>
        <c:forEach items="${records}" var="r">
            <tr>
                <td>${r.username}</td><td>${r.phone}</td><td>${r.model}</td>
                <td>${r.brand}</td><td>${r.price}</td><td>${r.quantity}</td>
                <td>${r.orderTime}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<script>
    (function(){
        var input  = document.getElementById('phoneInput');
        var box    = document.getElementById('suggestBox');
        var form   = document.getElementById('queryForm');
        var timer  = null;
        var items  = [];

        function hideBox() { box.style.display = 'none'; box.innerHTML = ''; items = []; }

        function render(users) {
            box.innerHTML = '';
            items = users;
            if (users.length === 0) { hideBox(); return; }
            users.forEach(function(u, i){
                var div = document.createElement('div');
                div.className = 'item';
                div.textContent = u.username + ' — ' + u.phone;
                div.onmousedown = function(e){ e.preventDefault(); select(i); };
                box.appendChild(div);
            });
            box.style.display = 'block';
        }

        function select(idx) {
            var u = items[idx];
            if (!u) return;
            input.value = u.phone;
            hideBox();
            form.submit();
        }

        input.addEventListener('input', function(){
            clearTimeout(timer);
            var val = input.value.trim();
            if (val.length === 0) { hideBox(); return; }
            timer = setTimeout(function(){
                fetch('/suggest?keyword=' + encodeURIComponent(val))
                    .then(function(r){ return r.json(); })
                    .then(function(data){
                        /* 只在输入框值仍匹配时渲染，避免竞态 */
                        if (input.value.trim().length === 0) return;
                        render(Array.isArray(data) ? data : []);
                    })
                    .catch(function(){ hideBox(); });
            }, 300);
        });

        /* 点击空白处收起 */
        document.addEventListener('click', function(e){
            if (!input.contains(e.target) && !box.contains(e.target)) hideBox();
        });

        /* 已有值聚焦时重新拉建议 */
        input.addEventListener('focus', function(){
            var val = input.value.trim();
            if (val.length === 0) return;
            clearTimeout(timer);
            timer = setTimeout(function(){
                fetch('/suggest?keyword=' + encodeURIComponent(val))
                    .then(function(r){ return r.json(); })
                    .then(function(data){
                        if (input.value.trim().length === 0) return;
                        render(Array.isArray(data) ? data : []);
                    })
                    .catch(function(){ hideBox(); });
            }, 150);
        });
    })();
</script>

</body>
</html>
