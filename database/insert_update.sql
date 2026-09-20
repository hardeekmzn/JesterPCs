USE jesterpcs;


-- Admin account

UPDATE users
SET role = 'ADMIN'
WHERE username = 'eksrika';


-- Inserts

-- categories

INSERT INTO categories (category_name) VALUES
                                           ('cpu'),
                                           ('gpu'),
                                           ('ram'),
                                           ('storage'),
                                           ('motherboard'),
                                           ('psu'),
                                           ('case'),
                                           ('cooling');


-- Entry-Level

INSERT INTO products (
    category_id,
    product_name,
    brand,
    price,
    description,
    specifications,
    image_url,
    stock_quantity,
    socket_type,
    ram_type,
    ram_speed,
    storage_interface,
    wattage,
    gpu_length,
    cooler_height,
    performance_tier
) VALUES

-- CPU
(
    1,
    'AMD Ryzen 5 5600',
    'AMD',
    18500,
    '6-core processor suitable for entry-level gaming and everyday use.',
    '6 Cores / 12 Threads, 4.4GHz Boost, 65W TDP',
    'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea',
    10,
    'AM4',
    'DDR4',
    3200,
    NULL,
    65,
    NULL,
    NULL,
    'budget'
),

-- GPU
(
    2,
    'AMD Radeon RX 6600',
    'AMD',
    30000,
    'Entry-level 1080p gaming graphics card.',
    '8GB GDDR6, 128-bit, 132W Board Power',
    'https://images.unsplash.com/photo-1591488320449-011701bb6704',
    8,
    NULL,
    NULL,
    NULL,
    NULL,
    132,
    190,
    NULL,
    'budget'
),

-- RAM
(
    3,
    'Corsair Vengeance LPX 16GB',
    'Corsair',
    6500,
    'Reliable DDR4 memory for gaming and everyday workloads.',
    '16GB (2x8GB), DDR4, 3200MHz',
    'https://images.unsplash.com/photo-1562976540-1502c2145186',
    15,
    NULL,
    'DDR4',
    3200,
    NULL,
    NULL,
    NULL,
    NULL,
    'budget'
),

-- Storage
(
    4,
    'Kingston NV2 1TB NVMe',
    'Kingston',
    8500,
    'Fast 1TB NVMe SSD for the operating system, games and applications.',
    '1TB NVMe PCIe 4.0, M.2',
    'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b',
    12,
    NULL,
    NULL,
    NULL,
    'NVMe',
    NULL,
    NULL,
    NULL,
    'budget'
),

-- Motherboard
(
    5,
    'MSI B550M PRO-VDH',
    'MSI',
    14500,
    'Affordable AM4 motherboard with essential features for an entry-level gaming build.',
    'AM4, B550 Chipset, DDR4, Micro-ATX',
    'https://images.unsplash.com/photo-1518770660439-4636190af475',
    8,
    'AM4',
    'DDR4',
    3200,
    'NVMe',
    NULL,
    NULL,
    NULL,
    'budget'
),

-- PSU
(
    6,
    'Corsair CX550',
    'Corsair',
    8500,
    'Reliable 550W power supply for an entry-level gaming system.',
    '550W, 80+ Bronze',
    'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad',
    10,
    NULL,
    NULL,
    NULL,
    NULL,
    550,
    NULL,
    NULL,
    'budget'
),

-- Case
(
    7,
    'DeepCool CC560',
    'DeepCool',
    7500,
    'Affordable airflow-focused ATX case.',
    'ATX Mid Tower, Front Mesh, Tempered Glass Side Panel',
    'https://images.unsplash.com/photo-1587831990711-23ca6441447b',
    10,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    320,
    165,
    'budget'
),

-- Cooling
(
    8,
    'AMD Stock Cooler',
    'AMD',
    0,
    'Stock CPU cooler included with the Ryzen 5 5600.',
    'AMD Stock Cooler',
    'https://images.unsplash.com/photo-1591488320449-011701bb6704',
    10,
    'AM4',
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    55,
    'budget'
);


