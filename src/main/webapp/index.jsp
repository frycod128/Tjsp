<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>耳机商城查询</title>
    <style>
        body { font-family: sans-serif; margin: 30px 40px; }
        h2 { border-bottom: 2px solid #ccc; padding-bottom: 6px; margin-top: 36px; }
        .wrap { position: relative; display: inline-block; }
        input[type=text], input[type=number] { padding: 5px 8px; font-size: 14px; }
        select { padding: 5px 8px; font-size: 14px; }
        button { padding: 6px 14px; font-size: 14px; cursor: pointer; }
        table { border-collapse: collapse; margin-top: 14px; width: 100%; max-width: 960px; }
        th, td { border: 1px solid #999; padding: 7px 10px; text-align: center; font-size: 14px; }
        th { background: #eee; }
        .msg { color: #c00; margin-top: 10px; }
        .row { display: flex; flex-wrap: wrap; gap: 10px 24px; align-items: center; margin: 8px 0; }
        .row label { font-size: 14px; white-space: nowrap; }
        .row input[type=number] { width: 80px; }
        .suggest-box {
            display: none; position: absolute; top: 100%; left: 0; right: 0;
            border: 1px solid #bbb; border-top: none; background: #fff;
            z-index: 99; max-height: 180px; overflow-y: auto;
        }
        .suggest-box .item {
            padding: 7px 10px; cursor: pointer; border-bottom: 1px solid #eee; font-size: 14px;
        }
        .suggest-box .item:hover { background: #eef4ff; }
        hr { margin: 24px 0 16px; border: none; border-top: 1px dashed #ccc; }
    </style>
</head>
<body>

<!-- ==================== 模块一：手机号查购买记录 ==================== -->
<h2>① 手机号查购买记录</h2>
<form action="query" method="get" id="queryForm">
    <div class="wrap">
        <input type="text" id="phoneInput" name="phone"
               placeholder="输入手机号，自动匹配" autocomplete="off"
               value="${phone}" style="width:220px;" />
        <div class="suggest-box" id="suggestBox"></div>
    </div>
    <button type="submit">查询</button>
</form>

<c:if test="${not empty msg}"><div class="msg">${msg}</div></c:if>

<c:if test="${not empty records}">
    <table>
        <tr><th>用户名</th><th>手机号</th><th>耳机型号</th><th>品牌</th><th>单价(元)</th><th>数量</th><th>下单时间</th></tr>
        <c:forEach items="${records}" var="r">
            <tr>
                <td>${r.username}</td><td>${r.phone}</td><td>${r.model}</td>
                <td>${r.brand}</td><td>${r.price}</td><td>${r.quantity}</td>
                <td>${r.orderTime}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<hr>

<!-- ==================== 模块二：动态SQL多条件查商品 ==================== -->
<h2>② 动态条件查询商品（AND运算，填几个都行）</h2>
<form action="headphoneSearch" method="get">
    <div class="row">
        <label>型号: <input type="text" name="model" /></label>
        <label>品牌: <input type="text" name="brand" /></label>
        <label>频响: <input type="text" name="frequencyResponse" placeholder="如 20Hz-20kHz" /></label>
    </div>
    <div class="row">
        <label>驱动单元(mm): <input type="number" name="driverSize" step="0.1" /></label>
        <label>阻抗(Ω): <input type="number" name="impedance" /></label>
        <label>灵敏度(dB): <input type="number" name="sensitivity" /></label>
    </div>
    <div class="row">
        <label>最低价: <input type="number" name="priceMin" step="0.01" /></label>
        <label>最高价: <input type="number" name="priceMax" step="0.01" /></label>
        <label>库存≥: <input type="number" name="stock" /></label>
    </div>
    <div class="row">
        <label>无线:
            <select name="wireless">
                <option value="">不限</option>
                <option value="1">是</option>
                <option value="0">否</option>
            </select>
        </label>
        <label>降噪:
            <select name="noiseCancelling">
                <option value="">不限</option>
                <option value="1">是</option>
                <option value="0">否</option>
            </select>
        </label>
        <button type="submit">搜商品</button>
    </div>
</form>

<c:if test="${not empty headphoneMsg}"><div class="msg">${headphoneMsg}</div></c:if>

<c:if test="${not empty headphones}">
    <table>
        <tr><th>ID</th><th>型号</th><th>品牌</th><th>驱动(mm)</th><th>阻抗(Ω)</th><th>灵敏度(dB)</th><th>频响</th><th>价格</th><th>库存</th><th>无线</th><th>降噪</th></tr>
        <c:forEach items="${headphones}" var="h">
            <tr>
                <td>${h.id}</td><td>${h.model}</td><td>${h.brand}</td>
                <td>${h.driverSize}</td><td>${h.impedance}</td><td>${h.sensitivity}</td>
                <td>${h.frequencyResponse}</td><td>${h.price}</td><td>${h.stock}</td>
                <td>${h.wireless == 1 ? '是' : '否'}</td><td>${h.noiseCancelling == 1 ? '是' : '否'}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<hr>

<!-- ==================== 模块三：批量商品名模糊查询 ==================== -->
<h2>③ 批量商品名模糊查询（OR运算，多个名用逗号/空格分隔）</h2>
<form action="batchModel" method="get">
    <input type="text" name="models" placeholder="如: XM5, K371, M50x"
           value="${batchInput}" style="width:360px;" />
    <button type="submit">批量搜</button>
</form>

<c:if test="${not empty batchMsg}"><div class="msg">${batchMsg}</div></c:if>

<c:if test="${not empty batchResults}">
    <table>
        <tr><th>ID</th><th>型号</th><th>品牌</th><th>驱动(mm)</th><th>阻抗(Ω)</th><th>灵敏度(dB)</th><th>频响</th><th>价格</th><th>库存</th><th>无线</th><th>降噪</th></tr>
        <c:forEach items="${batchResults}" var="h">
            <tr>
                <td>${h.id}</td><td>${h.model}</td><td>${h.brand}</td>
                <td>${h.driverSize}</td><td>${h.impedance}</td><td>${h.sensitivity}</td>
                <td>${h.frequencyResponse}</td><td>${h.price}</td><td>${h.stock}</td>
                <td>${h.wireless == 1 ? '是' : '否'}</td><td>${h.noiseCancelling == 1 ? '是' : '否'}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<!-- ==================== 手机号建议脚本（模块一） ==================== -->
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
            if (!users || users.length === 0) { hideBox(); return; }
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
                        if (input.value.trim().length === 0) return;
                        render(Array.isArray(data) ? data : []);
                    })
                    .catch(function(){ hideBox(); });
            }, 300);
        });

        document.addEventListener('click', function(e){
            if (!input.contains(e.target) && !box.contains(e.target)) hideBox();
        });

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
