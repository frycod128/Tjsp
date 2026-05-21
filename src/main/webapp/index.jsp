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
        }

        .refresh-btn {
            background-color: #607D8B;
        }

        .search-field-select {
            width: 150px;
        }
    </style>
</head>
<body>
<h2 id="pageTitle">通用数据管理系统</h2>

<div id="message" class="message"></div>

<!-- 查询区域 -->
<div>
    <div class="collapse-header" onclick="toggleCollapse('search')">▼ 查询</div>
    <div id="searchCollapse" class="collapse-content">
        <div class="form-group">
            <label>查询字段:</label>
            <select id="searchField" class="search-field-select"></select>
        </div>
        <div class="form-group">
            <label>关键字:</label>
            <input type="text" id="searchKeyword" placeholder="请输入">
        </div>
        <button onclick="search()">查询</button>
        <button onclick="loadAll()">显示全部</button>
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
        <span id="recordCount"></span>
        <button class="refresh-btn" onclick="loadAll()">刷新</button>
    </div>
    <div id="dataTable">
        <div class="loading">加载中...</div>
    </div>
</div>

<script>
    const API_BASE = '/api';
    let tableConfig = null;
    let allColumns = [];  // 存储所有列名
    let columnLabels = {}; // 列名到显示名的映射
    let editableColumns = []; // 可编辑列
    let primaryKey = 'id'; // 主键名

    // 页面加载时获取配置和表结构
    async function loadConfig() {
        try {
            // 获取表结构
            const columnsResp = await fetch(API_BASE + '/columns');
            const columnsResult = await columnsResp.json();
            if (columnsResult && columnsResult.code === 200) {
                allColumns = columnsResult.data;
                // 构建列名到显示名的映射
                for (const col of allColumns) {
                    let label = col.COLUMN_COMMENT;
                    if (!label || label === '') {
                        label = col.COLUMN_NAME;
                    }
                    columnLabels[col.COLUMN_NAME] = label;
                }
            }

            // 获取配置
            const configResp = await fetch(API_BASE + '/config');
            const configResult = await configResp.json();
            if (configResult && configResult.code === 200) {
                tableConfig = configResult.data;
                primaryKey = tableConfig.primaryKey || 'id';
                document.getElementById('pageTitle').innerText = tableConfig.tableName + ' - 数据管理系统';

                // 使用配置中的可编辑列，如果没有则使用所有列
                editableColumns = tableConfig.editableColumns || allColumns.map(c => c.COLUMN_NAME);
                // 过滤掉主键和自动生成的时间戳
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
        // 使用所有可搜索的列
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
                // 处理下拉框
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

    async function loadAll() {
        if (!tableConfig) {
            await loadConfig();
        }
        const result = await request(API_BASE + '/all', { method: 'GET' });
        if (result && result.code === 200) {
            renderTable(result.data);
            document.getElementById('recordCount').innerHTML = '共 ' + result.data.length + ' 条记录';
        }
    }

    async function search() {
        const field = document.getElementById('searchField').value;
        const keyword = document.getElementById('searchKeyword').value.trim();

        if (!keyword) {
            loadAll();
            return;
        }

        const result = await request(API_BASE + '/search?field=' + encodeURIComponent(field) + '&keyword=' + encodeURIComponent(keyword), { method: 'GET' });
        if (result && result.code === 200) {
            renderTable(result.data);
            document.getElementById('recordCount').innerHTML = '找到 ' + result.data.length + ' 条记录';
            showMessage('找到 ' + result.data.length + ' 条记录', 'success');
        }
    }

    async function save() {
        const id = document.getElementById('editId').value;
        const data = {};

        for (const col of editableColumns) {
            const inputEl = document.getElementById('field_' + col);
            if (inputEl) {
                let value = inputEl.value;
                // 如果是空字符串，不提交该字段
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
            loadAll();
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
            loadAll();
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

    function renderTable(data) {
        if (!data || data.length === 0) {
            document.getElementById('dataTable').innerHTML = '<p>暂无数据</p>';
            return;
        }

        // 获取所有需要显示的列（排除大文本字段）
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
                // 截断过长的内容
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
        await loadAll();
    })();
</script>
</body>
</html>