-- Mid-Tier

INSERT INTO products (
    category_id,
    product_name,
    brand,
    price,
    description,
    specifications,
    image_url,
    stock_quantity,
    socket_type,
    ram_type,
    ram_speed,
    storage_interface,
    wattage,
    gpu_length,
    cooler_height,
    performance_tier
) VALUES

-- CPU
(
    1,
    'AMD Ryzen 5 7600X',
    'AMD',
    28000,
    'High-performance 6-core processor designed for modern gaming and content creation.',
    '6 Cores / 12 Threads, Up to 5.3GHz, 105W TDP',
    'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea',
    10,
    'AM5',
    'DDR5',
    5200,
    NULL,
    105,
    NULL,
    NULL,
    'mid'
),

-- GPU
(
    2,
    'NVIDIA GeForce RTX 5070',
    'NVIDIA',
    95000,
    'High-performance graphics card for 1440p gaming and demanding creative workloads.',
    '12GB GDDR7, DLSS 4, Ray Tracing',
    'https://images.unsplash.com/photo-1591488320449-011701bb6704',
    6,
    NULL,
    NULL,
    NULL,
    NULL,
    250,
    304,
    NULL,
    'mid'
),

-- RAM
(
    3,
    'Corsair Vengeance 32GB DDR5',
    'Corsair',
    18000,
    'Fast DDR5 memory providing strong performance for gaming and multitasking.',
    '32GB (2x16GB), DDR5, 6000MHz',
    'https://images.unsplash.com/photo-1562976540-1502c2145186',
    12,
    NULL,
    'DDR5',
    6000,
    NULL,
    NULL,
    NULL,
    NULL,
    'mid'
),

-- Storage
(
    4,
    'Samsung 990 Pro 1TB NVMe',
    'Samsung',
    18000,
    'High-speed PCIe 4.0 NVMe SSD for fast boot and application loading.',
    '1TB, PCIe 4.0 NVMe, M.2',
    'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b',
    10,
    NULL,
    NULL,
    NULL,
    'NVMe',
    NULL,
    NULL,
    NULL,
    'mid'
),

-- Motherboard
(
    5,
    'MSI B650 Gaming Plus',
    'MSI',
    27000,
    'AM5 motherboard designed for modern Ryzen processors and DDR5 memory.',
    'AM5, B650 Chipset, DDR5, ATX',
    'https://images.unsplash.com/photo-1518770660439-4636190af475',
    8,
    'AM5',
    'DDR5',
    6000,
    'NVMe',
    NULL,
    NULL,
    NULL,
    'mid'
),

-- PSU
(
    6,
    'Corsair RM750e',
    'Corsair',
    13500,
    'Reliable 750W modular power supply suitable for a modern mid-tier gaming PC.',
    '750W, 80+ Gold, Fully Modular',
    'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad',
    10,
    NULL,
    NULL,
    NULL,
    NULL,
    750,
    NULL,
    NULL,
    'mid'
),

-- Case
(
    7,
    'Montech AIR 903 MAX',
    'Montech',
    13500,
    'High-airflow ATX case with spacious interior and strong cooling support.',
    'ATX Mid Tower, High Airflow, Tempered Glass',
    'https://images.unsplash.com/photo-1587831990711-23ca6441447b',
    10,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    400,
    180,
    'mid'
),

-- Cooling
(
    8,
    'DeepCool AK620',
    'DeepCool',
    10500,
    'High-performance air cooler designed for modern high-TDP processors.',
    'Dual Tower, Dual Fan, 160mm Height',
    'https://images.unsplash.com/photo-1591488320449-011701bb6704',
    8,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    160,
    'mid'
);


-- Fully Max

