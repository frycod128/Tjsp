<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>耳机管理系统</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        .btn { padding: 5px 10px; margin: 2px; cursor: pointer; }
        .search-box { margin: 10px 0; }
        input, select { padding: 5px; margin: 5px; }
    </style>
</head>
<body>
<h1>头戴式耳机管理系统</h1>

<div class="search-box">
    <button class="btn" onclick="loadAll()">全部耳机</button>
    <button class="btn" onclick="loadWireless()">无线耳机</button>
    <button class="btn" onclick="loadNoise()">降噪耳机</button>
    <button class="btn" onclick="loadBrands()">品牌统计</button>
</div>

<div id="result"></div>

<script>
    async function loadAll() {
        const response = await fetch('/api/headphone/all');
        const data = await response.json();
        displayResult(data.data);
    }

    async function loadWireless() {
        const response = await fetch('/api/headphone/wireless');
        const data = await response.json();
        displayResult(data.data);
    }

    async function loadNoise() {
        const response = await fetch('/api/headphone/noise');
        const data = await response.json();
        displayResult(data.data);
    }

    async function loadBrands() {
        const response = await fetch('/api/headphone/brands');
        const data = await response.json();
        displayBrandStats(data.data);
    }

    function displayResult(headphones) {
        if (!headphones || headphones.length === 0) {
            document.getElementById('result').innerHTML = '<p>暂无数据</p>';
            return;
        }

        let html = '<table>';
        html += '<tr><th>ID</th><th>型号</th><th>品牌</th><th>驱动单元</th><th>阻抗</th><th>灵敏度</th><th>频响范围</th><th>价格</th><th>库存</th><th>无线</th><th>降噪</th></tr>';

        for (const h of headphones) {
            html += `<tr>
                            <td>${h.id}</td>
                            <td>${h.model}</td>
                            <td>${h.brand}</td>
                            <td>${h.driverSize}mm</td>
                            <td>${h.impedance}Ω</td>
                            <td>${h.sensitivity}dB</td>
                            <td>${h.frequencyResponse}</td>
                            <td>¥${h.price}</td>
                            <td>${h.stock}</td>
                            <td>${h.wirelessStr}</td>
                            <td>${h.noiseCancellingStr}</td>
                         </tr>`;
        }
        html += '</table>';
        document.getElementById('result').innerHTML = html;
    }

    function displayBrandStats(stats) {
        if (!stats || stats.length === 0) {
            document.getElementById('result').innerHTML = '<p>暂无数据</p>';
            return;
        }

        let html = '<table>';
        html += '<tr><th>品牌</th><th>产品数量</th><th>平均价格</th></tr>';
        for (const s of stats) {
            html += `<tr>
                            <td>${s.brand}</td>
                            <td>${s.count}</td>
                            <td>¥${parseFloat(s.avgPrice).toFixed(2)}</td>
                         </tr>`;
        }
        html += '</table>';
        document.getElementById('result').innerHTML = html;
    }

    // 页面加载时显示所有耳机
    loadAll();
</script>
</body>
</html>