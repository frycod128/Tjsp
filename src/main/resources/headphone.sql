-- 头戴式耳机商品表
CREATE TABLE headphone (
                           id INT PRIMARY KEY AUTO_INCREMENT COMMENT '耳机ID',
                           model VARCHAR(100) NOT NULL COMMENT '型号名称',
                           brand VARCHAR(50) NOT NULL COMMENT '品牌',
                           driver_size DECIMAL(3,1) COMMENT '驱动单元尺寸(mm)',
                           impedance INT COMMENT '阻抗(Ω)',
                           sensitivity INT COMMENT '灵敏度(dB)',
                           frequency_response VARCHAR(50) COMMENT '频响范围',
                           price DECIMAL(10,2) NOT NULL COMMENT '价格',
                           stock INT DEFAULT 0 COMMENT '库存数量',
                           wireless TINYINT DEFAULT 0 COMMENT '是否无线(1:是 0:否)',
                           noise_cancelling TINYINT DEFAULT 0 COMMENT '是否降噪(1:是 0:否)',
                           create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='头戴式耳机商品表';

-- 插入8条测试数据
INSERT INTO headphone (model, brand, driver_size, impedance, sensitivity, frequency_response, price, stock, wireless, noise_cancelling) VALUES
                                                                                                                                            ('WH-1000XM5', 'Sony', 30.0, 48, 102, '4Hz-40kHz', 2299.00, 45, 1, 1),
                                                                                                                                            ('QuietComfort 45', 'Bose', 40.0, 32, 96, '20Hz-20kHz', 1999.00, 32, 1, 1),
                                                                                                                                            ('HD 660S2', 'Sennheiser', 42.0, 300, 104, '8Hz-41.5kHz', 3299.00, 18, 0, 0),
                                                                                                                                            ('K371', 'AKG', 50.0, 32, 114, '5Hz-40kHz', 899.00, 27, 0, 0),
                                                                                                                                            ('M50x', 'Audio-Technica', 45.0, 38, 99, '15Hz-28kHz', 1099.00, 53, 0, 0),
                                                                                                                                            ('Solo Pro', 'Beats', 40.0, 32, 108, '20Hz-20kHz', 1499.00, 21, 1, 1),
                                                                                                                                            ('SHP9500', 'Philips', 50.0, 32, 101, '12Hz-35kHz', 459.00, 67, 0, 0),
                                                                                                                                            ('Arctis Nova Pro', 'SteelSeries', 40.0, 38, 93, '10Hz-40kHz', 1899.00, 14, 1, 1);