INSERT INTO products (
    category_id,
    product_name,
    brand,
    price,
    description,
    specifications,
    image_url,
    stock_quantity,
    socket_type,
    ram_type,
    ram_speed,
    storage_interface,
    wattage,
    gpu_length,
    cooler_height,
    performance_tier
) VALUES

-- CPU
(
    1,
    'AMD Ryzen 7 9800X3D',
    'AMD',
    75000,
    'High-end gaming processor with 3D V-Cache, designed for flagship gaming systems.',
    '8 Cores / 16 Threads, Up to 5.2GHz, 120W TDP, 3D V-Cache',
    'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea',
    5,
    'AM5',
    'DDR5',
    5600,
    NULL,
    120,
    NULL,
    NULL,
    'max'
),

-- GPU
(
    2,
    'NVIDIA GeForce RTX 5090',
    'NVIDIA',
    385000,
    'Flagship graphics card built for uncompromised 4K gaming and professional workloads.',
    '32GB GDDR7, 512-bit Memory Bus, DLSS 4, Ray Tracing',
    'https://images.unsplash.com/photo-1591488320449-011701bb6704',
    2,
    NULL,
    NULL,
    NULL,
    NULL,
    575,
    304,
    NULL,
    'max'
),

-- RAM
(
    3,
    'Corsair Dominator Titanium 64GB DDR5',
    'Corsair',
    52000,
    'Premium high-speed DDR5 memory for extreme gaming, content creation and multitasking.',
    '64GB (2x32GB), DDR5, 6000MHz',
    'https://images.unsplash.com/photo-1562976540-1502c2145186',
    5,
    NULL,
    'DDR5',
    6000,
    NULL,
    NULL,
    NULL,
    NULL,
    'max'
),

-- Storage
(
    4,
    'Samsung 990 Pro 2TB NVMe',
    'Samsung',
    26000,
    'Premium PCIe 4.0 NVMe SSD offering high capacity and extremely fast storage performance.',
    '2TB, PCIe 4.0 NVMe, M.2',
    'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b',
    5,
    NULL,
    NULL,
    NULL,
    'NVMe',
    NULL,
    NULL,
    NULL,
    'max'
),

-- Motherboard
(
    5,
    'ASUS ROG Crosshair X870E Hero',
    'ASUS',
    90000,
    'Flagship AM5 motherboard designed for high-end Ryzen processors and enthusiast builds.',
    'AM5, X870E Chipset, DDR5, ATX, PCIe 5.0',
    'https://images.unsplash.com/photo-1518770660439-4636190af475',
    3,
    'AM5',
    'DDR5',
    6000,
    'NVMe',
    NULL,
    NULL,
    NULL,
    'max'
),

-- PSU
(
    6,
    'Corsair HX1500i',
    'Corsair',
    42000,
    'High-capacity modular power supply designed for flagship GPUs and high-end PC systems.',
    '1500W, 80+ Platinum, Fully Modular',
    'https://images.unsplash.com/photo-1624705002806-5d72df19c3ad',
    4,
    NULL,
    NULL,
    NULL,
    NULL,
    1500,
    NULL,
    NULL,
    'max'
),

-- Case
(
    7,
    'Lian Li O11 Dynamic EVO XL',
    'Lian Li',
    35000,
    'Large premium chassis designed for extreme hardware, high airflow and custom cooling.',
    'Full Tower, E-ATX Support, Tempered Glass, High Airflow',
    'https://images.unsplash.com/photo-1587831990711-23ca6441447b',
    4,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    455,
    167,
    'max'
),

-- Cooling
(
    8,
    'Corsair iCUE H150i Elite LCD',
    'Corsair',
    22000,
    'Premium 360mm liquid CPU cooler with high-performance cooling for flagship processors.',
    '360mm AIO, Triple 120mm Fans, LCD Display',
    'https://images.unsplash.com/photo-1591488320449-011701bb6704',
    5,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL,
    27,
    'max'
);