<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>通用数据管理系统</title>
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
            table-layout: auto;
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
        tr:hover {
            background-color: #f9f9f9;
        }

        .form-group {
            display: inline-block;
            margin-right: 15px;
            margin-bottom: 10px;
        }
        .form-group label {
            display: inline-block;
            width: 100px;
            font-weight: normal;
        }
        input, select, textarea {
            padding: 5px;
            width: 180px;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        textarea {
            width: 300px;
            height: 60px;
        }
        button {
            padding: 5px 15px;
            margin: 2px;
            cursor: pointer;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 3px;
        }
        button:hover {
            background-color: #45a049;
        }
        button.btn-cancel {
            background-color: #f0f0f0;
            color: #333;
            border: 1px solid #ccc;
        }
        button.btn-cancel:hover {
            background-color: #e0e0e0;
        }
        button.btn-delete {
            background-color: #f44336;
        }
        button.btn-delete:hover {
            background-color: #da190b;
        }
        button.btn-edit {
            background-color: #2196F3;
        }
        button.btn-page {
            background-color: #607D8B;
            padding: 3px 10px;
            margin: 0 2px;
        }
        button.btn-page.active {
            background-color: #4CAF50;
        }
        button.btn-page:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        .error { color: red; }
        .success { color: green; }
        .message { margin: 10px 0; padding: 10px; border-radius: 3px; }

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
            margin: 0 2px;
        }

        .loading {
            text-align: center;
            padding: 20px;
            color: #666;
        }

        .table-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
            flex-wrap: wrap;
            gap: 10px;
        }

        .pagination-controls {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .page-size-select {
            width: 80px;
            padding: 3px;
        }

        .page-info {
            color: #666;
            font-size: 14px;
        }

        .query-section {
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }

        .query-group {
            display: flex;
            align-items: center;
            gap: 5px;
            border: 1px solid #ddd;
            padding: 5px 10px;
            border-radius: 5px;
            background: #f9f9f9;
        }

        .query-group label {
            font-weight: bold;
            margin: 0;
            width: auto;
        }

        .query-group input {
            width: 150px;
            margin: 0;
        }

        hr {
            margin: 10px 0;
            border: none;
            border-top: 1px solid #eee;
        }
    </style>
</head>
<body>
<h2 id="pageTitle">通用数据管理系统</h2>

<div id="message" class="message"></div>

<!-- 查询区域 -->
<div>
    <div class="collapse-header" onclick="toggleCollapse('search')">▼ 高级查询</div>
    <div id="searchCollapse" class="collapse-content">
        <div class="query-section">
            <div class="query-group">
                <label>商品编号:</label>
                <input type="text" id="searchById" placeholder="精确查询ID">
                <button onclick="searchById()">查询</button>
            </div>
            <div class="query-group">
                <label>商品名称:</label>
                <input type="text" id="searchByName" placeholder="模糊查询">
                <button onclick="searchByName()">查询</button>
            </div>
            <div class="query-group">
                <label>查询字段:</label>
                <select id="searchField"></select>
                <input type="text" id="searchKeyword" placeholder="请输入关键字">
                <button onclick="search()">查询</button>
            </div>
            <button onclick="loadPage(1)">显示全部</button>
        </div>
    </div>
</div>

<!-- 新增/修改区域 -->
<div>
    <div class="collapse-header" onclick="toggleCollapse('add')">▼ 新增</div>
    <div id="addCollapse" class="collapse-content">
        <input type="hidden" id="editId">
        <div id="formFields"></div>
        <div style="margin-top: 10px;">
            <button id="saveBtn" onclick="save()">保存</button>
            <button id="cancelBtn" class="btn-cancel" onclick="cancelEdit()" style="display:none;">取消</button>
            <button id="clearBtn" onclick="clearForm()">清空</button>
        </div>
    </div>
</div>

<!-- 数据表格 -->
<div class="table-container">
    <div class="table-header">
        <div class="pagination-controls">
            <span class="page-info" id="recordCount">共 0 条记录</span>
            <select id="pageSizeSelect" class="page-size-select" onchange="changePageSize()">
                <option value="5">5条/页</option>
                <option value="10" selected>10条/页</option>
                <option value="20">20条/页</option>
                <option value="50">50条/页</option>
            </select>
            <button class="refresh-btn" onclick="loadPage(1)">刷新</button>
        </div>
        <div id="pagination" class="pagination-controls"></div>
    </div>
    <div id="dataTable">
        <div class="loading">加载中...</div>
    </div>
</div>

<script>
    const API_BASE = '/api';
    let tableConfig = null;
    let allColumns = [];
    let columnLabels = {};
    let editableColumns = [];
    let primaryKey = 'id';

    // 分页相关变量
    let currentPage = 1;
    let pageSize = 10;
    let totalRecords = 0;
    let allData = [];
    let currentQueryType = 'all'; // 'all', 'byId', 'byName', 'search'
    let currentQueryParams = {};

    // 页面加载时获取配置和表结构
    async function loadConfig() {
        try {
            const columnsResp = await fetch(API_BASE + '/columns');
            const columnsResult = await columnsResp.json();
            if (columnsResult && columnsResult.code === 200) {
                allColumns = columnsResult.data;
                for (const col of allColumns) {
                    let label = col.COLUMN_COMMENT;
                    if (!label || label === '') {
                        label = col.COLUMN_NAME;
                    }
                    columnLabels[col.COLUMN_NAME] = label;
                }
            }

            const configResp = await fetch(API_BASE + '/config');
            const configResult = await configResp.json();
            if (configResult && configResult.code === 200) {
                tableConfig = configResult.data;
                primaryKey = tableConfig.primaryKey || 'id';
                document.getElementById('pageTitle').innerText = tableConfig.tableName + ' - 数据管理系统';

                editableColumns = tableConfig.editableColumns || allColumns.map(c => c.COLUMN_NAME);
                editableColumns = editableColumns.filter(col =>
                    col !== primaryKey && col !== 'create_time' && col !== 'update_time'
                );

                buildSearchFields();
                buildFormFields();
                return true;
            }
        } catch (e) {
            console.error('加载配置失败:', e);
            showMessage('加载配置失败: ' + e.message, 'error');
        }
        return false;
    }

    // 构建查询字段下拉框
    function buildSearchFields() {
        const searchField = document.getElementById('searchField');
        searchField.innerHTML = '';
        const searchableCols = tableConfig.searchableColumns || allColumns.map(c => c.COLUMN_NAME);

        for (const col of searchableCols) {
            if (col !== primaryKey && col !== 'create_time') {
                const option = document.createElement('option');
                option.value = col;
                option.textContent = columnLabels[col] || col;
                searchField.appendChild(option);
            }
        }
    }

    // 构建表单字段
    function buildFormFields() {
        const container = document.getElementById('formFields');
        container.innerHTML = '';

        for (const col of editableColumns) {
            const label = columnLabels[col] || col;
            const formTypes = tableConfig.formTypes || {};
            const formType = formTypes[col];

            const div = document.createElement('div');
            div.className = 'form-group';

            const labelEl = document.createElement('label');
            labelEl.textContent = label + ':';
            div.appendChild(labelEl);

            let inputEl;
            if (formType && formType.startsWith('select:')) {
                inputEl = document.createElement('select');
                const options = formType.substring(7).split(',');
                for (const opt of options) {
                    const parts = opt.split('=');
                    const val = parts[0];
                    const text = parts[1] || val;
                    const option = document.createElement('option');
                    option.value = val;
                    option.textContent = text;
                    inputEl.appendChild(option);
                }
            } else if (formType === 'textarea') {
                inputEl = document.createElement('textarea');
            } else if (formType === 'number') {
                inputEl = document.createElement('input');
                inputEl.type = 'number';
                inputEl.step = 'any';
            } else if (formType === 'date') {
                inputEl = document.createElement('input');
                inputEl.type = 'date';
            } else {
                inputEl = document.createElement('input');
                inputEl.type = 'text';
            }

            inputEl.id = 'field_' + col;
            inputEl.placeholder = label;
            div.appendChild(inputEl);
            container.appendChild(div);
        }
    }

    function toggleCollapse(id) {
        const el = document.getElementById(id + 'Collapse');
        if (el) {
            el.classList.toggle('show');
        }
    }

    function showMessage(msg, type) {
        const div = document.getElementById('message');
        div.innerHTML = '<span class="' + type + '">' + msg + '</span>';
        setTimeout(() => { div.innerHTML = ''; }, 3000);
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

    // 根据ID精确查询
    async function searchById() {
        const id = document.getElementById('searchById').value.trim();
        if (!id) {
            showMessage('请输入商品编号', 'error');
            return;
        }

        const result = await request(API_BASE + '/' + id, { method: 'GET' });
        if (result && result.code === 200) {
            allData = [result.data];
            totalRecords = 1;
            currentPage = 1;
            currentQueryType = 'byId';
            currentQueryParams = { id: id };
            renderTable(allData);
            updatePagination();
            document.getElementById('recordCount').innerHTML = '找到 1 条记录';
        } else {
            showMessage('未找到编号为 ' + id + ' 的记录', 'error');
            allData = [];
            totalRecords = 0;
            renderTable([]);
            updatePagination();
            document.getElementById('recordCount').innerHTML = '共 0 条记录';
        }
    }

    // 根据商品名模糊查询
    async function searchByName() {
        const keyword = document.getElementById('searchByName').value.trim();
        if (!keyword) {
            showMessage('请输入商品名称关键字', 'error');
            return;
        }

        // 使用model字段进行模糊查询
        const result = await request(API_BASE + '/search?field=model&keyword=' + encodeURIComponent(keyword), { method: 'GET' });
        if (result && result.code === 200) {
            allData = result.data;
            totalRecords = allData.length;
            currentPage = 1;
            currentQueryType = 'byName';
            currentQueryParams = { keyword: keyword };
            renderTable(getPageData());
            updatePagination();
            document.getElementById('recordCount').innerHTML = '找到 ' + totalRecords + ' 条记录';
            showMessage('找到 ' + totalRecords + ' 条包含 "' + keyword + '" 的记录', 'success');
        } else {
            showMessage('查询失败', 'error');
        }
    }

    // 通用搜索
    async function search() {
        const field = document.getElementById('searchField').value;
        const keyword = document.getElementById('searchKeyword').value.trim();

        if (!keyword) {
            loadPage(1);
            return;
        }

        const result = await request(API_BASE + '/search?field=' + encodeURIComponent(field) + '&keyword=' + encodeURIComponent(keyword), { method: 'GET' });
        if (result && result.code === 200) {
            allData = result.data;
            totalRecords = allData.length;
            currentPage = 1;
            currentQueryType = 'search';
            currentQueryParams = { field: field, keyword: keyword };
            renderTable(getPageData());
            updatePagination();
            document.getElementById('recordCount').innerHTML = '找到 ' + totalRecords + ' 条记录';
            showMessage('找到 ' + totalRecords + ' 条记录', 'success');
        }
    }

    // 加载全部数据（分页）
    async function loadPage(page) {
        if (!tableConfig) {
            await loadConfig();
        }

        currentPage = page;
        currentQueryType = 'all';
        currentQueryParams = {};

        const result = await request(API_BASE + '/all', { method: 'GET' });
        if (result && result.code === 200) {
            allData = result.data;
            totalRecords = allData.length;
            renderTable(getPageData());
            updatePagination();
            document.getElementById('recordCount').innerHTML = '共 ' + totalRecords + ' 条记录';
        }
    }

    // 获取当前页的数据
    function getPageData() {
        const start = (currentPage - 1) * pageSize;
        const end = start + pageSize;
        return allData.slice(start, end);
    }

    // 更新分页控件
    function updatePagination() {
        const totalPages = Math.ceil(totalRecords / pageSize);
        const paginationDiv = document.getElementById('pagination');

        if (totalPages <= 1) {
            paginationDiv.innerHTML = '';
            return;
        }

        let html = '';

        // 上一页
        html += '<button class="btn-page" onclick="goToPage(' + (currentPage - 1) + ')" ' + (currentPage <= 1 ? 'disabled' : '') + '>上一页</button>';

        // 页码按钮
        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, currentPage + 2);

        if (startPage > 1) {
            html += '<button class="btn-page" onclick="goToPage(1)">1</button>';
            if (startPage > 2) html += '<span>...</span>';
        }

        for (let i = startPage; i <= endPage; i++) {
            html += '<button class="btn-page ' + (i === currentPage ? 'active' : '') + '" onclick="goToPage(' + i + ')">' + i + '</button>';
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) html += '<span>...</span>';
            html += '<button class="btn-page" onclick="goToPage(' + totalPages + ')">' + totalPages + '</button>';
        }

        // 下一页
        html += '<button class="btn-page" onclick="goToPage(' + (currentPage + 1) + ')" ' + (currentPage >= totalPages ? 'disabled' : '') + '>下一页</button>';

        paginationDiv.innerHTML = html;
    }

    // 跳转到指定页
    function goToPage(page) {
        if (page < 1 || page > Math.ceil(totalRecords / pageSize)) return;
        currentPage = page;

        if (currentQueryType === 'all') {
            renderTable(getPageData());
        } else if (currentQueryType === 'byId') {
            // ID查询不分页
            renderTable(allData);
        } else {
            renderTable(getPageData());
        }
        updatePagination();

        // 滚动到表格顶部
        document.querySelector('.table-container').scrollIntoView({ behavior: 'smooth' });
    }

    // 修改每页显示记录数
    function changePageSize() {
        pageSize = parseInt(document.getElementById('pageSizeSelect').value);
        currentPage = 1;

        if (currentQueryType === 'all') {
            renderTable(getPageData());
        } else if (currentQueryType === 'byId') {
            renderTable(allData);
        } else {
            renderTable(getPageData());
        }
        updatePagination();
    }

    async function save() {
        const id = document.getElementById('editId').value;
        const data = {};

        for (const col of editableColumns) {
            const inputEl = document.getElementById('field_' + col);
            if (inputEl) {
                let value = inputEl.value;
                if (value !== '') {
                    data[col] = value;
                }
            }
        }

        let url = API_BASE + '/*';
        let method = 'POST';
        if (id) {
            data[primaryKey] = parseInt(id);
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
            loadPage(1);
        } else {
            showMessage('保存失败: ' + (result ? result.msg : '未知错误'), 'error');
        }
    }

    function cancelEdit() {
        clearForm();
        const addHeader = document.querySelector('#addCollapse')?.parentElement?.querySelector('.collapse-header');
        if (addHeader) addHeader.innerHTML = '▼ 新增';
        const cancelBtn = document.getElementById('cancelBtn');
        const clearBtn = document.getElementById('clearBtn');
        if (cancelBtn) cancelBtn.style.display = 'none';
        if (clearBtn) clearBtn.style.display = 'inline-block';
        document.getElementById('editId').value = '';
    }

    async function edit(id) {
        const addHeader = document.querySelector('#addCollapse')?.parentElement?.querySelector('.collapse-header');
        if (addHeader) addHeader.innerHTML = '▼ 修改 (ID: ' + id + ')';

        const cancelBtn = document.getElementById('cancelBtn');
        const clearBtn = document.getElementById('clearBtn');
        if (cancelBtn) cancelBtn.style.display = 'inline-block';
        if (clearBtn) clearBtn.style.display = 'none';
        document.getElementById('editId').value = id;

        const result = await request(API_BASE + '/' + id, { method: 'GET' });
        if (result && result.code === 200) {
            const item = result.data;
            for (const col of editableColumns) {
                const inputEl = document.getElementById('field_' + col);
                if (inputEl && item[col] !== undefined && item[col] !== null) {
                    inputEl.value = item[col];
                }
            }
            const addCollapse = document.getElementById('addCollapse');
            if (addCollapse && !addCollapse.classList.contains('show')) {
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
            loadPage(1);
        } else {
            showMessage('删除失败', 'error');
        }
    }

    function clearForm() {
        for (const col of editableColumns) {
            const inputEl = document.getElementById('field_' + col);
            if (inputEl) {
                inputEl.value = '';
                if (inputEl.type === 'select-one') {
                    inputEl.selectedIndex = 0;
                }
            }
        }
    }

    // 清空查询条件
    function clearSearch() {
        document.getElementById('searchById').value = '';
        document.getElementById('searchByName').value = '';
        document.getElementById('searchKeyword').value = '';
        loadPage(1);
    }

    function renderTable(data) {
        if (!data || data.length === 0) {
            document.getElementById('dataTable').innerHTML = '<p>暂无数据</p>';
            return;
        }

        const displayColumns = allColumns.filter(col =>
            col.COLUMN_NAME !== 'create_time' &&
            col.DATA_TYPE !== 'text' &&
            col.DATA_TYPE !== 'longtext'
        ).map(col => col.COLUMN_NAME);

        let html = '<table>';
        html += '<thead><tr>';
        for (const col of displayColumns) {
            const label = columnLabels[col] || col;
            html += '<th>' + label + '</th>';
        }
        html += '<th>操作</th>';
        html += '</tr></thead><tbody>';

        for (const row of data) {
            html += '<tr>';
            for (const col of displayColumns) {
                let val = row[col];
                if (val === null || val === undefined) {
                    val = '-';
                }
                if (typeof val === 'string' && val.length > 50) {
                    val = val.substring(0, 50) + '...';
                }
                html += '<td>' + val + '</td>';
            }
            html += '<td class="action-btns">';
            html += '<button class="btn-edit" onclick="edit(' + row[primaryKey] + ')">编辑</button> ';
            html += '<button class="btn-delete" onclick="del(' + row[primaryKey] + ')">删除</button>';
            html += '</td>';
            html += '</tr>';
        }
        html += '</tbody></table>';
        document.getElementById('dataTable').innerHTML = html;
    }

    // 初始化
    (async function() {
        await loadConfig();
        await loadPage(1);
    })();
</script>
</body>
</html>