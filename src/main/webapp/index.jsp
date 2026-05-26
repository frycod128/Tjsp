<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>数据管理</title>
    <style>
        * { box-sizing: border-box; }
        body { font-family: Arial, sans-serif; margin: 20px; font-size: 14px; }

        .collapse-header {
            background: #f0f0f0;
            padding: 10px;
            cursor: pointer;
            border: 1px solid #ddd;
            margin-top: 10px;
            font-weight: bold;
        }
        .collapse-header:hover {
            background: #e0e0e0;
        }
        .collapse-content {
            border: 1px solid #ddd;
            border-top: none;
            padding: 15px;
            display: none;
        }
        .collapse-content.show {
            display: block;
        }

        table {
            border-collapse: collapse;
            width: 100%;
            margin-top: 10px;
            table-layout: fixed;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            vertical-align: middle;
            word-wrap: break-word;
        }
        th {
            background-color: #f5f5f5;
            font-weight: bold;
        }

        .form-group {
            display: inline-block;
            margin-right: 15px;
            margin-bottom: 10px;
        }
        .form-group label {
            display: inline-block;
            width: 100px;
        }
        input, select {
            padding: 5px;
            width: 180px;
        }
        button {
            padding: 5px 15px;
            margin: 2px;
            cursor: pointer;
        }
        .error { color: red; }
        .success { color: green; }
        .message { margin: 10px 0; }

        .table-container {
            overflow-x: auto;
            margin-top: 20px;
        }

        .action-btns {
            white-space: nowrap;
            text-align: center;
        }
        .action-btns button {
            padding: 3px 8px;
            font-size: 12px;
        }

        .pagination {
            margin-top: 20px;
            text-align: center;
        }
        .pagination button {
            margin: 0 5px;
            padding: 5px 12px;
        }
        .pagination .page-info {
            margin: 0 15px;
            font-size: 14px;
        }
        .pagination input {
            width: 60px;
            text-align: center;
        }

        .page-size-select {
            margin-left: 20px;
            width: 70px;
        }

        /* 固定列宽 */
        .col-id { width: 5%; }
        .col-model { width: 12%; }
        .col-brand { width: 8%; }
        .col-driver { width: 8%; }
        .col-impedance { width: 7%; }
        .col-sensitivity { width: 7%; }
        .col-frequency { width: 10%; }
        .col-price { width: 8%; }
        .col-stock { width: 6%; }
        .col-wireless { width: 5%; }
        .col-noise { width: 5%; }
        .col-action { width: 10%; }

        .btn-cancel {
            background-color: #f0f0f0;
            border: 1px solid #ccc;
        }
        .btn-cancel:hover {
            background-color: #e0e0e0;
        }

        .search-row {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 10px;
        }
        .search-item {
            display: flex;
            align-items: center;
        }
        hr {
            margin: 15px 0;
        }
    </style>
</head>
<body>
<h2>数据管理</h2>

<div id="message" class="message"></div>

<!-- 查询区域 - 默认折叠 -->
<div>
    <div class="collapse-header" onclick="toggleCollapse('search')">▼ 查询</div>
    <div id="searchCollapse" class="collapse-content">
        <div class="search-row">
            <div class="search-item">
                <label>按ID查询:</label>
                <input type="number" id="searchId" placeholder="输入ID" style="width:100px">
                <button onclick="searchById()">查询</button>
            </div>
            <div class="search-item">
                <label>字段:</label>
                <select id="searchField">
                    <option value="model">型号</option>
                    <option value="brand">品牌</option>
                </select>
            </div>
            <div class="search-item">
                <label>关键字:</label>
                <input type="text" id="searchKeyword" placeholder="请输入">
            </div>
            <div class="search-item">
                <button onclick="search()">模糊查询</button>
                <button onclick="loadPage(1)">显示全部</button>
            </div>
        </div>
    </div>
</div>

