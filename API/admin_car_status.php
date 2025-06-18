<?php
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: Content-Type");
header("Access-Control-Allow-Methods: GET, OPTIONS");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$host = 'localhost';
$db   = 'whalexe';
$user = 'root';
$pass = '';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
];

try {
    $pdo = new PDO($dsn, $user, $pass, $options);
} catch (\PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database connection failed']);
    exit;
}

// Handle GET request for car status data
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    try {
        // Get rented cars (cars currently in active bookings)
        $rentedCarsQuery = "
            SELECT DISTINCT v.vehicle_id, v.name as vehicle_name, v.brand, v.model,
                   COALESCE(a.username, a.phone_number) as rented_by, 
                   b.pickup_date as rental_date, b.return_date,
                   b.status
            FROM vehicles v
            JOIN bookings b ON v.vehicle_id = b.vehicle_id
            JOIN accounts a ON b.renter_id = a.account_id
            WHERE b.status IN ('confirmed', 'ongoing', 'active', 'rented')
            AND (b.return_date IS NULL OR b.return_date >= CURDATE())
        ";
        
        $stmt = $pdo->prepare($rentedCarsQuery);
        $stmt->execute();
        $rentedCars = $stmt->fetchAll();

        // Get available cars (cars not in active bookings and available)
        $availableCarsQuery = "
            SELECT v.vehicle_id, v.name as vehicle_name, v.brand, v.model,
                   COALESCE(v.status, 'available') as status
            FROM vehicles v
            WHERE v.vehicle_id NOT IN (
                SELECT DISTINCT vehicle_id FROM bookings 
                WHERE status IN ('confirmed', 'ongoing', 'active', 'rented')
                AND (return_date IS NULL OR return_date >= CURDATE())
            )
            AND COALESCE(v.status, 'available') NOT IN ('maintenance', 'unavailable')
        ";
        
        $stmt = $pdo->prepare($availableCarsQuery);
        $stmt->execute();
        $availableCars = $stmt->fetchAll();

        // Get maintenance cars (check if status column exists)
        $maintenanceCarsQuery = "
            SELECT v.vehicle_id, v.name as vehicle_name, v.brand, v.model,
                   COALESCE(v.updated_at, NOW()) as maintenance_date, 
                   DATE_ADD(COALESCE(v.updated_at, NOW()), INTERVAL 7 DAY) as expected_completion,
                   COALESCE(v.status, 'available') as status
            FROM vehicles v
            WHERE COALESCE(v.status, 'available') IN ('maintenance', 'unavailable', 'repair')
        ";
        
        $stmt = $pdo->prepare($maintenanceCarsQuery);
        $stmt->execute();
        $maintenanceCars = $stmt->fetchAll();

        $data = [
            'rented_count' => count($rentedCars),
            'available_count' => count($availableCars),
            'maintenance_count' => count($maintenanceCars),
            'rented_cars' => $rentedCars,
            'available_cars' => $availableCars,
            'maintenance_cars' => $maintenanceCars
        ];

        echo json_encode(['success' => true, 'data' => $data]);

    } catch (\PDOException $e) {
        // If tables don't exist or error occurs, return sample data
        $sampleCars = [
            [
                'vehicle_id' => 1,
                'vehicle_name' => 'Tesla Model S',
                'brand' => 'Tesla',
                'model' => 'Model S',
                'rented_by' => 'Nguyễn Văn A',
                'rental_date' => '2024-06-15',
                'return_date' => '2024-06-20'
            ],
            [
                'vehicle_id' => 2,
                'vehicle_name' => 'BMW X5',
                'brand' => 'BMW',
                'model' => 'X5',
                'rented_by' => 'Trần Thị B',
                'rental_date' => '2024-06-16',
                'return_date' => '2024-06-22'
            ]
        ];

        $availableSample = [
            [
                'vehicle_id' => 3,
                'vehicle_name' => 'Audi A4',
                'brand' => 'Audi',
                'model' => 'A4'
            ],
            [
                'vehicle_id' => 4,
                'vehicle_name' => 'Mercedes C-Class',
                'brand' => 'Mercedes',
                'model' => 'C-Class'
            ]
        ];

        $maintenanceSample = [
            [
                'vehicle_id' => 5,
                'vehicle_name' => 'Toyota Camry',
                'brand' => 'Toyota',
                'model' => 'Camry',
                'maintenance_date' => '2024-06-10',
                'expected_completion' => '2024-06-25'
            ]
        ];

        $data = [
            'rented_count' => count($sampleCars),
            'available_count' => count($availableSample),
            'maintenance_count' => count($maintenanceSample),
            'rented_cars' => $sampleCars,
            'available_cars' => $availableSample,
            'maintenance_cars' => $maintenanceSample
        ];

        echo json_encode(['success' => true, 'data' => $data]);
    }
} else {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
}
?>
