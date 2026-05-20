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
            width: 80px;
        }
        input, select {
            padding: 5px;
            width: 150px;
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
    </style>
</head>
<body>
<h2>数据管理</h2>

<div id="message" class="message"></div>

<!-- 查询区域 - 默认折叠 -->
<div>
    <div class="collapse-header" onclick="toggleCollapse('search')">▼ 查询</div>
    <div id="searchCollapse" class="collapse-content">
        <div class="form-group">
            <label>字段:</label>
            <select id="searchField">
                <option value="model">型号</option>
                <option value="brand">品牌</option>
            </select>
        </div>
        <div class="form-group">
            <label>关键字:</label>
            <input type="text" id="searchKeyword" placeholder="请输入">
        </div>
        <button onclick="search()">查询</button>
        <button onclick="loadAll()">显示全部</button>
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

<script>
    const API_BASE = '/api/headphone';

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

    async function loadAll() {
        const result = await request(API_BASE + '/all', { method: 'GET' });
        if (result && result.code === 200) {
            renderTable(result.data);
        }
    }

    async function search() {
        const field = document.getElementById('searchField').value;
        const keyword = document.getElementById('searchKeyword').value.trim();
        if (!keyword) {
            loadAll();
            return;
        }
        const result = await request(API_BASE + '/all', { method: 'GET' });
        if (result && result.code === 200) {
            let filtered = result.data.filter(item => {
                const val = item[field] || '';
                return val.toString().toLowerCase().includes(keyword.toLowerCase());
            });
            renderTable(filtered);
            showMessage('找到 ' + filtered.length + ' 条', 'success');
        }
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
            cancelEdit();  // 使用 cancelEdit 重置所有状态
            loadAll();
        } else {
            showMessage('保存失败: ' + (result ? result.msg : '未知错误'), 'error');
        }
    }

    // 取消编辑，恢复到新增模式
    function cancelEdit() {
        clearForm();  // 清空表单
        // 恢复标题
        const addHeader = document.querySelector('#addCollapse').parentElement.querySelector('.collapse-header');
        addHeader.innerHTML = '▼ 新增';
        // 隐藏取消按钮，显示清空按钮
        document.getElementById('cancelBtn').style.display = 'none';
        document.getElementById('clearBtn').style.display = 'inline-block';
    }

    async function edit(id) {
        // 修改标题
        const addHeader = document.querySelector('#addCollapse').parentElement.querySelector('.collapse-header');
        addHeader.innerHTML = '▼ 修改 (ID: ' + id + ')';

        // 显示取消按钮，隐藏清空按钮
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
            cancelEdit(); // 获取失败时恢复状态
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

        // 列定义：key, 名称, 样式类
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

    // 加载数据
    loadAll();
</script>
</body>
</html>