<!-- 新增/修改区域 - 默认折叠 -->
<div>
    <div class="collapse-header" onclick="toggleCollapse('add')">▼ 新增</div>
    <div id="addCollapse" class="collapse-content">
        <input type="hidden" id="editId">
        <div class="form-group"><label>型号:</label><input type="text" id="model" placeholder="型号名称"></div>
        <div class="form-group"><label>品牌:</label><input type="text" id="brand" placeholder="品牌"></div>
        <div class="form-group"><label>驱动单元(mm):</label><input type="text" id="driverSize" placeholder="驱动单元"></div>
        <div class="form-group"><label>阻抗(Ω):</label><input type="text" id="impedance" placeholder="阻抗"></div>
        <div class="form-group"><label>灵敏度(dB):</label><input type="text" id="sensitivity" placeholder="灵敏度"></div>
        <div class="form-group"><label>频响范围:</label><input type="text" id="frequencyResponse" placeholder="频响范围"></div>
        <div class="form-group"><label>价格:</label><input type="text" id="price" placeholder="价格"></div>
        <div class="form-group"><label>库存:</label><input type="text" id="stock" placeholder="库存"></div>
        <div class="form-group"><label>无线:</label>
            <select id="wireless"><option value="0">否</option><option value="1">是</option></select>
        </div>
        <div class="form-group"><label>降噪:</label>
            <select id="noiseCancelling"><option value="0">否</option><option value="1">是</option></select>
        </div>
        <div>
            <button id="saveBtn" onclick="save()">保存</button>
            <button id="cancelBtn" class="btn-cancel" onclick="cancelEdit()" style="display:none;">取消</button>
            <button id="clearBtn" onclick="clearForm()">清空</button>
        </div>
    </div>
</div>

<!-- 数据表格 -->
<div class="table-container">
    <div id="dataTable"></div>
</div>

<!-- 分页区域 -->
<div class="pagination">
    <button onclick="loadPage(1)" ${currentPage == 1 ? 'disabled' : ''}>首页</button>
    <button onclick="loadPage(currentPage - 1)" ${currentPage == 1 ? 'disabled' : ''}>上一页</button>
    <span class="page-info">
        第 <span id="currentPage">1</span> / <span id="totalPages">1</span> 页
        (共 <span id="totalRecords">0</span> 条)
    </span>
    <button onclick="loadPage(currentPage + 1)" ${currentPage == totalPages ? 'disabled' : ''}>下一页</button>
    <button onclick="loadPage(totalPages)" ${currentPage == totalPages ? 'disabled' : ''}>末页</button>

    <span style="margin-left: 20px;">
        跳转到:
        <input type="number" id="gotoPage" min="1" style="width:60px">
        <button onclick="gotoPage()">GO</button>
    </span>

    <span style="margin-left: 20px;">
        每页显示:
        <select id="pageSizeSelect" onchange="changePageSize()" class="page-size-select">
            <option value="5">5</option>
            <option value="10" selected>10</option>
            <option value="20">20</option>
            <option value="50">50</option>
        </select>
        条
    </span>
</div>

<script>
    const API_BASE = '/api/headphone';

    // 分页变量
    let currentPage = 1;
    let pageSize = 10;
    let totalRecords = 0;
    let totalPages = 1;
    let allData = [];  // 缓存所有数据（用于前端分页）
    let isFiltered = false;  // 是否处于筛选状态
    let filteredData = [];  // 筛选后的数据

    function toggleCollapse(id) {
        const el = document.getElementById(id + 'Collapse');
        el.classList.toggle('show');
    }

    function showMessage(msg, type) {
        const div = document.getElementById('message');
        div.innerHTML = '<span class="' + type + '">' + msg + '</span>';
        setTimeout(() => { div.innerHTML = ''; }, 2000);
    }

    async function request(url, options) {
        try {
            const response = await fetch(url, options);
            return await response.json();
        } catch (e) {
            showMessage('请求失败: ' + e.message, 'error');
            return null;
        }
    }

    // 加载所有数据（用于前端分页）
    async function loadAllData() {
        const result = await request(API_BASE + '/all', { method: 'GET' });
        if (result && result.code === 200) {
            allData = result.data;
            isFiltered = false;
            totalRecords = allData.length;
            totalPages = Math.ceil(totalRecords / pageSize);
            updatePaginationInfo();
            renderPageData();
        }
    }

    // 按ID查询
    async function searchById() {
        const id = document.getElementById('searchId').value;
        if (!id) {
            showMessage('请输入ID', 'error');
            return;
        }
        const result = await request(API_BASE + '/' + id, { method: 'GET' });
        if (result && result.code === 200) {
            // 单条数据转为数组显示
            filteredData = [result.data];
            isFiltered = true;
            totalRecords = filteredData.length;
            totalPages = 1;
            currentPage = 1;
            updatePaginationInfo();
            renderTable(filteredData);
            showMessage('查询成功', 'success');
        } else {
            showMessage('未找到ID为 ' + id + ' 的记录', 'error');
        }
    }

    // 模糊查询（前端筛选）
    async function search() {
        const field = document.getElementById('searchField').value;
        const keyword = document.getElementById('searchKeyword').value.trim();

        if (!keyword) {
            loadAllData();
            return;
        }

        // 如果没有缓存数据，先加载
        if (allData.length === 0) {
            const result = await request(API_BASE + '/all', { method: 'GET' });
            if (result && result.code === 200) {
                allData = result.data;
            } else {
                return;
            }
        }

        filteredData = allData.filter(item => {
            const val = item[field] || '';
            return val.toString().toLowerCase().includes(keyword.toLowerCase());
        });

        isFiltered = true;
        totalRecords = filteredData.length;
        totalPages = Math.ceil(totalRecords / pageSize);
        currentPage = 1;
        updatePaginationInfo();
        renderPageData();
        showMessage('找到 ' + filteredData.length + ' 条记录', 'success');
    }

    // 渲染当前页数据
    function renderPageData() {
        const dataSource = isFiltered ? filteredData : allData;
        const start = (currentPage - 1) * pageSize;
        const end = start + pageSize;
        const pageData = dataSource.slice(start, end);
        renderTable(pageData);
    }

    // 加载指定页
    function loadPage(page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;
        updatePaginationInfo();
        renderPageData();
    }

    // 跳转到指定页
    function gotoPage() {
        const page = parseInt(document.getElementById('gotoPage').value);
        if (isNaN(page)) {
            showMessage('请输入有效的页码', 'error');
            return;
        }
        if (page < 1 || page > totalPages) {
            showMessage('页码范围: 1 - ' + totalPages, 'error');
            return;
        }
        loadPage(page);
        document.getElementById('gotoPage').value = '';
    }

    // 改变每页显示条数
    function changePageSize() {
        pageSize = parseInt(document.getElementById('pageSizeSelect').value);
        const dataSource = isFiltered ? filteredData : allData;
        totalRecords = dataSource.length;
        totalPages = Math.ceil(totalRecords / pageSize);
        currentPage = 1;
        updatePaginationInfo();
        renderPageData();
    }

    // 更新分页控件信息
    function updatePaginationInfo() {
        document.getElementById('currentPage').innerText = currentPage;
        document.getElementById('totalPages').innerText = totalPages;
        document.getElementById('totalRecords').innerText = totalRecords;

        // 更新按钮状态
        const btns = document.querySelectorAll('.pagination button');
        // 简单禁用处理
    }

    // 重置筛选状态
    function resetFilter() {
        isFiltered = false;
        document.getElementById('searchId').value = '';
        document.getElementById('searchKeyword').value = '';
        loadAllData();
    }

    async function save() {
        const id = document.getElementById('editId').value;
        const data = {
            model: document.getElementById('model').value,
            brand: document.getElementById('brand').value,
            driverSize: parseFloat(document.getElementById('driverSize').value) || null,
            impedance: parseInt(document.getElementById('impedance').value) || null,
            sensitivity: parseInt(document.getElementById('sensitivity').value) || null,
            frequencyResponse: document.getElementById('frequencyResponse').value,
            price: parseFloat(document.getElementById('price').value),
            stock: parseInt(document.getElementById('stock').value) || 0,
            wireless: parseInt(document.getElementById('wireless').value),
            noiseCancelling: parseInt(document.getElementById('noiseCancelling').value)
        };

        if (!data.model || !data.brand || !data.price || isNaN(data.price)) {
            showMessage('请填写型号、品牌和有效价格', 'error');
            return;
        }

        let url = API_BASE + '/*';
        let method = 'POST';
        if (id) {
            data.id = parseInt(id);
            method = 'PUT';
        }

        const result = await request(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (result && result.code === 200) {
            showMessage(id ? '更新成功' : '新增成功', 'success');
            cancelEdit();
            // 刷新数据
            await loadAllData();
            // 如果有筛选，重新应用筛选
            if (isFiltered && document.getElementById('searchKeyword').value) {
                await search();
            }
        } else {
            showMessage('保存失败: ' + (result ? result.msg : '未知错误'), 'error');
        }
    }

    function cancelEdit() {
        clearForm();
        const addHeader = document.querySelector('#addCollapse').parentElement.querySelector('.collapse-header');
        addHeader.innerHTML = '▼ 新增';
        document.getElementById('cancelBtn').style.display = 'none';
        document.getElementById('clearBtn').style.display = 'inline-block';
    }

    async function edit(id) {
        const addHeader = document.querySelector('#addCollapse').parentElement.querySelector('.collapse-header');
        addHeader.innerHTML = '▼ 修改 (ID: ' + id + ')';

        document.getElementById('cancelBtn').style.display = 'inline-block';
        document.getElementById('clearBtn').style.display = 'none';

        const result = await request(API_BASE + '/' + id, { method: 'GET' });
        if (result && result.code === 200) {
            const item = result.data;
            document.getElementById('editId').value = item.id;
            document.getElementById('model').value = item.model || '';
            document.getElementById('brand').value = item.brand || '';
            document.getElementById('driverSize').value = item.driverSize || '';
            document.getElementById('impedance').value = item.impedance || '';
            document.getElementById('sensitivity').value = item.sensitivity || '';
            document.getElementById('frequencyResponse').value = item.frequencyResponse || '';
            document.getElementById('price').value = item.price || '';
            document.getElementById('stock').value = item.stock || '';
            document.getElementById('wireless').value = item.wireless || 0;
            document.getElementById('noiseCancelling').value = item.noiseCancelling || 0;
            const addCollapse = document.getElementById('addCollapse');
            if (!addCollapse.classList.contains('show')) {
                addCollapse.classList.add('show');
            }
            document.getElementById('addCollapse').scrollIntoView({ behavior: 'smooth' });
        } else {
            showMessage('获取数据失败', 'error');
            cancelEdit();
        }
    }

    async function del(id) {
        if (!confirm('确定删除？')) return;
        const result = await request(API_BASE + '/' + id, { method: 'DELETE' });
        if (result && result.code === 200) {
            showMessage('删除成功', 'success');
            await loadAllData();
            if (isFiltered && document.getElementById('searchKeyword').value) {
                await search();
            }
        } else {
            showMessage('删除失败', 'error');
        }
    }

    function clearForm() {
        document.getElementById('editId').value = '';
        document.getElementById('model').value = '';
        document.getElementById('brand').value = '';
        document.getElementById('driverSize').value = '';
        document.getElementById('impedance').value = '';
        document.getElementById('sensitivity').value = '';
        document.getElementById('frequencyResponse').value = '';
        document.getElementById('price').value = '';
        document.getElementById('stock').value = '';
        document.getElementById('wireless').value = '0';
        document.getElementById('noiseCancelling').value = '0';
    }

    function renderTable(data) {
        if (!data || data.length === 0) {
            document.getElementById('dataTable').innerHTML = '<p>暂无数据</p>';
            return;
        }

        const columns = [
            { key: 'id', name: 'ID', class: 'col-id' },
            { key: 'model', name: '型号', class: 'col-model' },
            { key: 'brand', name: '品牌', class: 'col-brand' },
            { key: 'driverSize', name: '驱动单元(mm)', class: 'col-driver' },
            { key: 'impedance', name: '阻抗(Ω)', class: 'col-impedance' },
            { key: 'sensitivity', name: '灵敏度(dB)', class: 'col-sensitivity' },
            { key: 'frequencyResponse', name: '频响范围', class: 'col-frequency' },
            { key: 'price', name: '价格', class: 'col-price' },
            { key: 'stock', name: '库存', class: 'col-stock' },
            { key: 'wireless', name: '无线', class: 'col-wireless' },
            { key: 'noiseCancelling', name: '降噪', class: 'col-noise' }
        ];

        let html = '<table>';
        html += '<thead><tr>';
        for (const col of columns) {
            html += '<th class="' + col.class + '">' + col.name + '</th>';
        }
        html += '<th class="col-action">操作</th>';
        html += '</tr></thead><tbody>';

        for (const row of data) {
            html += '<tr>';
            for (const col of columns) {
                let val = row[col.key];
                if (col.key === 'wireless') {
                    val = val === 1 ? '是' : '否';
                } else if (col.key === 'noiseCancelling') {
                    val = val === 1 ? '是' : '否';
                } else if (val === null || val === undefined) {
                    val = '-';
                }
                html += '<td class="' + col.class + '">' + val + '</td>';
            }
            html += '<td class="col-action action-btns">';
            html += '<button onclick="edit(' + row.id + ')">编辑</button> ';
            html += '<button onclick="del(' + row.id + ')">删除</button>';
            html += '</td>';
            html += '</tr>';
        }
        html += '</tbody></table>';
        document.getElementById('dataTable').innerHTML = html;
    }

    // 初始化 - 加载数据
    loadAllData();
</script>
</body>
</html>